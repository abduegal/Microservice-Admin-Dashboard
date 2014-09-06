package com.aegal.framework.core.util;

import com.aegal.framework.core.exceptions.GuardException;

/**
 * Does some checks and returns an exception if not passed.
 * User: A.Egal
 * Date: 8/8/14
 * Time: 5:51 PM
 */
public class Guard<T> {

    /**
     * Returns a GuardException if the object is null.
     */
    public static <T> T notNull(T object) {
        if (object == null) {
            throw new GuardException(String.format("object %s is null", object));
        }

        return object;
    }

}
