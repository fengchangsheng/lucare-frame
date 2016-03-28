package com.lucare.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.nio.ByteBuffer;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class BASE64Utils {
    public BASE64Utils() {
    }

    public static String encodeUTF8(String input) {
        return encode(input, "UTF-8");
    }

    public static String encode(String input, String charset) {
        try {
            return encode(input.getBytes(charset));
        } catch (Exception var3) {
            throw CommonUtils.illegalStateException(var3);
        }
    }

    public static String encode(byte[] bytes) {
        return (new BASE64Encoder()).encode(bytes);
    }

    public static String encode(ByteBuffer buf) {
        return (new BASE64Encoder()).encode(buf);
    }

    public static String decodeUTF8(String input) {
        try {
            return decode(input, "UTF-8");
        } catch (Exception var2) {
            throw CommonUtils.illegalStateException(var2);
        }
    }

    public static String decode(String input, String charset) {
        try {
            return new String(decode(input), charset);
        } catch (Exception var3) {
            throw CommonUtils.illegalStateException(var3);
        }
    }

    public static byte[] decode(String input) {
        try {
            return (new BASE64Decoder()).decodeBuffer(input);
        } catch (Exception var2) {
            throw CommonUtils.illegalStateException(var2);
        }
    }

    public static void main(String[] args) {
        String input = "融信财富";
        String output = encodeUTF8(input);
        System.out.println("encode input=" + output);
        System.out.println("decode output=" + decodeUTF8(output));
    }
}
