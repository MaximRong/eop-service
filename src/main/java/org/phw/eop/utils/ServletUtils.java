package org.phw.eop.utils;

import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

/** Some simple time savers. Note that most are static methods.
 *  <P>
 *  Taken from Core Servlets and JavaServer Pages
 *  from Prentice Hall and Sun Microsystems Press,
 *  http://www.coreservlets.com/.
 *  &copy; 2000 Marty Hall; may be freely used or adapted.
 */

public class ServletUtils {
    public static final String DOCTYPE =
            "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
                    "Transitional//EN\">";

    public static String headWithTitle(String title) {
        return DOCTYPE + "\n" +
                "<HTML>\n" +
                "<HEAD><TITLE>" + title + "</TITLE></HEAD>\n";
    }

    /**
     * 获取浏览器端IP.
     * 参考：http://xuechenyoyo.iteye.com/blog/586007。
     * x-cluster-client-ip/x-forwarded-for/WL-Proxy-Client-IP/Proxy-Client-IP
     * @param request
     * @return
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-real-ip");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return StringUtils.substringBefore(ip, ",");
    }

    /**
      * 判断是否为搜索引擎
      * @param req
      * @return
      */
    public static boolean isRobot(HttpServletRequest req) {
        String ua = req.getHeader("user-agent");
        if (Strings.isBlank(ua)) {
            return false;
        }

        return ua != null && (ua.indexOf("Baiduspider") != -1 || ua.indexOf("Googlebot") != -1
                || ua.indexOf("sogou") != -1
                || ua.indexOf("sina") != -1
                || ua.indexOf("iaskspider") != -1
                || ua.indexOf("ia_archiver") != -1
                || ua.indexOf("Sosospider") != -1
                || ua.indexOf("YoudaoBot") != -1
                || ua.indexOf("yahoo") != -1
                || ua.indexOf("yodao") != -1
                || ua.indexOf("MSNBot") != -1
                || ua.indexOf("spider") != -1
                || ua.indexOf("Twiceler") != -1
                || ua.indexOf("Sosoimagespider") != -1
                || ua.indexOf("naver.com/robots") != -1
                || ua.indexOf("Nutch") != -1
                || ua.indexOf("spider") != -1);
    }

    /**
    
      * 获取用户访问URL中的根域名
    
      * 例如: www.dlog.cn -> dlog.cn
    
      * @param req
    
      * @return
    
      */

    public static String getDomainOfServerName(String host) {
        if (isIPAddr(host)) {
            return null;
        }

        String[] names = Strings.split(host, ".", true);
        int len = names.length;

        if (len == 1) {
            return null;
        }

        if (len == 3) {
            return makeup(names[len - 2], names[len - 1]);
        }

        if (len > 3) {
            String dp = names[len - 2];
            if (dp.equalsIgnoreCase("com") || dp.equalsIgnoreCase("gov") || dp.equalsIgnoreCase("net")
                    || dp.equalsIgnoreCase("edu") || dp.equalsIgnoreCase("org")) {
                return makeup(names[len - 3], names[len - 2], names[len - 1]);
            }
            return makeup(names[len - 2], names[len - 1]);
        }

        return host;
    }

    /**
    
     * 判断字符串是否是一个IP地址
    
     * @param addr
    
     * @return
    
     */

    public static boolean isIPAddr(String addr) {
        if (Strings.isEmpty(addr)) {
            return false;
        }

        String[] ips = Strings.split(addr, ".", false);
        if (ips.length != 4) {
            return false;
        }

        try {
            int ipa = Integer.parseInt(ips[0]);
            int ipb = Integer.parseInt(ips[1]);
            int ipc = Integer.parseInt(ips[2]);
            int ipd = Integer.parseInt(ips[3]);
            return ipa >= 0 && ipa <= 255 && ipb >= 0 && ipb <= 255 && ipc >= 0
                    && ipc <= 255 && ipd >= 0 && ipd <= 255;
        }
        catch (Exception e) {
        }

        return false;
    }

    private static String makeup(String... ps) {
        StringBuilder s = new StringBuilder();
        for (int idx = 0; idx < ps.length; idx++) {
            if (idx > 0) {
                s.append('.');
            }
            s.append(ps[idx]);
        }
        return s.toString();
    }

    /**
    
     * 获取HTTP端口
    
     * @param req
    
     * @return
    
     * @throws MalformedURLException
    
     */

    public static int getHttpPort(HttpServletRequest req) {
        try {
            return new URL(req.getRequestURL().toString()).getPort();
        }
        catch (MalformedURLException excp) {
            return 80;
        }
    }

    /** Read a parameter with the specified name, convert it
     *  to an int, and return it. Return the designated default
     *  value if the parameter doesn't exist or if it is an
     *  illegal integer format.
    */

    public static int getIntParameter(HttpServletRequest request,
            String paramName,
            int defaultValue) {
        String paramString = request.getParameter(paramName);
        int paramValue;
        try {
            paramValue = Integer.parseInt(paramString);
        }
        catch (NumberFormatException nfe) { // null or bad format
            paramValue = defaultValue;
        }
        return paramValue;
    }

    /** Given an array of Cookies, a name, and a default value,
     *  this method tries to find the value of the cookie with
     *  the given name. If there is no cookie matching the name
     *  in the array, then the default value is returned instead.
     */

    public static String getCookieValue(Cookie[] cookies,
            String cookieName,
            String defaultValue) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return defaultValue;
    }

    /** Given an array of cookies and a name, this method tries
     *  to find and return the cookie from the array that has
     *  the given name. If there is no cookie matching the name
     *  in the array, null is returned.
     */

    public static Cookie getCookie(Cookie[] cookies,
            String cookieName) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie;
                }
            }
        }
        return null;
    }

    /** Given a string, this method replaces all occurrences of
     *  '<' with '&lt;', all occurrences of '>' with
     *  '&gt;', and (to handle cases that occur inside attribute
     *  values), all occurrences of double quotes with
     *  '&quot;' and all occurrences of '&' with '&amp;'.
     *  Without such filtering, an arbitrary string
     *  could not safely be inserted in a Web page.
     */

    public static String filter(String input) {
        StringBuffer filtered = new StringBuffer(input.length());
        char c;
        for (int i = 0; i < input.length(); i++) {
            c = input.charAt(i);
            if (c == '<') {
                filtered.append("&lt;");
            }
            else if (c == '>') {
                filtered.append("&gt;");
            }
            else if (c == '"') {
                filtered.append("&quot;");
            }
            else if (c == '&') {
                filtered.append("&amp;");
            }
            else {
                filtered.append(c);
            }
        }
        return filtered.toString();
    }

    public static void printHead(HttpServletRequest request, PrintWriter out) {
        out.println();
        out.println("Request headers:");
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            String value = request.getHeader(name);
            out.println("  " + name + " : " + value);
        }
    }

    public static void printParameters(HttpServletRequest request, PrintWriter out) {
        out.println();
        out.println("Parameters:");
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = (String) paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            out.println("    " + name + ":");
            for (String value : values) {
                out.println("      " + value);
            }
        }
    }

    public static void printSchema(HttpServletRequest request, PrintWriter out) {
        out.println("getCharacterEncoding: " + request.getCharacterEncoding());
        out.println("getContentLength: " + request.getContentLength());
        out.println("getContentType: " + request.getContentType());
        out.println("getProtocol: " + request.getProtocol());
        out.println("getRemoteAddr: " + request.getRemoteAddr());
        out.println("getRemoteHost: " + request.getRemoteHost());
        out.println("getScheme: " + request.getScheme());
        out.println("getServerName: " + request.getServerName());
        out.println("getServerPort: " + request.getServerPort());
        out.println("getAuthType: " + request.getAuthType());
        out.println("getMethod: " + request.getMethod());
        out.println("getPathInfo: " + request.getPathInfo());
        out.println("getPathTranslated: " + request.getPathTranslated());
        out.println("getQueryString: " + request.getQueryString());
        out.println("getRemoteUser: " + request.getRemoteUser());
        out.println("getRequestURI: " + request.getRequestURI());
        out.println("getServletPath: " + request.getServletPath());
    }

    public static void printCookies(HttpServletRequest request, PrintWriter out) {
        out.println();
        out.println("Cookies:");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                out.println("  " + name + " : " + value);
            }
        }
    }
}
