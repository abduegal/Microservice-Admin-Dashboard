package com.aegal.frontend.commands;

import com.aegal.framework.core.FeignBuilder;
import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.srv.NamespacesManager;
import com.yammer.tenacity.core.TenacityCommand;

/**
 * Created by vagrant on 11/12/14.
 */
public class PingCommand extends TenacityCommand<String> {

    private NamespacesManager namespacesManager;
    private MicroserviceMetaData instance;
    private String ns;

    public PingCommand(final NamespacesManager namespacesManager,
                       final MicroserviceMetaData instance,
                       final String ns) {
        super(DependencyKeys.DASHBOARD_PING);
        this.namespacesManager = namespacesManager;
        this.instance = instance;
        this.ns = ns;
    }

    @Override
    protected String run() throws Exception {
        String address = ServiceLocator.getInstance().buildAddress(
                instance.getListenAddress(), instance.getPingport()) + instance.getPingaddress();
        return new FeignBuilder(false).build(address, AdminMetrics.class).ping();
    }

    @Override
    protected String getFallback() {
        return "Unreachable";
    }
}
