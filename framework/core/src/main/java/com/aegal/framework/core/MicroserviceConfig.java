package com.aegal.framework.core;

import com.aegal.framework.core.auth.AuthConfig;
import com.aegal.framework.core.database.DbConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ge.snowizard.discovery.DiscoveryFactory;
import com.yammer.tenacity.core.config.BreakerboxConfiguration;
import io.dropwizard.Configuration;

/**
 * User: A.Egal
 * Date: 8/7/14
 * Time: 8:30 PM
 */
public abstract class MicroserviceConfig extends Configuration {

    @JsonProperty("discovery")
    private DiscoveryFactory discoveryFactory;

    @JsonProperty("test")
    private boolean test = false;

    @JsonProperty("allow-origin")
    private boolean allowOrigin = true;

    @JsonProperty("authentication")
    private AuthConfig authConfig;

    @JsonProperty("breakerbox")
    private BreakerboxConfiguration breakerboxConfiguration;

    public DiscoveryFactory getDiscoveryFactory() {
        return discoveryFactory;
    }

    public void setDiscoveryFactory(DiscoveryFactory discoveryFactory) {
        this.discoveryFactory = discoveryFactory;
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(AuthConfig authConfig) {
        this.authConfig = authConfig;
    }

    public abstract DbConfig getDatabaseConfig();

    public boolean isTest() {
        return test;
    }

    public boolean isAllowOrigin() {
        return allowOrigin;
    }

    public BreakerboxConfiguration getBreakerboxConfiguration() {
        return breakerboxConfiguration;
    }

    public void setBreakerboxConfiguration(BreakerboxConfiguration breakerboxConfiguration) {
        this.breakerboxConfiguration = breakerboxConfiguration;
    }
}
