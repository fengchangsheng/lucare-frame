package com.lucare.common.utils;

import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class CommonUtils {
    public static final String UTF8 = "UTF-8";
    public static final List<?> EMPTY_LIST = Collections.unmodifiableList(new ArrayList());
    public static final Map<?, ?> EMPTY_MAP = Collections.unmodifiableMap(new HashMap());
    private static Boolean isWindowsOS = null;

    public CommonUtils() {
    }

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static String emptyIfNull(String input) {
        return input == null?"":input.trim();
    }

    public static String emptyIfNull(Object input) {
        return input == null?"":input.toString().trim();
    }

    public static String emptyIfNull(String input, String def) {
        input = emptyIfNull(input);
        return input.isEmpty()?def:input;
    }

    public static int parseInt(Object data) {
        return parseInt(data, 0);
    }

    public static int parseInt(Object data, int def) {
        if(data != null) {
            try {
                if(data instanceof Number) {
                    return ((Number)data).intValue();
                }

                return Integer.valueOf(String.valueOf(data)).intValue();
            } catch (Exception var3) {
                ;
            }
        }

        return def;
    }

    public static long parseLong(Object data) {
        return parseLong(data, 0L);
    }

    /** @deprecated */
    @Deprecated
    public static long parseLong(Object data, int def) {
        return parseLong(data, (long)def);
    }

    public static long parseLong(Object data, long def) {
        if(data != null) {
            try {
                if(data instanceof Number) {
                    return ((Number)data).longValue();
                }

                return Long.valueOf(String.valueOf(data)).longValue();
            } catch (Exception var4) {
                ;
            }
        }

        return def;
    }

    public static double parseDouble(Object data) {
        return parseDouble(data, 0.0D);
    }

    public static double parseDouble(Object data, double def) {
        double value = def;
        if(data != null) {
            try {
                if(data instanceof BigDecimal) {
                    value = ((BigDecimal)data).doubleValue();
                } else if(data instanceof Number) {
                    value = ((Number)data).doubleValue();
                } else {
                    value = Double.valueOf(String.valueOf(data)).doubleValue();
                }
            } catch (Exception var6) {
                ;
            }
        }

        return value == def?def:MathUtils.roundHalfUp(value);
    }

    public static Date parseUnixtime(Object data) {
        return data == null?null:(data instanceof Timestamp ?TimeUtils.parse((Timestamp)data):(data instanceof Number?TimeUtils.newDateByUnixTimestamp(((Number)data).longValue()):(data instanceof String?TimeUtils.newDateByUnixTimestamp((String)data):null)));
    }

    public static long unixtimeToMilliseonds(Object data) {
        long unixtimestamp = 0L;
        if(data != null) {
            if(data instanceof Timestamp) {
                unixtimestamp = ((Timestamp)data).getTime();
            } else if(data instanceof Number) {
                unixtimestamp = ((Number)data).longValue();
            } else if(data instanceof String) {
                unixtimestamp = Long.valueOf((String)data).longValue();
            }
        }

        return unixtimestamp == 0L?0L:unixtimestamp * 1000L;
    }

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static <T> boolean addAll(Collection<T> from, Collection<T> to) {
        return from != null && to != null?to.addAll(from):false;
    }

    public static int size(Collection<?> c) {
        return c == null?0:c.size();
    }

    public static <T> List<T> emptyList() {
        return (List<T>) EMPTY_LIST;
    }

    public static <T> List<T> emptyList(List<T> list) {
        return list == null? (List<T>) emptyList() :list;
    }

    public static <T> List<T> newArrayList(Collection<?> c) {
        return new ArrayList(size(c));
    }

    public static <T> Set<T> newHashSet(Collection<?> c) {
        return new HashSet(size(c), 1.0F);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> Map<K, V> emptyMap() {
        return (Map<K, V>) EMPTY_MAP;
    }

    public static <K, V> Map<K, V> emptyMap(Map<K, V> map) {
        return map == null? (Map<K, V>) EMPTY_MAP :map;
    }

    public static <K, V> Map<K, V> stableMap(int size) {
        return new HashMap(size, 1.0F);
    }

    public static <T> T indexGet(T[] array, int index, T def) {
        int arrayLength = array == null?0:array.length;
        return indexGet(array, arrayLength, index, def);
    }

    public static <T> T indexGet(T[] array, int arrayLength, int index, T def) {
        return index >= 0 && index < arrayLength?array[index]:def;
    }

    public static String filterHtml(String content) {
        if(isEmpty(content)) {
            return "";
        } else {
            HashMap regs = new HashMap();
            regs.put("<([^>]*)>", "");
            regs.put("(&nbsp;)", " ");
            regs.put("(&apos;)", "\'");
            regs.put("(&quot;)", "\"");
            regs.put("(&ldquo;)", "“");
            regs.put("(&rdquo;)", "”");
            regs.put("(&lt;)", "<");
            regs.put("(&gt;)", ">");
            regs.put("(&ndash;)", "-");
            Pattern p = null;
            Matcher m = null;

            Map.Entry entry;
            for(Iterator i$ = regs.entrySet().iterator(); i$.hasNext(); content = m.replaceAll((String)entry.getValue())) {
                entry = (Map.Entry)i$.next();
                p = Pattern.compile((String)entry.getKey(), 2);
                m = p.matcher(content);
            }

            return content;
        }
    }

    /** @deprecated */
    @Deprecated
    public static String replaceHref(String str) {
        if(str == null) {
            return null;
        } else {
            String firstStr = "<a href=\'/invest/a";
            String lastStr = ".html\' target=_blank>";
            String firstReplace = "<a href=\'/invest-page.html?id=";
            String lastReplace = "\' target=_blank>";
            int first = str.indexOf(firstStr);
            if(first >= 0) {
                str = str.replaceAll(firstStr, firstReplace);
                str = str.replaceAll(lastStr, lastReplace);
            }

            return str;
        }
    }

    public static IllegalStateException illegalStateException(Throwable t) {
        return new IllegalStateException(t);
    }

    public static IllegalStateException illegalStateException(String message) {
        return new IllegalStateException(message);
    }

    public static IllegalStateException illegalStateException(String message, Throwable t) {
        return new IllegalStateException(message, t);
    }

    public static IllegalArgumentException illegalArgumentException(String message) {
        return new IllegalArgumentException(message);
    }

    public static UnsupportedOperationException unsupportedMethodException() {
        return new UnsupportedOperationException("unsupport this method");
    }

    public static Throwable foundRealThrowable(Throwable t) {
        Throwable cause = t.getCause();
        return cause == null?t:foundRealThrowable(cause);
    }

    public static String formatThrowable(Throwable t) {
        if(t == null) {
            return "";
        } else {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            pw.flush();
            sw.flush();
            return sw.toString();
        }
    }

    public static String formatThrowableForHtml(Throwable t) {
        String ex = formatThrowable(t);
        return ex.replaceAll("\n\t", " ");
    }

    public static <T> T newInstance(Class<T> klass) {
        try {
            return klass.newInstance();
        } catch (Exception var2) {
            throw new IllegalArgumentException("instance class[" + klass.getName() + "] with ex:", var2);
        }
    }

    public static <T> T newInstance(Class<T> klass, Class<?>[] cstTypes, Object... cstParameters) {
        try {
            Constructor e = klass.getConstructor(cstTypes);
            return (T) e.newInstance(cstParameters);
        } catch (Exception var4) {
            throw new IllegalArgumentException("instance class[" + klass.getName() + "], cstTypes=" + Arrays.toString(cstTypes) + ", " + Arrays.toString(cstParameters) + " with ex:", var4);
        }
    }

    public static <T> T newInstance(String className) {
        try {
            return (T) newInstance(classForName(className));
        } catch (Exception var2) {
            throw new IllegalArgumentException("instance class[" + className + "] with ex:", var2);
        }
    }

    public static Class<?> classForName(String className) {
        try {
            return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        } catch (Exception var4) {
            try {
                return Class.forName(className);
            } catch (Exception var3) {
                throw new IllegalArgumentException("classForName(" + className + ")  with ex:", var3);
            }
        }
    }

    public static Class<?> loadClass(String className, ClassLoader classLoader) {
        try {
            return classLoader.loadClass(className);
        } catch (Exception var3) {
            throw new IllegalArgumentException("loadClass(" + className + ")  with ex:", var3);
        }
    }

    public static InputStream getInputStreamFromClassPath(String filename) {
        if(isEmpty(filename)) {
            return null;
        } else {
            URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
            System.out.println("[load file from classpath]:name={" + filename + "}, locator={" + url + "}");
            return url == null?null:Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
        }
    }

    public static String mergePath(String... paths) {
        if(isEmpty((Object[])paths)) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(paths[0]);

            for(int i = 1; i < paths.length; ++i) {
                String path = emptyIfNull(paths[i]);
                path = path.startsWith(File.separator)?path.substring(1):path;
                path = path.endsWith(File.separator)?path.substring(0, path.length() - 1):path;
                builder.append(File.separator).append(path);
            }

            return builder.toString();
        }
    }

    public static String urlDecodeUTF8(String input) {
        if(input == null) {
            return null;
        } else {
            try {
                return URLDecoder.decode(input, "UTF-8");
            } catch (Exception var2) {
                throw illegalStateException((Throwable)var2);
            }
        }
    }

    public static String urlEncodeUTF8(String input) {
        if(input == null) {
            return null;
        } else {
            try {
                return URLEncoder.encode(input, "UTF-8");
            } catch (Exception var2) {
                throw illegalStateException((Throwable)var2);
            }
        }
    }

    public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
        if(map != null) {
            if(key != null && value != null) {
                map.put(key, value);
            }

        }
    }

    public static <T> T get(Map<T, T> map, Object key) {
        return map == null?null:map.get(key);
    }

    public static <T> T get(Map<T, T> map, Object key, T def) {
        Object t = null;

        try {
            Object ignore = get(map, key);
            if(ignore == null) {
                t = def;
            } else if(def == null) {
                t = ignore;
            } else {
                Object to = TypesUtils.getDefaultValue(def.getClass());
                t = TypesUtils.cashTo(ignore, to);
            }
        } catch (Exception var6) {
            ;
        }

        return t == null?(T)def:(T)t;
    }

    public static RuntimeException convertRuntimeException(Throwable e) {
        RuntimeException ex = null;
        if(e != null) {
            if(e instanceof RuntimeException) {
                ex = (RuntimeException)e;
            } else {
                ex = new RuntimeException(e);
            }
        }

        return ex;
    }

    public static boolean isPhone(String input) {
        if(input != null && input.length() == 11 && input.startsWith("1")) {
            boolean is = isNumber(input);
            return is;
        } else {
            return false;
        }
    }

    public static boolean isNumber(String input) {
        if(input == null) {
            return false;
        } else {
            for(int index = 0; index < input.length(); ++index) {
                if(!Character.isDigit(input.charAt(index))) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean isWindowsOS() {
        if(isWindowsOS == null) {
            isWindowsOS = Boolean.valueOf(System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1);
        }

        return isWindowsOS.booleanValue();
    }

    public static void main(String[] args) {
        Integer.parseInt("1.333");
    }
}
