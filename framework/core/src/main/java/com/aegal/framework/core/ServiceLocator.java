package com.aegal.framework.core;


import com.aegal.framework.core.discovery.MicroserviceDiscoveryBundle;
import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.framework.core.exceptions.ServiceCallException;
import io.dropwizard.discovery.client.DiscoveryClient;
import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Helps locating services.
 * User: A.Egal
 * Date: 8/12/14
 * Time: 8:37 PM
 */
public class ServiceLocator {

    private static final Logger logger = LoggerFactory.getLogger(ServiceLocator.class);

    private static ServiceLocator DEFAULTINSTANCE;
    private MicroserviceDiscoveryBundle discoveryBundle;
    private ServiceDiscovery<MicroserviceMetaData> serviceDiscovery;

    public String find(String serviceName) throws ServiceCallException {
        try {

            DiscoveryClient discoveryClient = discoveryBundle.getDiscoveryClient(serviceName);
            ServiceInstance instance = discoveryClient.getInstance();
            return buildAddress(instance.getAddress(), instance.getPort());

        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    public String findAdmin(String serviceName) throws ServiceCallException {
        try {

            DiscoveryClient<MicroserviceMetaData> discoveryClient = discoveryBundle.getDiscoveryClient(serviceName);
            ServiceInstance<MicroserviceMetaData> instance = discoveryClient.getInstance();
            return buildAddress(instance.getAddress(), instance.getPayload().getAdminPort());


        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    public String find(String serviceName, String protocol) throws ServiceCallException {
        try {

            DiscoveryClient discoveryClient = discoveryBundle.getDiscoveryClient(serviceName);
            ServiceInstance instance = discoveryClient.getInstance();
            return buildAddress(protocol, instance.getAddress(), instance.getPort());

        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    public String findAdmin(String serviceName, String protocol) throws ServiceCallException {
        try {

            DiscoveryClient<MicroserviceMetaData> discoveryClient = discoveryBundle.getDiscoveryClient(serviceName);
            ServiceInstance<MicroserviceMetaData> instance = discoveryClient.getInstance();
            return buildAddress(protocol, instance.getAddress(), instance.getPayload().getAdminPort());
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    public String buildAddress(String protocol, String address, int port) {
        return String.format("%s://%s:%s", protocol, address, port);
    }

    public String buildAddress(String address, int port) {
        return String.format("http://%s:%s", address, port);
    }


    /**
     * Returns all serviceinstances of the microservices with the given name.
     * @param servicename the servicename.
     */
    public Collection<ServiceInstance<MicroserviceMetaData>> instances(String servicename) throws ServiceCallException {
        try {
            DiscoveryClient<MicroserviceMetaData> discoveryClient = discoveryBundle.getDiscoveryClient(servicename);
            return discoveryClient.getInstances();
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    /**
     * Returns all serviceinstances of all microservices registered with zookeeper.
     */
    public Collection<ServiceInstance<MicroserviceMetaData>> allInstances() throws ServiceCallException {
        try {
            Collection<ServiceInstance<MicroserviceMetaData>> result = new ArrayList<>();
            Collection<String> services = serviceDiscovery.queryForNames();
            if (services.size() == 0) {
                return new ArrayList<>();
            }
            for (String service : services) {
                result.addAll(allInstances(service));
            }
            return result;
        }catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    /**
     * Returns all serviceinstances of all microservices registered with zookeeper mapped by name (type).
     */
    public Map<String, Collection<ServiceInstance<MicroserviceMetaData>>> allInstancesMap()
            throws ServiceCallException {
        try {
            Map<String, Collection<ServiceInstance<MicroserviceMetaData>>> map = new HashMap<>();
            Collection<String> services = serviceDiscovery.queryForNames();
            for (String service : services) {
                map.put(service, allInstances(service));
            }

            return map;
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    private Collection<ServiceInstance<MicroserviceMetaData>> allInstances(String servicename) throws Exception{
        DiscoveryClient<MicroserviceMetaData> client = new DiscoveryClient<>(servicename,
                serviceDiscovery,
                new DownInstancePolicy(),
                new RoundRobinStrategy<MicroserviceMetaData>());
        client.start();
        Collection<ServiceInstance<MicroserviceMetaData>> instances = client.getInstances();
        client.close();
        return instances;
    }

    public static void createInstance(MicroserviceDiscoveryBundle discoveryBundle) {
        DEFAULTINSTANCE = new ServiceLocator();
        DEFAULTINSTANCE.discoveryBundle = discoveryBundle;
    }

    /**
     * Returns the default instance.
     */
    public static ServiceLocator getInstance() {
        return DEFAULTINSTANCE;
    }

    public ServiceDiscovery<MicroserviceMetaData> getServiceDiscovery() {
        return discoveryBundle != null ? discoveryBundle.getServiceDiscovery() : serviceDiscovery;
    }

    /**
     * Returns the default instance.
     */
    public static ServiceLocator getInstance(ServiceDiscovery serviceDiscovery) {
        ServiceLocator serviceLocator = new ServiceLocator();
        serviceLocator.serviceDiscovery = serviceDiscovery;
        return serviceLocator;
    }
}
