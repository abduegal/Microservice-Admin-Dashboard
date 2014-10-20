package com.aegal.frontend.srv;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.frontend.dto.SubscribeRequest;
import com.ge.snowizard.discovery.core.InstanceMetadata;
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

        ServiceLocator serviceLocator = namespacesManager.getServiceLocator(request.getNamespace());
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
                // check if an active connection exists:
                if (subscription.isActive()) {
                    // check if the active connection should end
                    if (subscription.isExpired()) {
                        endConnection(subscription);
                        subscription.setActive(false);
                    }

                } else {
                    if (!subscription.isExpired()) {
                        createConnection(subscription); // create connection
                        subscription.setActive(true);
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
            ServiceLocator serviceLocator = namespacesManager.getServiceLocator(subscription.getNamespace());
            ServiceDiscovery<InstanceMetadata> serviceDiscovery = serviceLocator.getServiceDiscovery();
            serviceDiscovery.registerService(convert(subscription));
        } catch (Exception ex) {
            LOGGER.error("Unable to handle the Zookeeper request", ex);
        }

    }

    public void endConnection(SubscribeRequest subscription) {
        try {
            ServiceLocator serviceLocator = namespacesManager.getServiceLocator(subscription.getNamespace());
            ServiceDiscovery<InstanceMetadata> serviceDiscovery = serviceLocator.getServiceDiscovery();
            serviceDiscovery.unregisterService(convert(subscription));

        } catch (Exception ex) {
            LOGGER.error("Unable to handle the Zookeeper request", ex);
        }

    }

    public ServiceInstance<InstanceMetadata> convert(SubscribeRequest subscription) throws Exception {
        ServiceInstanceBuilder<InstanceMetadata> builder = ServiceInstance.builder();
        return builder.serviceType(ServiceType.DYNAMIC)
                .name(subscription.getServicename())
                .address(subscription.getAddress())
                .port(subscription.getPort())
                .payload(new InstanceMetadata(UUID.nameUUIDFromBytes(subscription.toString().getBytes()), subscription.getAddress(), subscription.getPort()))
                .build();
    }

}
