package com.aegal.framework.core.exceptions;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 5:48 PM
 */
public class ServiceCallException extends Exception implements MsException {

    private static final long serialVersionUID = 1L;

    public ServiceCallException(Throwable cause) {
        super(cause);
    }
}
