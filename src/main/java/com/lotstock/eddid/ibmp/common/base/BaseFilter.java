package com.lotstock.eddid.ibmp.common.base;

import com.lotstock.eddid.ibmp.common.util.IOUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;


public abstract class BaseFilter extends ZuulFilter {

    private PathMatcher pathMatcher = new AntPathMatcher();

    /* 成功标记 */
    public final static String REQUEST_OK = "ok";

    protected Object failMsg(String msg) {
        return fail(String.format("{\"code\": %s, \"msg\": \"%s\"}", -1, msg));
    }

    protected Object fail(String responseBody) {
        RequestContext ctx = getCurrentContext();
        ctx.set(REQUEST_OK, false);
        ctx.setSendZuulResponse(false); // 不再路由
        if (responseBody != null) {
            ctx.getResponse().setCharacterEncoding("UTF-8");
            ctx.getResponse().setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
            ctx.setResponseBody(responseBody);
            ctx.setResponseStatusCode(HttpStatus.OK.value());
        }
        return null;
    }

    protected Object success() {
        RequestContext ctx = getCurrentContext();
        // 验证登录成功，转换参数
        ctx.set(REQUEST_OK, true);
//        ctx.setResponseStatusCode(HttpStatus.OK.value());
        ctx.setSendZuulResponse(true);
        return null;
    }

    protected boolean isSuccess() {
        RequestContext ctx = getCurrentContext();
        Object success = ctx.get(REQUEST_OK);
        return success == null ? true : Boolean.parseBoolean(success.toString());
    }

    protected RequestContext getCurrentContext() {
        return RequestContext.getCurrentContext();
    }

    protected HttpServletRequest getCurrentRequest() {
        return getCurrentContext().getRequest();
    }

    protected String getRequestBody() throws IOException {
        InputStream in = null;
        try {
            RequestContext ctx = getCurrentContext();
            // 校验成功
            in = (InputStream) ctx.get("requestEntity");
            if (in == null) {
                in = ctx.getRequest().getInputStream();
            }
            return StreamUtils.copyToString(in, Charset.forName("UTF-8"));
        } finally {
            IOUtils.close(in);
        }
    }

    protected void setRequestBody(final String body) {
        final byte[] bodyBytes = body.getBytes();
        final RequestContext ctx = getCurrentContext();
        ctx.setRequest(new HttpServletRequestWrapper(ctx.getRequest()) {
            @Override
            public ServletInputStream getInputStream() throws IOException {
                return new ServletInputStreamWrapper(bodyBytes);
            }

            @Override
            public int getContentLength() {
                return bodyBytes.length;
            }

            @Override
            public long getContentLengthLong() {
                return bodyBytes.length;
            }
        });
    }

//    private String zuulServletPath = "/zuul";
//
//    public void setZuulServletPath(String zuulServletPath) {
//        this.zuulServletPath = zuulServletPath;
//    }
//
//    protected String getServiceUri() {
//        String requestURI = getCurrentRequest().getRequestURI();
//        if (requestURI.startsWith(zuulServletPath)) {
//            return requestURI.substring(zuulServletPath.length());
//        }
//        return requestURI;
//    }

    protected String getRouteRequestUri() {
        return (String) getCurrentContext().get(REQUEST_URI_KEY);
    }

    protected boolean shouldFilter(List<String> pathPatterns) {
        if (!isSuccess()) {
            return false;
        }
        // SpringCloud+ZUUL跨域请求中的OPTIONS请求处理，参考https://www.cnblogs.com/justbeginning/p/9334109.html
        if (getCurrentRequest().getMethod().equals("OPTIONS")) {
            return false;
        }
        if (pathPatterns != null) {
            String requestURI = getCurrentRequest().getRequestURI();
            for (String pattern : pathPatterns) {
                if (pathMatcher.match(pattern, requestURI)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public boolean shouldFilter() {
        return shouldFilter(null);
    }


}