package com.aegal.frontend.commands;

import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.srv.NamespacesManager;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.yammer.tenacity.core.TenacityCommand;

/**
 * Created by vagrant on 11/12/14.
 */
public class PingCommand extends TenacityCommand<String> {

    private NamespacesManager namespacesManager;
    private InstanceMetadata serviceInstance;
    private String ns;

    public PingCommand(final NamespacesManager namespacesManager,
                       final InstanceMetadata serviceInstance,
                       final String ns) {
        super(DependencyKeys.DASHBOARD_PING);
        this.namespacesManager = namespacesManager;
        this.serviceInstance = serviceInstance;
        this.ns = ns;
    }

    @Override
    protected String run() throws Exception {
        return namespacesManager.getServiceLocator(ns).buildAdminNoSerialize(serviceInstance, AdminMetrics.class).ping();
    }

    @Override
    protected String getFallback() {
        return "Unreachable";
    }
}
