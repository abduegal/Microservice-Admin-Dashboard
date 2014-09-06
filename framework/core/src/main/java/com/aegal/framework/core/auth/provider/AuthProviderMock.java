package com.aegal.framework.core.auth.provider;

import com.aegal.framework.core.auth.AuthConfig;
import com.aegal.framework.core.auth.domain.AuthUser;
import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * User: A.Egal
 * Date: 5/8/14
 * Time: 9:35 PM
 */
public class AuthProviderMock implements Authenticator<String, AuthUser> {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthProviderMock.class);

    private final AuthConfig authConfig;

    public AuthProviderMock(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    @Override
    public Optional<AuthUser> authenticate(String token) throws AuthenticationException {
        LOGGER.debug("Authentication performed");

        return Optional.of(AuthUser.create("mock", "mock", Arrays.asList("mock"), "mock"));
    }
}
