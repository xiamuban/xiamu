package com.xiamu.spring.utils;

import com.xiamu.spring.annotation.AccessLimit;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Author xianghui.luo
 * @Date 2025/2/7 14:40
 * @Description 访问控制处理工具类
 */
public class RateLimitUtil {

    /**
     * 获取唯一key根据注解类型
     * <p>
     * 规则 资源名:业务key:perSecond
     *
     * @param method
     * @param request
     * @return
     */
    public static String generateCacheKey(HandlerMethod method, HttpServletRequest request) {
        //获取方法上的注解
        AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
        StringBuffer cacheKey = new StringBuffer(accessLimit.name() + ":");
        String ipAddr = getIpAddr(request);
        switch (accessLimit.limitKeyType()) {
            case IPADDR:
                cacheKey.append(ipAddr + ":");
                break;
            case IPADDRACCOUNT:
                cacheKey.append(ipAddr + ":");
                cacheKey.append(request.getSession().getAttribute("account") + ":");
                break;
            case ALL:
                break;
        }
        cacheKey.append(accessLimit.limit());
        return cacheKey.toString();
    }

    public static String generateSimpleCacheKey(HandlerMethod method, HttpServletRequest request) {
        //获取方法上的注解
        AccessLimit accessLimit = method.getMethodAnnotation(AccessLimit.class);
        return String.join(":",accessLimit.name() ,String.valueOf(accessLimit.limit()));
    }

    /**
     * 获取客户端IP地址
     *
     * @param request 请求
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip)) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ip = inet.getHostAddress();
            }
        }
        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    public static double getCacheKeyPerSecond(String key) {
        String[] ruleStr = key.split(":");
        String perSecond = ruleStr[ruleStr.length-1];
        return Double.parseDouble(perSecond);
    }

}