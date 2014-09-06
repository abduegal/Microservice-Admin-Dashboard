package com.aegal.framework.core.zookeeper;

import com.ge.snowizard.discovery.DiscoveryFactory;

/**
 * User: A.Egal
 * Date: 8/7/14
 * Time: 8:39 PM
 */
public class MSDiscoveryFactory extends DiscoveryFactory {

    private String serviceName;

    public MSDiscoveryFactory(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }
}
