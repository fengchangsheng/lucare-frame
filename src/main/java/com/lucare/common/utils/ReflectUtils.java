package com.lucare.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Lucare.Feng on 2016/3/27.
 */
public class ReflectUtils {
    private static ConcurrentMap<Class<?>, Map<String, Field>> descContainer = new ConcurrentHashMap();

    public static Object invokeMethod(Method m, Object instance, Object[] args) {
        try {
            return m.invoke(instance, args);
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public static void main(String[] args) {
    }

    public static Method foundMethod(Class<?> klass, String name, Class<?>[] parameterTypes) {
        try {
            return klass.getDeclaredMethod(name, parameterTypes);
        } catch (Exception ignore) {
            Method[] methods = klass.getDeclaredMethods();
            String internedName = name.intern();
            for (int i = 0; i < methods.length; i++) {
                Method m = methods[i];
                if (m.getName() == internedName) {
                    return m;
                }
            }
        }

        throw new IllegalStateException("UNFOUND Method=[" + name + "] @ class=[" + klass.getName() + "]");
    }

    public static boolean setStaticField(Class<?> klass, String name, Object value, boolean isReturnIfNotField) {
        try {
            Field field = directGetField(klass, name);
            if ((field == null) && (isReturnIfNotField))
                return false;
            Object realValue = TypesUtils.cashFor(value, field.getType());
            field.set(klass, realValue);
            return true;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }

    public static Field directGetField(Class<?> klass, String name) {
        Field field = null;
        String internedName = null;
        try {
            field = klass.getDeclaredField(name);
        } catch (Exception ignore) {
            internedName = name.intern();
        }
        for (; (klass != null) && (klass != Object.class); klass = klass.getSuperclass()) {
            Field[] fields = klass.getDeclaredFields();
            for (Field e : fields) {
                if (e.getName() == internedName) {
                    field = e;
                    break;
                }
            }

        }

        if (field != null)
            field.setAccessible(true);
        return field;
    }

    public static Field foundField(Class<?> klass, String name) {
        return (Field)getFields(klass).get(name);
    }

    public static Map<String, Field> getFields(Class<?> klass) {
        Map stamp = (Map)descContainer.get(klass);
        if (stamp == null) {
            stamp = new LinkedHashMap();
            for (Class klazz = klass; (klazz != null) && (klazz != Object.class); klazz = klazz.getSuperclass()) {
                Field[] fields = klazz.getDeclaredFields();
                for (Field e : fields)
                    if (!stamp.containsKey(e.getName())) {
                        int mod = e.getModifiers();
                        if ((!Modifier.isFinal(mod)) && (!Modifier.isStatic(mod))) {
                            e.setAccessible(true);
                            stamp.put(e.getName(), e);
                        }
                    }
            }
            Map absent = (Map)descContainer.putIfAbsent(klass, stamp);
            if (absent != null)
                stamp = absent;
        }
        return stamp;
    }
}
