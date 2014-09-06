package com.aegal.framework.core.auth.provider;

import com.aegal.framework.core.auth.domain.AuthUser;
import com.aegal.framework.core.auth.provider.srv.MvelVariableProvider;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.core.HttpContext;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class MSVariableProvider implements MvelVariableProvider<AuthUser> {

    @Override
    public VariableResolverFactory createCommonVariables() {
        VariableResolverFactory functionFactory = new MapVariableResolverFactory();
        MVEL.eval("def user(userParam) { usr == userParam; };", functionFactory);
        MVEL.eval("def privilege(privilege) { priv contains privilege; };", functionFactory);
        MVEL.eval("def role(role) { rls == role; };", functionFactory);
        MVEL.eval("def owner() { path.matches('.*user/' + usr + '($|/.*)'); };", functionFactory);
        return functionFactory;
    }

    @Override
    public VariableResolverFactory createPerRequestVariables(AuthUser res, HttpContext httpContext) {
        return new MapVariableResolverFactory(
                ImmutableMap.<String, Object>of(
                        "usr", res.getUsername(),
                        "priv", res.getPrivileges(),
                        "rls", res.getRole(),
                        "path", httpContext.getUriInfo().getPath()));
    }
}