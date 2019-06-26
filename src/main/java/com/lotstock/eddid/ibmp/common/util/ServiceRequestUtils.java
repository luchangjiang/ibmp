package com.lotstock.eddid.ibmp.common.util;

import com.alibaba.fastjson.JSONObject;
import com.lotstock.eddid.ibmp.common.base.AjaxResult;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

public class ServiceRequestUtils {

//    private static Class<? extends ResultModel> resultType = new ResultModel<HashMap>().getClass();

    public static AjaxResult request(RestTemplate restTemplate, String confServiceId, String confLocation, String reqServiceId, JSONObject params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        String body = null;
        if (params == null) {
            params = new JSONObject();
        }
        if (reqServiceId != null && !reqServiceId.equals(confServiceId)) {
            params.put("serviceId", reqServiceId);
        }
        body = params.toJSONString();
        HttpEntity<String> entity = new HttpEntity<String>(body, headers);

        String url = null;
        if (confLocation.startsWith("http://")) {
            // 统一用户Url或服务
            url = confLocation;
        } else {
            if (confServiceId == null || confServiceId.trim().length() == 0) {
                confServiceId = reqServiceId;
            }
            // 单个用户服务
            url = String.format("http://%s%s", confServiceId, confLocation);
        }
        return restTemplate.postForObject(url, entity, AjaxResult.class);
    }


}
