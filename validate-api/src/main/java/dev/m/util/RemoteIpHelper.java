package dev.m.util;

import lombok.experimental.UtilityClass;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public class RemoteIpHelper {

    private static final String UNKNOWN = "unknown";

    public static String getFullUrlApi(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        return scheme + "://" + serverName + ":" + serverPort + contextPath + servletPath;
    }

    public static String getRemoteIpFromNew(HttpServletRequest request) {
        String ip = null;

        // Lấy địa chỉ IP từ các header
        ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        // Nếu không tìm thấy từ các header, lấy địa chỉ IP từ địa chỉ mặc định
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return ip;
    }

    public static String getRemoteIpFrom(HttpServletRequest request) {
        String ip = null;
        int tryCount = 1;

        while (!isIpFound(ip) && tryCount <= 6) {
            switch (tryCount) {
                case 1:
                    ip = request.getHeader("X_FORWARDED_FOR");
                    break;
                case 2:
                    ip = request.getHeader("PROXY_CLIENT_IP");
                    break;
                case 3:
                    ip = request.getHeader("WL_PROXY_CLIENT_IP");
                    break;
                case 4:
                    ip = request.getHeader("HTTP_CLIENT_IP");
                    break;
                case 5:
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                    break;
                default:
                    ip = request.getRemoteAddr();
            }
            tryCount++;
        }
        return ip;
    }

    private static boolean isIpFound(String ip) {
        return ip != null && ip.length() > 0 && !UNKNOWN.equalsIgnoreCase(ip);
    }
}