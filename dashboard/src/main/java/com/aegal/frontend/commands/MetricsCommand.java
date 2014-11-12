package com.aegal.frontend.commands;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.dto.MetricsDTO;
import com.aegal.frontend.srv.NamespacesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.yammer.tenacity.core.TenacityCommand;
import org.apache.curator.x.discovery.ServiceInstance;

/**
 * Created by vagrant on 11/12/14.
 */
public class MetricsCommand extends TenacityCommand<MetricsDTO> {

    public enum Mode {
        FINDONE, SEARCH
    }

    private Mode mode;
    private final String ns;
    private InstanceMetadata serviceInstance;
    private final NamespacesManager namespacesManager;
    private String filter;

    private MetricsCommand(final String ns,
                           final NamespacesManager namespacesManager) {
        super(DependencyKeys.DASHBOARD_METRICS);
        this.ns = ns;
        this.namespacesManager = namespacesManager;
    }

    @Override
    protected MetricsDTO run() throws Exception {
        if (mode.equals(Mode.FINDONE)) {
            return MetricsDTO.from(
                    namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).metrics()
            );
        } else if (mode.equals(Mode.SEARCH)) {
            return search();
        } else {
            throw new IllegalArgumentException("unsupported mode");
        }
    }

    @Override
    protected MetricsDTO getFallback() {
        MetricsDTO metricsDTO = new MetricsDTO();
        metricsDTO.didRun = false;
        return metricsDTO;
    }

    /**
     * A method to display values of a given metric field name for all known services.
     *
     * @return the filter result. Example call and result:
     * <p>
     * <pre>http://yourserver:andPort/api/overview/{namespace}/metric-filter?filter=jvm.threads.runnable.count,org.apache.http.conn.ClientConnectionManager.Client.pending-connections</pre>
     * <p>
     * <pre>{
     * "filterResult": [
     * {
     * "service": {
     * "name": "myService1",
     * "baseUrl": "http://10.10.130.195:8091"
     * },
     * "metrics": {
     * "jvm.threads.runnable.count": {
     * "value": 24
     * },
     * "org.apache.http.conn.ClientConnectionManager.Client.pending-connections": null
     * }
     * },
     * {
     * "service": {
     * "name": "myService2",
     * "baseUrl": "http://10.10.230.195:8082"
     * },
     * "metrics": {
     * "jvm.threads.runnable.count": {
     * "value": 25
     * },
     * "org.apache.http.conn.ClientConnectionManager.Client.pending-connections": {
     * "value": 0
     * }
     * }
     * }
     * ]
     * }</pre>
     * @throws Exception the exception in case of any problems
     */
    private MetricsDTO search() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayNode arrayNode = mapper.createArrayNode();

        ServiceLocator serviceLocator = namespacesManager.getServiceLocator(ns);
        for (ServiceInstance<InstanceMetadata> s : serviceLocator.allInstances()) {

            ObjectNode resultNode = mapper.createObjectNode();
            //set service node
            ObjectNode serviceNode = mapper.createObjectNode();
            serviceNode.set("name", TextNode.valueOf(s.getName()));
            boolean sslAvailable = s.getSslPort() != null && s.getSslPort() > 0;
            serviceNode.set("baseUrl", TextNode.valueOf((sslAvailable ? "https" : "http") + "://" + s.getAddress() + ":" + (sslAvailable ? s.getSslPort() : s.getPort())));
            resultNode.set("service", serviceNode);

            //get metrics node with subnotes named like filter parts
            JsonNode metrics = namespacesManager.getServiceLocator(ns).buildAdmin(s.getPayload(), AdminMetrics.class).metrics();
            ObjectNode metricsNode = mapper.createObjectNode();

            for (String filterPart : Splitter.on(',').omitEmptyStrings().trimResults().splitToList(filter)) {
                JsonNode filterResult = metrics.findValue(filterPart);
                metricsNode.set(filterPart, Objects.firstNonNull(filterResult, MissingNode.getInstance()));
            }

            resultNode.set("metrics", metricsNode);
            arrayNode.add(resultNode);
        }
        return MetricsDTO.from(mapper.createObjectNode().set("filterResult", arrayNode));
    }


    public static MetricsCommandCreator create(final String ns, final NamespacesManager namespacesManager) {
        return new MetricsCommandCreator(ns, namespacesManager);
    }

    public static class MetricsCommandCreator {

        MetricsCommand metricsCommand;

        public MetricsCommandCreator(final String ns, final NamespacesManager namespacesManager) {
            metricsCommand = new MetricsCommand(ns, namespacesManager);
        }

        public MetricsCommand search(String filter) {
            metricsCommand.mode = Mode.SEARCH;
            metricsCommand.filter = filter;
            return metricsCommand;
        }

        public MetricsCommand findOne(InstanceMetadata serviceInstance) {
            metricsCommand.mode = Mode.FINDONE;
            metricsCommand.serviceInstance = serviceInstance;
            return metricsCommand;
        }

    }

}
