package com.aegal.framework.core.database;

import com.aegal.framework.core.exceptions.MsException;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 4:51 PM
 */
public class DatabaseNotConfiguratedException extends Exception implements MsException {

    private static final long serialVersionUID = 1L;

    public DatabaseNotConfiguratedException(Throwable cause) {
        super(cause);
    }
}
