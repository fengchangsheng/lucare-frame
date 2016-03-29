package com.lucare.common.utils;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class GenericUtils {

    public static final Boolean STAMP_BOOLEAN;
    public static final Byte STAMP_BYTE;
    public static final Short STAMP_SHORT;
    public static final Float STAMP_FLOAT;
    public static final Integer STAMP_INT;
    public static final Long STAMP_LONG;
    public static final Double STAMP_DOUBLE;
    public static final String STAMP_STRING = "";
    public static final boolean[] STAMP_BOOLEAN_ARRAY;
    public static final byte[] STAMP_BYTE_ARRAY;
    public static final short[] STAMP_SHORT_ARRAY;
    public static final int[] STAMP_INT_ARRAY;
    public static final long[] STAMP_LONG_ARRAY;
    public static final float[] STAMP_FLOAT_ARRAY;
    public static final double[] STAMP_DOUBLE_ARRAY;
    private static ConcurrentMap<Class<?>, Object> stampCache;

    public GenericUtils() {
    }

    public static Object foundGenericStamp(Type type) {
        if(type instanceof Class) {
            return getInstance((Class)type);
        } else {
            if(type instanceof ParameterizedType) {
                ParameterizedType genericArrayType = (ParameterizedType)type;
                Class componentType = (Class)genericArrayType.getRawType();
                Type[] component;
                Type array;
                if(Map.class.isAssignableFrom(componentType)) {
                    component = genericArrayType.getActualTypeArguments();
                    array = component[0];
                    Type e1 = component[1];
                    Object list1 = foundGenericStamp(array);
                    Object value = foundGenericStamp(e1);
                    HashMap map = new HashMap(2, 1.0F);
                    map.put(list1, value);
                    return map;
                }

                if(Collection.class.isAssignableFrom(componentType)) {
                    component = genericArrayType.getActualTypeArguments();
                    array = component[0];
                    Object e = foundGenericStamp(array);
                    ArrayList list = new ArrayList(1);
                    list.add(e);
                    return list;
                }
            } else if(type instanceof GenericArrayType) {
                GenericArrayType genericArrayType1 = (GenericArrayType)type;
                Type componentType1 = genericArrayType1.getGenericComponentType();
                Object component1 = foundGenericStamp(componentType1);
                Object[] array1 = (Object[])((Object[]) Array.newInstance(component1.getClass(), 1));
                array1[0] = component1;
                return array1;
            }

            throw new UnsupportedOperationException("Unsupported Generic Type=" + type);
        }
    }

    public static Object getInstance(Class<?> klass) {
        if(klass != Boolean.TYPE && klass != Boolean.class) {
            if(klass != Byte.TYPE && klass != Byte.class) {
                if(klass != Short.TYPE && klass != Short.class) {
                    if(klass != Integer.TYPE && klass != Integer.class) {
                        if(klass != Long.TYPE && klass != Long.class) {
                            if(klass != Float.TYPE && klass != Float.class) {
                                if(klass != Double.TYPE && klass != Double.class) {
                                    if(klass == String.class) {
                                        return "";
                                    } else if(klass == boolean[].class) {
                                        return STAMP_BOOLEAN_ARRAY;
                                    } else if(klass == byte[].class) {
                                        return STAMP_BYTE_ARRAY;
                                    } else if(klass == short[].class) {
                                        return STAMP_SHORT_ARRAY;
                                    } else if(klass == int[].class) {
                                        return STAMP_INT_ARRAY;
                                    } else if(klass == long[].class) {
                                        return STAMP_LONG_ARRAY;
                                    } else if(klass == float[].class) {
                                        return STAMP_FLOAT_ARRAY;
                                    } else if(klass == double[].class) {
                                        return STAMP_DOUBLE_ARRAY;
                                    } else {
                                        Object stamp = stampCache.get(klass);
                                        if(stamp == null) {
                                            if(klass.isArray()) {
                                                Class componentType = klass.getComponentType();
                                                Object[] array = (Object[])((Object[])Array.newInstance(componentType, 1));
                                                Object e = getInstance(componentType);
                                                array[0] = e;
                                                stamp = array;
                                            } else {
                                                stamp = CommonUtils.newInstance(klass);
                                            }

                                            stampCache.put(klass, stamp);
                                        }

                                        return stamp;
                                    }
                                } else {
                                    return STAMP_DOUBLE;
                                }
                            } else {
                                return STAMP_FLOAT;
                            }
                        } else {
                            return STAMP_LONG;
                        }
                    } else {
                        return STAMP_INT;
                    }
                } else {
                    return STAMP_SHORT;
                }
            } else {
                return STAMP_BYTE;
            }
        } else {
            return STAMP_BOOLEAN;
        }
    }

    public static <T> T parseObject(Class<?> klass, Object target) {
        Object stamp = getInstance(klass);
        return (T) parseObject(stamp, target);
    }

    public static Object parseObject(Object stamp, Object target) {
        Object realValue = null;
        if(target != null) {
            if(stamp.getClass() == target.getClass()) {
                realValue = target;
            } else {
                String stringOfTarget = CommonUtils.emptyIfNull(target.toString());
                if(stringOfTarget.isEmpty()) {
                    return stamp;
                }

                if(stamp == STAMP_DOUBLE) {
                    realValue = Double.valueOf(stringOfTarget);
                } else if(stamp == STAMP_LONG) {
                    realValue = Long.valueOf(stringOfTarget);
                } else if(stamp == STAMP_INT) {
                    realValue = Integer.valueOf(stringOfTarget);
                } else if(stamp == STAMP_BOOLEAN) {
                    realValue = Boolean.valueOf(stringOfTarget);
                } else if(stamp == STAMP_FLOAT) {
                    realValue = Float.valueOf(stringOfTarget);
                } else if(stamp == STAMP_SHORT) {
                    realValue = Short.valueOf(stringOfTarget);
                } else {
                    if(stamp != STAMP_BYTE) {
                        if(stamp == "") {
                            return stringOfTarget;
                        }

                        throw new UnsupportedOperationException("Unsupported fix Object=[" + target.getClass().getName() + "] stamp Class=" + stamp.getClass().getName());
                    }

                    realValue = Byte.valueOf(stringOfTarget);
                }
            }
        }

        return realValue;
    }

    static {
        STAMP_BOOLEAN = Boolean.TRUE;
        STAMP_BYTE = Byte.valueOf((byte)0);
        STAMP_SHORT = Short.valueOf((short)0);
        STAMP_FLOAT = Float.valueOf(0.0F);
        STAMP_INT = Integer.valueOf(0);
        STAMP_LONG = Long.valueOf(0L);
        STAMP_DOUBLE = Double.valueOf(0.0D);
        STAMP_BOOLEAN_ARRAY = new boolean[]{true};
        STAMP_BYTE_ARRAY = new byte[]{(byte)0};
        STAMP_SHORT_ARRAY = new short[]{(short)0};
        STAMP_INT_ARRAY = new int[]{0};
        STAMP_LONG_ARRAY = new long[]{0L};
        STAMP_FLOAT_ARRAY = new float[]{0.0F};
        STAMP_DOUBLE_ARRAY = new double[]{0.0D};
        stampCache = new ConcurrentHashMap();
    }
}
