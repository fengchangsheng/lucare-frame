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

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class TypesUtils {

    private static Map<Class<?>, Object> defaultValues = new ConcurrentHashMap();
    public static final Boolean STAMP_BOOLEAN;
    public static final Byte STAMP_BYTE;
    public static final Short STAMP_SHORT;
    public static final Float STAMP_FLOAT;
    public static final Integer STAMP_INT;
    public static final Long STAMP_LONG;
    public static final Double STAMP_DOUBLE;
    public static final String STAMP_STRING = "";
    public static final Object STAMP_OBJECT;
    public static final boolean[] STAMP_BOOLEAN_ARRAY;
    public static final byte[] STAMP_BYTE_ARRAY;
    public static final short[] STAMP_SHORT_ARRAY;
    public static final int[] STAMP_INT_ARRAY;
    public static final long[] STAMP_LONG_ARRAY;
    public static final float[] STAMP_FLOAT_ARRAY;
    public static final double[] STAMP_DOUBLE_ARRAY;

    public TypesUtils() {
    }

    public static boolean isPrimitive(Class<?> klass) {
        return klass == Boolean.TYPE || klass == Boolean.class || klass == Byte.TYPE || klass == Byte.class || klass == Short.TYPE || klass == Short.class || klass == Integer.TYPE || klass == Integer.class || klass == Long.TYPE || klass == Long.class || klass == Float.TYPE || klass == Float.class || klass == Double.TYPE || klass == Double.class || klass == String.class;
    }

    public static boolean isMap(Class<?> klass) {
        return Map.class.isAssignableFrom(klass);
    }

    public static boolean isCollection(Class<?> klass) {
        return Collection.class.isAssignableFrom(klass);
    }

    public static Object getDefaultValue(Class<?> klass) {
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
                                    } else if(klass != Void.TYPE && klass != Void.class) {
                                        if(klass == Object.class) {
                                            return STAMP_OBJECT;
                                        } else {
                                            Object stamp = defaultValues.get(klass);
                                            if(stamp == null) {
                                                if(klass.isArray()) {
                                                    Class componentType = klass.getComponentType();
                                                    Object[] array = (Object[])((Object[]) Array.newInstance(componentType, 1));
                                                    Object e = getDefaultValue(componentType);
                                                    array[0] = e;
                                                    stamp = array;
                                                } else {
                                                    stamp = CommonUtils.newInstance(klass);
                                                }

                                                defaultValues.put(klass, stamp);
                                            }

                                            return stamp;
                                        }
                                    } else {
                                        return null;
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

    public static Object foundDefualtValue(Type type) {
        if(type instanceof Class) {
            Class genericArrayType2 = (Class)type;
            return getDefaultValue(genericArrayType2);
        } else if(type instanceof ParameterizedType) {
            ParameterizedType genericArrayType1 = (ParameterizedType)type;
            Class componentType1 = (Class)genericArrayType1.getRawType();
            Type[] component1;
            Type array1;
            if(isMap(componentType1)) {
                component1 = genericArrayType1.getActualTypeArguments();
                array1 = component1[0];
                Type e1 = component1[1];
                Object list1 = foundDefualtValue(array1);
                Object value = foundDefualtValue(e1);
                HashMap map = new HashMap(1);
                map.put(list1, value);
                return map;
            } else if(isCollection(componentType1)) {
                component1 = genericArrayType1.getActualTypeArguments();
                array1 = component1[0];
                Object e = foundDefualtValue(array1);
                ArrayList list = new ArrayList(1);
                list.add(e);
                return list;
            } else {
                return getDefaultValue(componentType1);
            }
        } else if(type instanceof GenericArrayType) {
            GenericArrayType genericArrayType = (GenericArrayType)type;
            Type componentType = genericArrayType.getGenericComponentType();
            Object component = foundDefualtValue(componentType);
            Object[] array = (Object[])((Object[])Array.newInstance(component.getClass(), 1));
            array[0] = component;
            return array;
        } else {
            throw new IllegalStateException("Type=[" + type + "] illegal.");
        }
    }

    public static <T> T cashFor(Object data, Class<?> klass) {
        Object target = foundDefualtValue(klass);
        return (T) cashTo0(data, target);
    }

    public static Object cashTo(Object data, Object target) {
        if(data != null && target != null && target != STAMP_OBJECT) {
            Object realValue = null;
            if(target.getClass() == data.getClass()) {
                realValue = data;
            } else {
                String stringOfData = data.getClass() == String.class?(String)data:CommonUtils.emptyIfNull(data.toString());
                if(stringOfData.isEmpty()) {
                    return target;
                }

                if(target == STAMP_DOUBLE) {
                    realValue = Double.valueOf(stringOfData);
                } else if(target == STAMP_LONG) {
                    realValue = Long.valueOf(stringOfData);
                } else if(target == STAMP_INT) {
                    realValue = Integer.valueOf(stringOfData);
                } else if(target == STAMP_BOOLEAN) {
                    realValue = Boolean.valueOf(stringOfData);
                } else if(target == STAMP_FLOAT) {
                    realValue = Float.valueOf(stringOfData);
                } else if(target == STAMP_SHORT) {
                    realValue = Short.valueOf(stringOfData);
                } else {
                    if(target != STAMP_BYTE) {
                        if(target == "") {
                            return stringOfData;
                        }

                        throw new UnsupportedOperationException("Unsupported fix Object=[" + data.getClass().getName() + "] stamp Class=" + target.getClass().getName());
                    }

                    realValue = Byte.valueOf(stringOfData);
                }
            }

            return realValue;
        } else {
            return data;
        }
    }

    private static Object cashTo0(Object data, Object target) {
        try {
            return cashTo(data, target);
        } catch (Exception var3) {
            return null;
        }
    }

    static {
        STAMP_BOOLEAN = Boolean.TRUE;
        STAMP_BYTE = Byte.valueOf((byte)0);
        STAMP_SHORT = Short.valueOf((short)0);
        STAMP_FLOAT = Float.valueOf(0.0F);
        STAMP_INT = Integer.valueOf(0);
        STAMP_LONG = Long.valueOf(0L);
        STAMP_DOUBLE = Double.valueOf(0.0D);
        STAMP_OBJECT = new Object();
        STAMP_BOOLEAN_ARRAY = new boolean[]{true};
        STAMP_BYTE_ARRAY = new byte[]{(byte)0};
        STAMP_SHORT_ARRAY = new short[]{(short)0};
        STAMP_INT_ARRAY = new int[]{0};
        STAMP_LONG_ARRAY = new long[]{0L};
        STAMP_FLOAT_ARRAY = new float[]{0.0F};
        STAMP_DOUBLE_ARRAY = new double[]{0.0D};
    }
}
