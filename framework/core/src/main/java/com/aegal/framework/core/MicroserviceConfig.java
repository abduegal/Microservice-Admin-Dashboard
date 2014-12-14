package com.aegal.framework.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.tenacity.core.config.BreakerboxConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.discovery.DiscoveryFactory;

/**
 * User: A.Egal
 * Date: 8/7/14
 * Time: 8:30 PM
 */
public class MicroserviceConfig extends Configuration {

    @JsonProperty("discovery")
    private DiscoveryFactory discoveryFactory;

    @JsonProperty("version")
    private String version;

    @JsonProperty("test")
    private boolean test = false;

    @JsonProperty("allow-origin")
    private boolean allowOrigin = true;

    @JsonProperty("breakerbox")
    private BreakerboxConfiguration breakerboxConfiguration;

    public DiscoveryFactory getDiscoveryFactory() {
        return discoveryFactory;
    }

    public void setDiscoveryFactory(DiscoveryFactory discoveryFactory) {
        this.discoveryFactory = discoveryFactory;
    }

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

    public String getVersion() {
        return version;
    }
}
