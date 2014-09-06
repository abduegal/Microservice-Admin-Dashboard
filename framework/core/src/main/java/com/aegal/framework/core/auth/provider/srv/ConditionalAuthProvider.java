package com.aegal.framework.core.auth.provider.srv;


import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;

/**
 * Dropwizard AuthProvider wrapper which returns authentication result only
 * when the result of condition evaluation is true.
 *
 * @param <T> the principal type
 *
 * @author Stan Svec
 * Date: 4/10/13
 */
public class ConditionalAuthProvider<T> implements InjectableProvider<Auth, Parameter> {

    private final Evaluation<T> evaluation;

    private final InjectableProvider<io.dropwizard.auth.Auth, Parameter> wrappedProvider;

    public ConditionalAuthProvider(InjectableProvider<io.dropwizard.auth.Auth, Parameter> wrappedProvider,
                                   Evaluation<T> evaluation) {
        this.evaluation = evaluation;
        this.wrappedProvider = wrappedProvider;
    }

    @Override
    public ComponentScope getScope() {
        return wrappedProvider.getScope();
    }

    @Override
    public Injectable getInjectable(ComponentContext ic, final Auth auth, Parameter parameter) {
        return new ConditionalAuthInjectable<T>(
                evaluation, auth, (AbstractHttpContextInjectable) wrappedProvider.getInjectable(ic, new DropwizardAuth(auth.required()), parameter));
    }

    private static class ConditionalAuthInjectable<T> extends AbstractHttpContextInjectable<T> {

        private final Evaluation<T> evaluation;

        private final Auth auth;

        private final AbstractHttpContextInjectable<T> wrappedInjectable;

        public ConditionalAuthInjectable(Evaluation<T> evaluation, Auth auth, AbstractHttpContextInjectable<T> wrappedInjectable) {
            this.evaluation = evaluation;
            this.auth = auth;
            this.wrappedInjectable = wrappedInjectable;
        }

        @Override
        public T getValue(HttpContext httpContext) {
            T res = wrappedInjectable.getValue(httpContext);
            if (res == null) {
                return null;
            }
            if (evaluation.evaluate(res, auth, httpContext)) {
                return res;
            }

            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
    }

    private static class DropwizardAuth implements io.dropwizard.auth.Auth {

        private final boolean required;

        private DropwizardAuth(boolean required) {
            this.required = required;
        }

        @Override
        public boolean required() {
            return required;
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Auth.class;
        }

    }
}