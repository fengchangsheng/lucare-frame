package com.lucare.common.utils;

/**
 * Created by Lucare.Feng on 2016/3/29.
 */
public class XmlException extends RuntimeException {
    public XmlException(String message) {
        super(message);
    }

    public XmlException(String message, Throwable cause) {
        super(message, cause);
    }
}
