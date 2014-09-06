package com.aegal.framework.core.auth.provider.srv;

import com.sun.jersey.api.core.HttpContext;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The class use MVEL expression contained in Auth type to evaluate the authorization condition.
 * The class performs caching of expressions to achieve better performance.
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
public class MvelExpressionEvaluation<R> implements Evaluation<R> {

    private static final String ALWAYS_TRUE_EXP = "";

    private final MvelVariableProvider<R> variableProvider;

    private final VariableResolverFactory commonVariables;

    private final ConcurrentMap<String, Serializable> expressionCache;

    public MvelExpressionEvaluation(MvelVariableProvider<R> variableProvider) {
        this.variableProvider = variableProvider;
        this.commonVariables = variableProvider.createCommonVariables();
        this.expressionCache = new ConcurrentHashMap<String, Serializable>();
    }

    @Override
    public boolean evaluate(R res, Auth auth, HttpContext httpContext) {
        if (ALWAYS_TRUE_EXP.equals(auth.check())) {
            return true;
        }
        VariableResolverFactory vars = variableProvider.createPerRequestVariables(res, httpContext);
        vars.setNextFactory(commonVariables);
        Serializable exp = expressionCache.get(auth.check());
        if (exp == null) {
            exp = MVEL.compileExpression(auth.check().toString());
            expressionCache.put(auth.check().toString(), exp);
        }
        return (Boolean) MVEL.executeExpression(exp, vars);
    }
}