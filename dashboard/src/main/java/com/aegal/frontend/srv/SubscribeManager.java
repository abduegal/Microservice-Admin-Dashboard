package com.aegal.frontend.srv;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.frontend.dto.SubscribeRequest;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.apache.curator.x.discovery.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

/**
 * Created by vagrant on 10/20/14.
 */
public class SubscribeManager implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscribeManager.class);

    private final Set<SubscribeRequest> subscriptions = Collections.synchronizedSet(new HashSet<SubscribeRequest>());

    private final int INTERVAL = 5000; // 5 seconds
    private final NamespacesManager namespacesManager;

    public SubscribeManager(NamespacesManager namespacesManager) {
        this.namespacesManager = namespacesManager;
    }

    public void addSubscription(SubscribeRequest request) {

        ServiceLocator serviceLocator = namespacesManager.getServiceLocator(request.namespace);
        if (serviceLocator == null) {
            throw new WebApplicationException(new IllegalArgumentException("namespace does not exist."), BAD_REQUEST);
        }

        if (subscriptions.contains(request)) {
            subscriptions.remove(request);
        }
        subscriptions.add(request);
    }

    @Override
    public void run() {

        while (true) {

            for (SubscribeRequest subscription : subscriptions) {
                System.out.println(subscription.active);
                System.out.println(subscription.isExpired());
                // check if an active connection exists:
                if (subscription.active) {
                    // check if the active connection should end
                    if (subscription.isExpired()) {
                        endConnection(subscription);
                        subscription.active = false;
                    }

                } else {
                    if (!subscription.isExpired()) {
                        createConnection(subscription); // create connection
                        subscription.active = true;
                    }
                }
            }

            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
            }
        }

    }

    public void createConnection(SubscribeRequest subscription) {
        try {
            ServiceLocator serviceLocator = namespacesManager.getServiceLocator(subscription.namespace);
            ServiceDiscovery<MicroserviceMetaData> serviceDiscovery = serviceLocator.getServiceDiscovery();
            serviceDiscovery.registerService(convert(subscription));
        } catch (Exception ex) {
            LOGGER.error("Unable to handle the Zookeeper request", ex);
        }

    }

    public void endConnection(SubscribeRequest subscription) {
        try {
            ServiceLocator serviceLocator = namespacesManager.getServiceLocator(subscription.namespace);
            ServiceDiscovery<MicroserviceMetaData> serviceDiscovery = serviceLocator.getServiceDiscovery();
            serviceDiscovery.unregisterService(convert(subscription));

        } catch (Exception ex) {
            LOGGER.error("Unable to handle the Zookeeper request", ex);
        }

    }

    public ServiceInstance<MicroserviceMetaData> convert(SubscribeRequest subscription) throws Exception {
        ServiceInstanceBuilder<MicroserviceMetaData> builder = ServiceInstance.builder();
        return builder.serviceType(ServiceType.DYNAMIC)
                .name(subscription.servicename)
                .address(subscription.address)
                .port(subscription.port)
                .payload(new MicroserviceMetaData(
                        UUID.nameUUIDFromBytes(subscription.toString().getBytes()),
                        subscription.address,
                        subscription.port,
                        subscription.version,
                        subscription.getAdminPort(),
                        subscription.logFileLocation,
                        subscription.getHealthcheckport(),
                        subscription.getHealthcheckaddress(),
                        subscription.getMetricsport(),
                        subscription.getMetricsaddress(),
                        subscription.getPingport(),
                        subscription.getPingaddress()))
                .build();
    }

}
