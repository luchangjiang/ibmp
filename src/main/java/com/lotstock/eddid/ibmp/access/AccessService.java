package com.lotstock.eddid.ibmp.access;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.ibmp.common.base.AjaxResult;
import com.lotstock.eddid.ibmp.common.util.ServiceRequestUtils;
import org.springframework.web.client.RestTemplate;

public class AccessService {

    /* 请求模板 */
    private RestTemplate restTemplate;
    /* 接口地址 */
    private String location;
    private String tokenName;

    public AccessService(RestTemplate restTemplate, String location) {
        this.restTemplate = restTemplate;
        this.location = location;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }


    /**
     * 判断是否有权限访问
     *
     * @param userServiceId 用户中心服务ID，可为空
     * @param reqServiceId  请求接口服务ID
     * @param path          接口路径
     * @param token         登录凭证
     * @return 是否有权限访问：未登录、无权访问等
     */
    public AjaxResult<?> hasPermission(String userServiceId, String reqServiceId, String path, String token) {
        JSONObject params = new JSONObject();
        params.put("path", path);
        params.put(tokenName, token);
        return ServiceRequestUtils.request(restTemplate, userServiceId, location, reqServiceId, params);
    }


}
