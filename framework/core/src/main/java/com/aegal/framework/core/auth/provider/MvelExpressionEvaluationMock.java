package com.aegal.framework.core.auth.provider;

import com.aegal.framework.core.auth.provider.srv.Auth;
import com.aegal.framework.core.auth.provider.srv.Evaluation;
import com.aegal.framework.core.auth.provider.srv.MvelVariableProvider;
import com.sun.jersey.api.core.HttpContext;

/**
 * The class use MVEL expression contained in Auth type to evaluate the authorization condition.
 * The class performs caching of expressions to achieve better performance.
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
public class MvelExpressionEvaluationMock<R> implements Evaluation<R> {

    public MvelExpressionEvaluationMock(MvelVariableProvider<R> variableProvider) {
    }

    @Override
    public boolean evaluate(R res, Auth auth, HttpContext httpContext) {
        return true;
    }
}