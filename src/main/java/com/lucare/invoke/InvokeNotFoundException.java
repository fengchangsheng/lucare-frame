package com.lucare.invoke;

/**
 * Created by Lucare.Feng on 2016/3/27.
 */
public class InvokeNotFoundException extends RuntimeException {

    public InvokeNotFoundException(String message) {
        super(message);
    }

    public InvokeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeNotFoundException(Throwable cause) {
        super(cause);
    }

}
