package com.lucare.invoke.utils;


import com.lucare.common.utils.CommonUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class NetUtils {

    private static final String[] __http__ip__key__ = new String[]{"HTTP_X_FORWARDED_FOR", "http_x_forwarded_for", "X-Forwarded-For", "x-forwarded-for", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP", "http_client_ip"};

    public NetUtils() {
    }

    public static InetAddress getAddress() {
        InetAddress inetAddress = null;

        try {
            Enumeration e = NetworkInterface.getNetworkInterfaces();

            while(e.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface)e.nextElement();
                if(!networkInterface.isLoopback() && !networkInterface.isVirtual() && networkInterface.isUp()) {
                    Enumeration addresses = networkInterface.getInetAddresses();
                    inetAddress = addresses.hasMoreElements()?(InetAddress)addresses.nextElement():InetAddress.getLocalHost();
                }
            }

            return inetAddress;
        } catch (Exception var4) {
            throw CommonUtils.illegalStateException(var4);
        }
    }

    public static String getRequestIP(HttpServletRequest request) {
        if(request == null) {
            return null;
        } else {
            String ip = null;
            String[] arr$ = __http__ip__key__;
            int len$ = arr$.length;

            for(int i$ = 0; i$ < len$; ++i$) {
                String key = arr$[i$];
                ip = getIPFromHeader(key, request);
                if(!CommonUtils.isEmpty(ip)) {
                    break;
                }
            }

            if(CommonUtils.isEmpty(ip)) {
                ip = request.getRemoteAddr();
            }

            if(!CommonUtils.isEmpty(ip) && ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }

            ip = CommonUtils.emptyIfNull(ip);
            if(CommonUtils.isEmpty(ip)) {
                ip = "unknown";
            }

            return ip.trim();
        }
    }

    public static String getIPFromHeader(String key, HttpServletRequest request) {
        if(request == null) {
            return null;
        } else {
            String ip = request.getHeader(key);
            return "unknown".equalsIgnoreCase(ip)?null:ip;
        }
    }
}
