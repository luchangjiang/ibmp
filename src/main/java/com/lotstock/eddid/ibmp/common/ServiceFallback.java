package com.lotstock.eddid.ibmp.common;

import com.lotstock.eddid.ibmp.common.base.BaseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.ZuulFallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dell on 2018-05-21.
 * 统一异常：服务调用熔断
 */
@Slf4j
public class ServiceFallback implements ZuulFallbackProvider {

    private String route;

    public ServiceFallback() {
        // 代表所有的路由都适配该设置
        this.route = "*";
    }

    public ServiceFallback(String route) {
        this.route = route;
    }

    @Override
    public String getRoute() {
        return this.route;
    }

    @Override
    public ClientHttpResponse fallbackResponse() {
        return new ClientHttpResponse() {
            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
//                httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json;charset=" + System.getProperty("file.encoding", "UTF-8"));
                return httpHeaders;
            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream(BaseResultEnum.UNAVAILABLE.getResultJsonString().getBytes("UTF-8"));
            }

            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }

            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();
            }

            @Override
            public void close() {

            }
        };
    }


}
