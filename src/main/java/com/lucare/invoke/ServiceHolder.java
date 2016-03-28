package com.lucare.invoke;

import com.lucare.common.utils.CommonUtils;
import com.lucare.common.utils.GenericUtils;
import com.lucare.invoke.annotation.LoginState;
import com.lucare.invoke.annotation.LoginStateType;
import com.lucare.invoke.assembly.ClassFile;
import com.lucare.invoke.assembly.LocalVariableAttribute;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Method;

/**
 * Created by Lucare.Feng on 2016/3/26.
 */
public class ServiceHolder {
    private AtomicInteger invokeCount = new AtomicInteger(0);
    private final String name;
    private final Class<?> apiClass;
    private final Object service;
    private final Map<String, MethodHolder> holderMap;

    public ServiceHolder(String name, Class<?> apiClass, Object service) {
        this.name = name;
        this.apiClass = apiClass;
        this.service = service;
        this.holderMap = this.analyzeServiceMethods();
    }

    private Map<String, MethodHolder> analyzeServiceMethods() {
        HashMap holderMap = new HashMap();
        Method[] apiMethods = this.apiClass.getMethods();
        Method[] implMethods = this.service.getClass().getMethods();
        LoginStateType defStateType = LoginStateType.MUST_LOGIN;
        LoginState loginState = (LoginState) this.apiClass.getAnnotation(LoginState.class);
        if (loginState != null) {
            defStateType = loginState.stateType();
        }

        Method[] arr = apiMethods;
        int len = apiMethods.length;

        for (int i = 0; i < len; i++) {
            Method apiMethod = arr[i];
            LoginStateType stateType = defStateType;
            if (apiMethod.isAnnotationPresent(LoginState.class)) {
                stateType = ((LoginState) apiMethod.getAnnotation(LoginState.class)).stateType();
            }

            Method implMethod = null;

            for (int classFile = 0; classFile < implMethods.length; classFile++) {
                implMethod = implMethods[classFile];
                if (implMethod.getName().equals(apiMethod.getName())) {
                    break;
                }
            }

            if (implMethod == null) {
                throw CommonUtils.illegalStateException("MOT found...");
            }

            ClassFile var20 = ClassFile.createClassFile(this.service.getClass().getName());
            LocalVariableAttribute argsAttrs = var20 == null ? null : var20.getMethodLocalVariableAttribute(implMethod.getName());
            Type[] argsTypes = apiMethod.getGenericParameterTypes();
            Object[] argsMarkers = null;
            String[] argsNames = null;
            if (argsTypes != null && argsTypes.length > 0) {
                argsMarkers = new Object[argsTypes.length];
                argsNames = new String[argsTypes.length];
                int returnType = 0;

                for (int holder = 1; returnType < argsTypes.length; ++holder) {
                    Type type = argsTypes[returnType];
                    argsNames[returnType] = argsAttrs != null ? argsAttrs.methodVariableName(holder) : "args" + holder;
                    argsMarkers[returnType] = GenericUtils.foundGenericStamp(type);
                    ++returnType;
                }
            }

            Type var21 = implMethod.getGenericReturnType();
            if (!(var21 instanceof Class)) {
                throw new UnsupportedOperationException("Unsupported Return Type=" + var21);
            }

            MethodHolder var22 = new MethodHolder(implMethod, (Class) var21, argsMarkers, argsNames, stateType);
            holderMap.put(apiMethod.getName(), var22);
        }

        return holderMap;
    }

    public String getName() {
        return this.name;
    }

    public Class<?> getApiClass() {
        return this.apiClass;
    }

    public Object getService() {
        return this.service;
    }

    public MethodHolder getMethodHolder(String methodName) {
        return (MethodHolder)this.holderMap.get(methodName);
    }

    public Map<String, MethodHolder> getMethodHolderMap() {
        return this.holderMap;
    }

    public String toString() {
        return "{name=" + this.name + ", apiClass=" + this.apiClass.getName() + ", service=" + this.service.getClass().getName() + ", holderMap=" + this.holderMap + "}";
    }
}
