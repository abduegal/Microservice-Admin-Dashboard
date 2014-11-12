package com.aegal.frontend.commands;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.Connections;
import com.aegal.framework.core.exceptions.ServiceCallException;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.dto.D3GraphDTO;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.yammer.tenacity.core.TenacityCommand;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by vagrant on 11/12/14.
 */
public class BuildGraphServiceCommand extends TenacityCommand<List<D3GraphDTO<InstanceMetadata>>> {

    private static final Logger logger = LoggerFactory.getLogger(BuildGraphServiceCommand.class);
    private final ServiceLocator serviceLocator;
    private static Map<String, Integer> serviceNameGroupId = new HashMap<>();

    public BuildGraphServiceCommand(ServiceLocator serviceLocator) {
        super(DependencyKeys.DASHBOARD_BUILD_GRAPH);
        this.serviceLocator = serviceLocator;
    }

    @Override
    protected List<D3GraphDTO<InstanceMetadata>> run() throws Exception {
        List<D3GraphDTO<InstanceMetadata>> result = new ArrayList<>();

        Map<String, Collection<ServiceInstance<InstanceMetadata>>> instances = serviceLocator.allInstancesMap();
        for (String servicename : instances.keySet()) {
            for (ServiceInstance<InstanceMetadata> instance : instances.get(servicename)) {
                D3GraphDTO<InstanceMetadata> graphDTO = new D3GraphDTO<InstanceMetadata>();

                buildGraphDto(graphDTO, instance, servicename, instances);

                result.add(graphDTO);
            }
        }
        return result;
    }

    private void buildGraphDto(final D3GraphDTO<InstanceMetadata> graphDTO,
                               final ServiceInstance<InstanceMetadata> instance, String servicename,
                               final Map<String, Collection<ServiceInstance<InstanceMetadata>>> instances) throws ServiceCallException {

        graphDTO.id = instance.getPayload().getInstanceId().toString();
        graphDTO.group = groupForServiceName(servicename);
        graphDTO.name = servicename;
        graphDTO.location = String.format("%s:%s", instance.getAddress(), instance.getPort());
        graphDTO.size = 10 + 30 / instances.get(servicename).size();
        graphDTO.data = instance.getPayload();

        graphDTO.links = new AddConnectionInformationCommand(instance).execute();

    }

    private static int groupForServiceName(String serviceName) {
        Integer id = serviceNameGroupId.get(serviceName);
        if (id == null) {
            id = serviceNameGroupId.size();
            serviceNameGroupId.put(serviceName, id);
        }
        return id;
    }

    protected class AddConnectionInformationCommand extends TenacityCommand<List<InstanceMetadata>> {
        private final ServiceInstance<InstanceMetadata> instance;

        public AddConnectionInformationCommand(final ServiceInstance<InstanceMetadata> instance) {
            super(DependencyKeys.DASHBOARD_FIND_CONNECTIONS);
            this.instance = instance;
        }

        @Override
        protected List<InstanceMetadata> run() throws Exception {
            return serviceLocator.build(instance.getPayload(), Connections.class).getConnections();
        }

        @Override
        protected List<InstanceMetadata> getFallback() {
            logger.error("Error while building the d3 graph: ");
            return new ArrayList<>();
        }
    }

}