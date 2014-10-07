package com.aegal.frontend.srv;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.Connections;
import com.aegal.framework.core.util.Guard;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.dto.D3GraphDTO;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.yammer.tenacity.core.TenacityCommand;

import feign.FeignException;

import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;

import java.util.*;

/**
 * User: A.Egal
 * Date: 8/28/14
 * Time: 9:09 PM
 */
public class GraphDataGenerator extends TenacityCommand<List<D3GraphDTO<InstanceMetadata>>> {

    private static final Logger logger = LoggerFactory
	    .getLogger(GraphDataGenerator.class);
    
    private final ServiceLocator serviceLocator;
    private static Map<String, Integer> serviceNameGroupId = new HashMap<>();

    public GraphDataGenerator(ServiceLocator serviceLocator) {
        super(DependencyKeys.DASHBOARD_FIND_SERVICES);
        this.serviceLocator = serviceLocator;
    }

    @Override
    protected List<D3GraphDTO<InstanceMetadata>> run() throws Exception {
        Guard.notNull(serviceLocator);

        List<D3GraphDTO<InstanceMetadata>> result = new ArrayList<>();

        Map<String, Collection<ServiceInstance<InstanceMetadata>>> instances =
                serviceLocator.allInstancesMap();
        for (String servicename : instances.keySet()) {
            for (ServiceInstance<InstanceMetadata> instance : instances.get(servicename)) {
                D3GraphDTO<InstanceMetadata> graphDTO = new D3GraphDTO<InstanceMetadata>();

                buildGraphDto(graphDTO, instance, servicename, instances);

                result.add(graphDTO);
            }
        }
        return result;

    }

    private void buildGraphDto(D3GraphDTO<InstanceMetadata> graphDTO, ServiceInstance<InstanceMetadata> instance, String servicename,
                               Map<String, Collection<ServiceInstance<InstanceMetadata>>> instances) {

        graphDTO.id = instance.getPayload().getInstanceId().toString();
        graphDTO.group = groupForServiceName(servicename);
        graphDTO.name = servicename;
        graphDTO.location = String.format("%s:%s", instance.getAddress(), instance.getPort());
        graphDTO.size = 10 + 30 / instances.get(servicename).size();
        graphDTO.data = instance.getPayload();

        try {

            List<InstanceMetadata> instanceMetadatas =
                    serviceLocator.build(instance.getPayload(), Connections.class).getConnections();
            graphDTO.links = instanceMetadatas;

        } catch (WebApplicationException | FeignException e) {
            logger.error("Error while building the d3 graph: " + e.getMessage());
        }

    }

    private static int groupForServiceName(String serviceName) {
        Integer id = serviceNameGroupId.get(serviceName);
        if (id == null) {
            id = serviceNameGroupId.size();
            serviceNameGroupId.put(serviceName, id);
        }
        return id;
    }

}
