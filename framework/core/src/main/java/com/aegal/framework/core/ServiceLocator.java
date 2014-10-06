package com.aegal.framework.core;

import com.aegal.framework.core.api.AdminPort;
import com.aegal.framework.core.exceptions.ServiceCallException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.zookeeper.KeeperException.NoNodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Helps locating services.
 * User: A.Egal
 * Date: 8/12/14
 * Time: 8:37 PM
 */
public class ServiceLocator {

    private static final Logger logger = LoggerFactory
	    .getLogger(ServiceLocator.class);
    
    /**
     * Very easy round-robin like implementation.
     */
    private static Map<String, Integer> roundRobin = new HashMap<>();
    private static ServiceLocator DEFAULTINSTANCE;
    private ServiceDiscovery<InstanceMetadata> discovery;
    private ObjectMapper objectMapper;

    private List<InstanceMetadata> connections = new ArrayList<>();

    /**
     * Constructs an API interface, for remote http calls with Feign.
     */
    public <T> T build(String servicename, Class<T> clazz) throws ServiceCallException {
        ServiceInstance<InstanceMetadata> instance = pickRandomService(servicename);
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .encoder(new JacksonEncoder(objectMapper))
                .target(clazz, getAdress(instance.getPayload()));
    }

    /**
     * Constructs an API interface, for remote http calls with Feign.
     */
    public <T> T build(InstanceMetadata instanceMetadata, Class<T> clazz) throws ServiceCallException {
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .encoder(new JacksonEncoder(objectMapper))
                .target(clazz, getAdress(instanceMetadata));
    }

    /**
     * Constructs an API interface, using the admin port, for remote http calls with Feign.
     */
    public <T> T buildAdmin(InstanceMetadata instanceMetadata, Class<T> clazz) throws ServiceCallException {
        AdminPort adminPort = build(instanceMetadata, AdminPort.class);
        Integer port = adminPort.getAdminPort();
        return Feign.builder()
                .decoder(new JacksonDecoder(objectMapper))
                .encoder(new JacksonEncoder(objectMapper))
                .target(clazz, getAdress(instanceMetadata, port));
    }

    /**
     * Constructs an API interface, using the admin port, for remote http calls with Feign.
     */
    public <T> T buildNoSerialize(InstanceMetadata instanceMetadata, Class<T> clazz) throws ServiceCallException {
        return Feign.builder()
                .target(clazz, getAdress(instanceMetadata));
    }

    /**
     * Constructs an API interface, using the admin port, for remote http calls with Feign.
     */
    public <T> T buildAdminNoSerialize(InstanceMetadata instanceMetadata, Class<T> clazz) throws ServiceCallException {
        AdminPort adminPort = build(instanceMetadata, AdminPort.class);
        Integer port = adminPort.getAdminPort();
        return Feign.builder()
                .target(clazz, getAdress(instanceMetadata, port));
    }

    protected String getAdress(InstanceMetadata instance){
        return String.format("http://%s:%d", instance.getListenAddress(), instance.getListenPort());
    }

    protected String getAdress(InstanceMetadata instance, Integer port){
        return String.format("http://%s:%d", instance.getListenAddress(), port);
    }

    /**
     * Returns all serviceinstances of the microservices with the given name.
     * @param servicename the servicename.
     */
    public Collection<ServiceInstance<InstanceMetadata>> instances(String servicename) throws ServiceCallException {
        try {
            return discovery.queryForInstances(servicename);
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    /**
     * Returns all serviceinstances of all microservices registered with zookeeper.
     */
    public Collection<ServiceInstance<InstanceMetadata>> allInstances() throws ServiceCallException {
        try {
            Collection<ServiceInstance<InstanceMetadata>> result = new ArrayList<>();
            Collection<String> services = discovery.queryForNames();
            if (services.size() == 0) {
                return new ArrayList<>();
            }
            for (String service : services) {
                result.addAll(instances(service));
            }
            return result;
        }catch(NoNodeException nne){
            logger.info("No nodes found. Details: " + nne.getMessage());
            return Collections.emptyList();
        }catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    /**
     * Returns all serviceinstances of all microservices registered with zookeeper mapped by name (type).
     */
    public Map<String, Collection<ServiceInstance<InstanceMetadata>>> allInstancesMap()
            throws ServiceCallException {
        try {
            Map<String, Collection<ServiceInstance<InstanceMetadata>>> map = new HashMap<>();
            Collection<String> services = discovery.queryForNames();
            for (String service : services) {
                map.put(service, instances(service));
            }

            return map;
        }catch(NoNodeException nne){
            logger.info("No nodes found. Details: " + nne.getMessage());
            return Collections.emptyMap();
        } catch (Exception e) {
            throw new ServiceCallException(e);
        }
    }

    private ServiceInstance<InstanceMetadata> pickRandomService(String servicename) throws ServiceCallException {

        try {
            ArrayList<ServiceInstance<InstanceMetadata>> instances
                    = new ArrayList<>(discovery.queryForInstances(servicename));

            ServiceInstance<InstanceMetadata> instance = instances.get(counter(servicename) % instances.size());

            connections.add(instance.getPayload());
            return instance;

        } catch (Exception e) {
            throw new ServiceCallException(e);
        }

    }

    private Integer counter(String servicename) {
        Integer count = 0;
        if(roundRobin.containsKey(servicename)) {
            count = roundRobin.get(servicename);
        }
        count++;
        roundRobin.put(servicename, count);
        return count;
    }

    public List<InstanceMetadata> getConnections() {
        return connections;
    }

    public static void createInstance(ServiceDiscovery discovery, ObjectMapper objectMapper) {
        DEFAULTINSTANCE = new ServiceLocator();
        DEFAULTINSTANCE.discovery = discovery;
        DEFAULTINSTANCE.objectMapper = objectMapper;
    }

    /**
     * Returns the default instance.
     */
    public static ServiceLocator getInstance() {
        return DEFAULTINSTANCE;
    }

    public static ServiceLocator getInstance(ServiceDiscovery discovery, ObjectMapper objectMapper) {
        ServiceLocator serviceLocator = new ServiceLocator();
        serviceLocator.discovery = discovery;
        serviceLocator.objectMapper = objectMapper;
        return serviceLocator;
    }

}
