package com.aegal.frontend.commands;

import com.aegal.framework.core.FeignBuilder;
import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.framework.core.api.Connections;
import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.dto.HealthCheckDTO;
import com.aegal.frontend.srv.NamespacesManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yammer.tenacity.core.TenacityCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by vagrant on 11/12/14.
 */
public class HealthCheckCommand extends TenacityCommand<HealthCheckDTO> {

    private static final Logger logger = LoggerFactory.getLogger(HealthCheckCommand.class);

    private final String ns;
    private final MicroserviceMetaData instance;
    private final NamespacesManager namespacesManager;
    private final HealthCheckDTO healthCheckDTO = new HealthCheckDTO();

    public HealthCheckCommand(final String ns,
                              final MicroserviceMetaData instance,
                              final NamespacesManager namespacesManager) {
        super(DependencyKeys.DASHBOARD_HEALTCHECKS);
        this.ns = ns;
        this.instance = instance;
        this.namespacesManager = namespacesManager;
    }

    @Override
    protected HealthCheckDTO run() throws Exception {
        String address = ServiceLocator.getInstance().buildAddress(
                instance.getListenAddress(), instance.getHealthcheckport()) + instance.getHealthcheckaddress();
        healthCheckDTO.healthCheckResponse = new FeignBuilder().build(address, AdminMetrics.class).healthcheck();
        return healthCheckDTO;
    }

    @Override
    protected HealthCheckDTO getFallback() {
        if (logger.isDebugEnabled()) {
            logger.debug("FeignException while getting healthcheck information. This normally means only a 500 status because of unhealthy service.");
        }

        //try to parse the json body from the error message. Seems to be the only way to the body of a 500 response
        String msg = getFailedExecutionException().getMessage();
        String feignBodyPrefix = "; content:";

        try {
            if (msg.indexOf(feignBodyPrefix) > 0) {
                String jsonHealthResponse = msg.substring(msg.indexOf(feignBodyPrefix) + feignBodyPrefix.length());
                healthCheckDTO.healthCheckResponse = new ObjectMapper().readTree(jsonHealthResponse);
                return healthCheckDTO;
            }
        } catch (IOException e) {
        }

        logger.error("FeignException while getting healthcheck information. Unable to parse the healthcheck result from the message: " + msg);
        healthCheckDTO.didRun = false;
        return healthCheckDTO;
    }
}
