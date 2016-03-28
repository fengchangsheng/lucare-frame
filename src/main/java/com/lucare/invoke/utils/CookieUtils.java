package com.lucare.invoke.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class CookieUtils {
    public CookieUtils() {
    }

    public static String getCookie(String key, HttpServletRequest request) {
        String value = null;
        if(request != null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                Cookie[] arr$ = cookies;
                int len$ = cookies.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Cookie c = arr$[i$];
                    if(key.equalsIgnoreCase(c.getName())) {
                        value = c.getValue();
                        break;
                    }
                }
            }
        }

        return value;
    }

    public static void setCookie(String key, String value, int timeOut, HttpServletResponse response) {
        if(response != null) {
            Cookie cookie = new Cookie(key, value);
            cookie.setPath("/");
            cookie.setDomain(".touna.cn");
            cookie.setMaxAge(timeOut);
            response.addCookie(cookie);
        }

    }

    public static void clearCookie(String key, HttpServletRequest request, HttpServletResponse response) {
        if(request != null && response != null) {
            Cookie[] cookies = request.getCookies();
            if(cookies != null) {
                Cookie[] arr$ = cookies;
                int len$ = cookies.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    Cookie c = arr$[i$];
                    if(key.equalsIgnoreCase(c.getName())) {
                        c.setMaxAge(0);
                        response.addCookie(c);
                        break;
                    }
                }
            }

        }
    }
}
