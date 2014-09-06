package com.aegal.framework.core.auth.provider.srv;

import com.sun.jersey.api.core.HttpContext;
import org.mvel2.integration.VariableResolverFactory;

/**
 * The MvelVariableProvider returns variables which can be used in the condition expression.
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
public interface MvelVariableProvider<R> {

    VariableResolverFactory createCommonVariables();

    VariableResolverFactory createPerRequestVariables(R res, HttpContext httpContext);
}