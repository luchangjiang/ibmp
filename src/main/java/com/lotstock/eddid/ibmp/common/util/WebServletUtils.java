package com.lotstock.eddid.ibmp.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ServletRequest 工具类
 */
public class WebServletUtils {

    private static final Logger log = LoggerFactory.getLogger(WebServletUtils.class);

    private static boolean isEmpty(String str) {
        return str == null || str.trim().length() == 0;
    }

    /**
     * 获取客户端IP
     *
     * @param request 请求
     * @return 客户端IP：127.0.0.1
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (null == request) {
            return null;
        }
        String ip = request.getHeader("X-Forwarded-For");
        if (!isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (isEmpty(ip) || "unKnown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 获取操作系统和浏览器信息
     *
     * @param request 请求
     * @return 操作系统和浏览器信息（Linux：Netscape-?）
     */
    public static String getOsAndBrowser(HttpServletRequest request) {
        String browserDetails = request.getHeader("User-Agent");
        String userAgent = browserDetails;
        String user = userAgent.toLowerCase();

        String os = "";
        String browser = "";

        //=================OS Info=======================
        if (userAgent.toLowerCase().indexOf("windows") >= 0) {
            os = "Windows";
        } else if (userAgent.toLowerCase().indexOf("mac") >= 0) {
            os = "Mac";
        } else if (userAgent.toLowerCase().indexOf("x11") >= 0) {
            os = "Unix";
        } else if (userAgent.toLowerCase().indexOf("android") >= 0) {
            os = "Android";
        } else if (userAgent.toLowerCase().indexOf("iphone") >= 0) {
            os = "IPhone";
        } else {
//            os = "UnKnown, More-Info: " + userAgent;
            os = "UnKnown";
        }
        //===============Browser===========================
        if (user.contains("edge")) {
            browser = (userAgent.substring(userAgent.indexOf("Edge")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("msie")) {
            String substring = userAgent.substring(userAgent.indexOf("MSIE")).split(";")[0];
            browser = substring.split(" ")[0].replace("MSIE", "IE") + "-" + substring.split(" ")[1];
        } else if (user.contains("safari") && user.contains("version")) {
            browser = (userAgent.substring(userAgent.indexOf("Safari")).split(" ")[0]).split("/")[0]
                    + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
        } else if (user.contains("opr") || user.contains("opera")) {
            if (user.contains("opera")) {
                browser = (userAgent.substring(userAgent.indexOf("Opera")).split(" ")[0]).split("/")[0]
                        + "-" + (userAgent.substring(userAgent.indexOf("Version")).split(" ")[0]).split("/")[1];
            } else if (user.contains("opr")) {
                browser = ((userAgent.substring(userAgent.indexOf("OPR")).split(" ")[0]).replace("/", "-"))
                        .replace("OPR", "Opera");
            }

        } else if (user.contains("chrome")) {
            browser = (userAgent.substring(userAgent.indexOf("Chrome")).split(" ")[0]).replace("/", "-");
        } else if ((user.indexOf("mozilla/7.0") > -1) || (user.indexOf("netscape6") != -1) ||
                (user.indexOf("mozilla/4.7") != -1) || (user.indexOf("mozilla/4.78") != -1) ||
                (user.indexOf("mozilla/4.08") != -1) || (user.indexOf("mozilla/3") != -1)) {
            browser = "Netscape-?";

        } else if (user.contains("firefox")) {
            browser = (userAgent.substring(userAgent.indexOf("Firefox")).split(" ")[0]).replace("/", "-");
        } else if (user.contains("rv")) {
            String IEVersion = (userAgent.substring(userAgent.indexOf("rv")).split(" ")[0]).replace("rv:", "-");
            browser = "IE" + IEVersion.substring(0, IEVersion.length() - 1);
        } else {
//            browser = "UnKnown, More-Info: " + userAgent;
            browser = "UnKnown";
        }

        return os + "：" + browser;
    }

    /**
     * 获取浏览器名称
     *
     * @param request 请求
     * @return 浏览器名称：ie7、opera等
     */
    public static String getBrowserName(HttpServletRequest request) {
        String agent = request.getHeader("User-Agent").toLowerCase();
        if (agent.indexOf("msie 7") > 0) {
            return "ie7";
        } else if (agent.indexOf("msie 8") > 0) {
            return "ie8";
        } else if (agent.indexOf("msie 9") > 0) {
            return "ie9";
        } else if (agent.indexOf("msie 10") > 0) {
            return "ie10";
        } else if (agent.indexOf("msie") > 0) {
            return "ie";
        } else if (agent.indexOf("opera") > 0) {
            return "opera";
        } else if (agent.indexOf("opera") > 0) {
            return "opera";
        } else if (agent.indexOf("firefox") > 0) {
            return "firefox";
        } else if (agent.indexOf("webkit") > 0) {
            return "webkit";
        } else if (agent.indexOf("gecko") > 0 && agent.indexOf("rv:11") > 0) {
            return "ie11";
        } else {
            return "Others";
        }
    }

    /**
     * 获取当前请求HOST
     *
     * @param request 请求
     * @return http://localhost:80/xxx
     */
    public static String getCurrentHost(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    /**
     * 判断是否为AJAX请求
     *
     * @param request HttpServletRequest
     * @return 是否为AJAX请求
     */
    public static boolean isAjaxRequest(HttpServletRequest request) {
        return StringUtils.equalsIgnoreCase(request.getHeader("X-Requested-With"), "XMLHttpRequest");
    }


    /**
     * 获取请求参数
     *
     * @param request 请求
     * @return 参数
     */
    public static String getRequestParams(HttpServletRequest request) throws IOException {
        if ("get".equalsIgnoreCase(request.getMethod())) {
            return request.getQueryString();
        } else {
            return IOUtils.readToString(request.getInputStream());
        }
    }

    /**
     * 设置Session属性
     *
     * @param request 请求
     * @param name    Session属性名
     * @param value   Session属性值
     */
    public static void setSessionAttribute(HttpServletRequest request, String name, Object value) {
        request.getSession().setAttribute(name, value);
    }

    /**
     * 获取Session属性值
     *
     * @param request 请求
     * @param name    Session属性名
     * @param <T>     Session属性类型
     * @return Session属性值
     */
    public static <T> T getSessionAttribute(HttpServletRequest request, String name) {
        return (T) request.getSession().getAttribute(name);
    }


    /**
     * 将请求参数转换成Map
     *
     * @param queryString 请求参数
     * @return Map对象
     */
    public static Map<String, String> parseQueryString(String queryString) {
        String param = "";
        try {
            param = URLDecoder.decode(queryString, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, String> result = new HashMap<>();
        String[] params = param.split("&");
        for (String s : params) {
            Integer index = s.indexOf("=");
            result.put(s.substring(0, index), s.substring(index + 1));
        }
        return result;
    }

    /**
     * 将请求参数转换成Map
     *
     * @param request HttpServletRequest
     * @return Map对象
     */
    public static Map<String, String> parseQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        return parseQueryString(request.getQueryString());
    }


    /**
     * 参数解析
     *
     * @param query    查询字符串
     * @param encoding 编码格式
     * @return 参数
     */
    public static Map<String, String> parse(String query, String encoding) {
        Charset charset;
        if (StringUtils.isNotEmpty(encoding)) {
            charset = Charset.forName(encoding);
        } else {
            charset = Charset.forName("UTF-8");
        }
        List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(query, charset);
        Map<String, String> parameterMap = new HashMap<>();
        for (NameValuePair nameValuePair : nameValuePairs) {
            parameterMap.put(nameValuePair.getName(), nameValuePair.getValue());
        }
        return parameterMap;
    }

    /**
     * 解析参数
     *
     * @param query 查询字符串
     * @return 参数
     */
    public static Map<String, String> parse(String query) {
        return parse(query, null);
    }


    /**
     * 重定向
     *
     * @param request          HttpServletRequest
     * @param response         HttpServletResponse
     * @param url              URL
     * @param contextRelative  是否相对上下文路径
     * @param http10Compatible 是否兼容HTTP1.0
     */
    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url, boolean contextRelative, boolean http10Compatible) {
        StringBuilder targetUrl = new StringBuilder();
        if (contextRelative && url.startsWith("/")) {
            targetUrl.append(request.getContextPath());
        }
        targetUrl.append(url);
        String encodedRedirectURL = response.encodeRedirectURL(targetUrl.toString());
        if (http10Compatible) {
            try {
                response.sendRedirect(encodedRedirectURL);
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            response.setStatus(303);
            response.setHeader("Location", encodedRedirectURL);
        }
    }

    /**
     * 重定向
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @param url      URL
     */
    public static void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
        sendRedirect(request, response, url, true, true);
    }


    /**
     * 添加cookie
     *
     * @param response HttpServletResponse
     * @param name     Cookie名称
     * @param value    Cookie值
     * @param maxAge   有效期(单位: 秒)
     * @param path     路径
     * @param domain   域
     * @param secure   是否启用加密
     */
    public static void addCookie(HttpServletResponse response, String name, String value, Integer maxAge, String path, String domain, Boolean secure) {
        Cookie cookie = new Cookie(name, value);
        if (maxAge != null) {
            cookie.setMaxAge(maxAge);
        }
        if (StringUtils.isNotEmpty(path)) {
            cookie.setPath(path);
        }
        if (StringUtils.isNotEmpty(domain)) {
            cookie.setDomain(domain);
        }
        if (secure != null) {
            cookie.setSecure(secure);
        }
        response.addCookie(cookie);
    }

    /**
     * 添加Cookie信息
     *
     * @param response 响应
     * @param name     Cookie名称
     * @param value    Cookie值
     * @param path     路径
     * @param maxAge   过期时间
     * @param domain   域
     */
    public static void addCookie(HttpServletResponse response, String name, String value, Integer maxAge, String path, String domain) {
        addCookie(response, name, value, maxAge, path, domain, null);
    }

    /**
     * 添加Cookie信息
     *
     * @param response 响应
     * @param name     Cookie名称
     * @param value    Cookie值
     * @param maxAge   过期时间
     * @param domain   域
     */
    public static void addCookie(HttpServletResponse response, String name, String value, Integer maxAge, String domain) {
        addCookie(response, name, value, maxAge, "/", domain, null);
    }

    /**
     * 获取Cookie信息
     *
     * @param request HttpServletRequest
     * @param name    Cookie名称
     * @return Cookie值，若不存在则返回null
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * 获取Cookie值
     *
     * @param request HttpServletRequest
     * @param name    Cookie名称
     * @return Cookie值，若不存在则返回null
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        return cookie != null ? cookie.getValue() : null;
    }


    /**
     * 移除cookie
     *
     * @param response HttpServletResponse
     * @param name     Cookie名称
     * @param path     路径
     * @param domain   域
     */
    public static void removeCookie(HttpServletResponse response, String name, String path, String domain) {
        addCookie(response, name, null, 0, path, domain);
    }

    /**
     * 获取HttpServletRequest
     *
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null && requestAttributes instanceof ServletRequestAttributes ? ((ServletRequestAttributes) requestAttributes).getRequest() : null;
    }

    /**
     * 获取HttpServletResponse
     *
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return requestAttributes != null && requestAttributes instanceof ServletRequestAttributes ? ((ServletRequestAttributes) requestAttributes).getResponse() : null;
    }

}
