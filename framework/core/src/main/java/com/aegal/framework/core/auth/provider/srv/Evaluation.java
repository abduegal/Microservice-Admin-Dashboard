package com.aegal.framework.core.auth.provider.srv;

import com.sun.jersey.api.core.HttpContext;

/**
 * The Evaluation type evaluates condition contained in Auth type considering authentication result + context
 * and returns result.
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
public interface Evaluation<R> {

    boolean evaluate(R res, Auth auth, HttpContext httpContext);
}