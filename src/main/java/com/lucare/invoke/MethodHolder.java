package com.lucare.invoke;

import com.lucare.invoke.annotation.LoginStateType;

import java.lang.reflect.Method;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public class MethodHolder {
    private Method method;
    private Class<?> returnType;
    private Object[] argsMarkers;
    private String[] argsNames;
    private LoginStateType loginState;

    public MethodHolder(Method method, Class<?> returnType, Object[] argsMarkers, String[] argsNames, LoginStateType loginState) {
        this.method = method;
        this.returnType = returnType;
        this.argsMarkers = argsMarkers;
        this.argsNames = argsNames;
        this.loginState = loginState;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public Object[] getArgsMarkers() {
        return argsMarkers;
    }

    public String[] getArgsNames() {
        return argsNames;
    }

    public LoginStateType getLoginState() {
        return loginState;
    }
}
