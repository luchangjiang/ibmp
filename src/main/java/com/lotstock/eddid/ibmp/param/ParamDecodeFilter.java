package com.lotstock.eddid.ibmp.param;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.ibmp.constant.Constants;
import com.lotstock.eddid.ibmp.common.base.BaseFilter;
import com.lotstock.eddid.ibmp.common.base.BaseResultEnum;
import com.lotstock.eddid.ibmp.common.base.ResultException;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;

import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;


/**
 * 参数解码
 */
@Slf4j
public class ParamDecodeFilter extends BaseFilter {
    private int order = Integer.MAX_VALUE;
    private ParamManager paramManager;
    private ParamProperties paramProperties;
    private final static String DATA_NAME = "d";

    public ParamDecodeFilter(ParamManager paramManager, ParamProperties paramProperties) {
        this.paramManager = paramManager;
        this.paramProperties = paramProperties;
    }

    @Override
    public String filterType() {
        // 前置
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return order;
    }

    @Override
    public boolean shouldFilter() {
        return super.shouldFilter(paramProperties.getPathPatterns());
    }

    @Override
    public Object run() {
        RequestContext ctx = getCurrentContext();
        HttpServletRequest request = getCurrentRequest();
        try {
            String newBody = getData(request);
            if (newBody == null) {
                return success();
            }

            byte[] newBodyBytes = newBody.getBytes(request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8");
            ctx.setRequest(new HttpServletRequestWrapper(request) {
                @Override
                public ServletInputStream getInputStream() throws IOException {
                    return new ServletInputStreamWrapper(newBodyBytes);
                }

                @Override
                public int getContentLength() {
                    return newBodyBytes.length;
                }

                @Override
                public long getContentLengthLong() {
                    return newBodyBytes.length;
                }
            });
            return success();
        } catch (ResultException e) {
            return fail(JSONObject.toJSONString(e.getResultModel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BaseResultEnum.PARAM_FAIL.getResultJsonString();
    }

    private String getData(HttpServletRequest request) throws Exception {
        String method = request.getMethod();
        String contentType = request.getContentType();
        String data = null;

        String charset = request.getCharacterEncoding();
        if (charset == null) {
            charset = Constants.DEFAULT_CHARSET;
        }

        if ("GET".equals(method) || ("POST".equals(method) && contentType != null && contentType.startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE))) {
            data = request.getParameter(DATA_NAME);
            if (data != null && data.length() > 0) {
                data = URLEncoder.encode(data, charset);
                data = URLDecoder.decode(data, charset);
            }
        } else {
            //        String body = IOUtils.readToString(request.getInputStream());
            String body = StreamUtils.copyToString(request.getInputStream(), Charset.forName(charset));
            if (body == null || body.trim().length() == 0 || "{}".equals(body)) {
                // 参数为空，不处理解密
                log.debug("请求方式：{}，请求类型：{}, 请求数据：{}", method, contentType, body);
                return body;
            } else if (contentType != null && contentType.startsWith(MediaType.APPLICATION_JSON_VALUE) && body.startsWith("{")) {
                JSONObject jsonBody = JSONObject.parseObject(body);
                if (jsonBody.containsKey(DATA_NAME)) {
                    // IOS兼容：{"d": "xxx"}
                    data = JSONObject.parseObject(body).getString(DATA_NAME);
                } else {
                    // 参数未加密，直接返回失败
                    log.debug("参数未使用加密：{}", body);
                    throw new ResultException(BaseResultEnum.PARAM_RESOLVE_FAIL.getResultModel());
                }
            } else {
                data = body;
            }

        }
        log.debug("请求方式：{}，请求类型：{}, 请求数据：{}", method, contentType, data);
        if (data == null || data.trim().length() == 0) {
            return data;
        }
        String newData = paramManager.getDecodeParamString(request, data);
        if (newData != null) {
            // 删除加密填充
            newData = newData.trim();
        }
        log.debug("解密数据：{}", newData);
        return newData;
    }


}
