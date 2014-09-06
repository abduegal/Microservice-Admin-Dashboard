package com.aegal.framework.core.exceptions;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 5:53 PM
 */
public class GuardException extends RuntimeException implements MsException {

    public GuardException(String message) {
        super(message);
    }
}
