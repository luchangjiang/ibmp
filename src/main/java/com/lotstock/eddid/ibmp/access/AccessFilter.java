package com.lotstock.eddid.ibmp.access;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SERVICE_ID_KEY;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.lotstock.eddid.ibmp.common.base.AjaxResult;
import com.lotstock.eddid.ibmp.common.base.BaseFilter;
import com.lotstock.eddid.ibmp.common.base.BaseResultEnum;
import com.lotstock.eddid.ibmp.common.util.WebServletUtils;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.context.RequestContext;

/**
 * 账户登录、权限统一处理
 */
@Slf4j
public class AccessFilter extends BaseFilter {
	
    /* 账号服务 */
    private AccessService accessService;
    private AccessProperties accessProperties;
    private int filterOrder = 7;
    private boolean isCookie = false;
    private PathMatcher pathMatcher = new AntPathMatcher();

    public AccessFilter(AccessService accessService) {
        this.accessService = accessService;
    }

    public AccessFilter(RestTemplate restTemplate, String location) {
        this(new AccessService(restTemplate, location));
    }

    public void setAccessProperties(AccessProperties accessProperties) {
        this.accessProperties = accessProperties;
        this.accessService.setTokenName(accessProperties.getTokenName());
    }

    public void setFilterOrder(int filterOrder) {
        if (filterOrder <= 5) {
            throw new RuntimeException("filterOrder 必须大于5");
        }
        this.filterOrder = filterOrder;
    }


    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 此处大于5，可以拿到系统PreDecorationFilter设置的内容
        return this.filterOrder;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = getCurrentContext();
        Object serviceId = ctx.get(SERVICE_ID_KEY);
        if (serviceId == null || getCurrentRequest().getRequestURI().equals("")) {
            // 非服务ID调用方式不拦截
            return false;
        }
        return super.shouldFilter(accessProperties.getPathPatterns());
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = getCurrentContext();
            HttpServletRequest request = getCurrentRequest();

            String token = getToken(request);

            String serviceId = (String) ctx.get(SERVICE_ID_KEY);
            String path = (String) ctx.get(REQUEST_URI_KEY);

            AjaxResult<?> rm = accessService.hasPermission(accessProperties.getUserServiceId(), serviceId, path, token);
            if (rm == null) {
                // 获取信息失败
                return fail(BaseResultEnum.UNAVAILABLE.getResultJsonString());
            }
            if (!rm.isSuccess()) {
                // 登录失效或者无权访问
                return fail(JSONObject.toJSONString(rm));
            }

            Map result = (Map) rm.getResult();
            if (result != null) {
                // TODO 配置
                Map<String, String> headers = ctx.getZuulRequestHeaders();
                headers.put(accessProperties.getUserIdName(), result.get(accessProperties.getUserIdName()) + "");
            }

            return success();
        } catch (Exception e) {
            log.error("请求异常", e);
            return fail(BaseResultEnum.UNAVAILABLE.getResultJsonString());
        }
    }

    private String getToken(HttpServletRequest request) {
        String tokenName = accessProperties.getTokenName();
        String token = request.getHeader(tokenName);
        if (token == null || "".equals(token.trim())) {
            // 文件下载: Token获取不到，从参数中获取
            token = request.getParameter(tokenName);
        }
        if (isCookie && (token == null || "".equals(token.trim()))) {
            token = WebServletUtils.getCookieValue(request, tokenName);
        }
        return token;
    }


}
