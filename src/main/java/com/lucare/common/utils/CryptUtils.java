package com.lucare.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;

/**
 * Created by Lucare.Feng on 2016/3/24.
 */
public class CryptUtils {
    private static final char[] hexChar = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public CryptUtils() {
    }

    public static String md5(String input) {
        return md5(input, (String)null);
    }

    public static String md5(String input, String charset) {
        if(input == null) {
            return null;
        } else {
            try {
                byte[] e = charset == null?input.getBytes():input.getBytes(charset);
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(e);
                byte[] bytes = md.digest();
                char[] md5 = new char[bytes.length << 1];
                int len = 0;

                for(int i = 0; i < bytes.length; ++i) {
                    byte val = bytes[i];
                    md5[len++] = hexChar[val >>> 4 & 15];
                    md5[len++] = hexChar[val & 15];
                }

                return new String(md5);
            } catch (Exception var9) {
                throw CommonUtils.illegalStateException(var9);
            }
        }
    }

    public static String urlEncodeBase64(String input) {
        return input == null?null:CommonUtils.urlEncodeUTF8(BASE64Utils.encodeUTF8(input));
    }

    public static String urlDecodeBase64(String input) {
        if(input == null) {
            return null;
        } else {
            input = CommonUtils.urlDecodeUTF8(input);
            return BASE64Utils.decodeUTF8(input);
        }
    }

    public static String desEncode(String key, String data, String desIvParameterSpec) throws Exception {
        if(CommonUtils.isEmpty(data)) {
            return "";
        } else {
            DESKeySpec dks = new DESKeySpec(key.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey secretKey = keyFactory.generateSecret(dks);
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(desIvParameterSpec.getBytes());
            cipher.init(1, secretKey, iv);
            byte[] bytes = cipher.doFinal(data.getBytes("GBK"));
            return BASE64Utils.encode(bytes);
        }
    }

    public static String desDecode(String key, String data, String desIvParameterSpec) throws Exception {
        DESKeySpec dks = new DESKeySpec(key.getBytes());
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        IvParameterSpec iv = new IvParameterSpec(desIvParameterSpec.getBytes());
        cipher.init(2, secretKey, iv);
        byte[] bytes = cipher.doFinal(BASE64Utils.decode(data));
        return new String(bytes, "GBK");
    }

    public static void main(String[] args) {
        String input = "融信财富";
        String output = urlEncodeBase64(input);
        System.out.println("encode input=" + output);
        System.out.println("decode output=" + urlDecodeBase64(output));
    }
}
