package com.aegal.framework.core.exceptions;

/**
 * Created by vagrant on 11/29/14.
 */
public class ServiceHTTPException extends RuntimeException implements MsException {

    public ServiceHTTPException(String message) {
        super(message);
    }

    public ServiceHTTPException() {
    }


}
