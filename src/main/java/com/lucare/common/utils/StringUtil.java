package com.lucare.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucare.Feng on 2016/3/25.
 */
public class StringUtil {
    public StringUtil() {
    }

    public static String[] split(String url, String string) {
        return url.split(string);
    }

    public static String trim(String input) {
        return input == null?null:input.trim();
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isEmpty(String input) {
        return input == null || input.trim().isEmpty();
    }

    public static String encodeSQL(String sql) {
        if(sql == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < sql.length(); ++i) {
                char c = sql.charAt(i);
                switch(c) {
                    case '\b':
                        sb.append("\\b");
                        break;
                    case '\t':
                        sb.append("\\t");
                        break;
                    case '\n':
                        sb.append("\\n");
                        break;
                    case '\r':
                        sb.append("\\r");
                        break;
                    case '\"':
                        sb.append("\\\"");
                        break;
                    case '\'':
                        sb.append("\'\'");
                        break;
                    case '\\':
                        sb.append("\\\\");
                        break;
                    default:
                        sb.append(c);
                }
            }

            return sb.toString();
        }
    }

    public static String decodeNetUnicode(String str) {
        if(str == null) {
            return null;
        } else {
            String pStr = "&#(\\d+);";
            Pattern p = Pattern.compile(pStr);
            Matcher m = p.matcher(str);
            StringBuffer sb = new StringBuffer();

            while(m.find()) {
                String mcStr = m.group(1);
                int charValue = convertInt(mcStr, -1);
                String s = charValue > 0?(char)charValue + "":"";
                m.appendReplacement(sb, Matcher.quoteReplacement(s));
            }

            m.appendTail(sb);
            return sb.toString();
        }
    }

    public static String convertString(String str, String defaults) {
        return str == null?defaults:str;
    }

    public static int convertInt(String input, int def) {
        try {
            return Integer.valueOf(input).intValue();
        } catch (Exception var3) {
            return def;
        }
    }

    public static long convertLong(String str, long defaults) {
        if(str == null) {
            return defaults;
        } else {
            try {
                return Long.parseLong(str);
            } catch (Exception var4) {
                return defaults;
            }
        }
    }

    public static double convertDouble(String str, double defaults) {
        if(str == null) {
            return defaults;
        } else {
            try {
                return Double.parseDouble(str);
            } catch (Exception var4) {
                return defaults;
            }
        }
    }

    public static short convertShort(String str, short defaults) {
        if(str == null) {
            return defaults;
        } else {
            try {
                return Short.parseShort(str);
            } catch (Exception var3) {
                return defaults;
            }
        }
    }

    public static float convertFloat(String str, float defaults) {
        if(str == null) {
            return defaults;
        } else {
            try {
                return Float.parseFloat(str);
            } catch (Exception var3) {
                return defaults;
            }
        }
    }

    public static boolean convertBoolean(String str, boolean defaults) {
        if(str == null) {
            return defaults;
        } else {
            try {
                return Boolean.parseBoolean(str);
            } catch (Exception var3) {
                return defaults;
            }
        }
    }

    public static String hideStringUsername(String username) {
        if(CommonUtils.isEmpty(username)) {
            return null;
        } else {
            StringBuilder builder = new StringBuilder(4);
            builder.append(username.substring(0, 1));
            builder.append("***");
            builder.append(username.substring(username.length() - 1));
            return builder.toString();
        }
    }
}
