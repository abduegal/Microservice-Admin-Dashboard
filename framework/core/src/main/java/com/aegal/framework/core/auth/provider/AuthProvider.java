package com.aegal.framework.core.auth.provider;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.Authentication;
import com.aegal.framework.core.auth.AuthConfig;
import com.aegal.framework.core.auth.domain.AuthUser;
import com.aegal.framework.core.exceptions.ServiceCallException;
import com.google.common.base.Optional;
import feign.FeignException;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: A.Egal
 * Date: 5/8/14
 * Time: 9:35 PM
 */
public class AuthProvider implements Authenticator<String, AuthUser> {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthProvider.class);

    private final AuthConfig authConfig;

    public AuthProvider(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public Optional<AuthUser> authenticate(String token) throws AuthenticationException {
        LOGGER.debug("Authentication performed");

        try {

            AuthUser authenticate =
                    ServiceLocator.getInstance().build(authConfig.getServicename(), Authentication.class)
                            .authenticate(authConfig.getPath(), token);

            return Optional.fromNullable(authenticate);

        } catch (ServiceCallException e) {
            throw new AuthenticationException(e);
        } catch (FeignException e) {
            return Optional.absent();
        }
    }

}
