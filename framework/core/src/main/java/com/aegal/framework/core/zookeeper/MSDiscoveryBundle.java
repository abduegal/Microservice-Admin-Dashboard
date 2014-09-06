package com.aegal.framework.core.zookeeper;

import com.aegal.framework.core.MicroserviceConfig;
import com.ge.snowizard.discovery.DiscoveryBundle;
import io.dropwizard.setup.Environment;

/**
 * User: A.Egal
 * Date: 8/5/14
 * Time: 10:24 PM
 */
public abstract class MSDiscoveryBundle <T extends MicroserviceConfig> extends DiscoveryBundle<T> {

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        if (enabled(configuration)) {
            super.run(configuration, environment);
        }
    }

    public boolean enabled(T configuration) {
        return configuration.getDiscoveryFactory().getServiceName() != null;
    }
    
}
