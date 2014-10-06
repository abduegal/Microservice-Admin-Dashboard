package com.aegal.framework.core.exceptions;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 5:53 PM
 */
public class GuardException extends RuntimeException implements MsException {

    private static final long serialVersionUID = 1L;

    public GuardException(String message) {
        super(message);
    }
}
