package com.aegal.frontend.resources;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.framework.core.api.LogFile;
import com.aegal.frontend.FrontendConfig;
import com.aegal.frontend.dto.D3GraphDTO;
import com.aegal.frontend.srv.GraphDataGenerator;
import com.aegal.frontend.srv.NamespacesManager;
import com.aegal.frontend.srv.ReadLogFileCommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import feign.FeignException;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import java.util.List;

/**
 * User: A.Egal
 * Date: 8/13/14
 * Time: 7:37 PM
 */
@Path("/overview/{namespace}")
public class OverviewResource {


    private static final Logger logger = LoggerFactory
            .getLogger(OverviewResource.class);

    private NamespacesManager namespacesManager;

    public OverviewResource(NamespacesManager namespacesManager) {
        this.namespacesManager = namespacesManager;
    }

    @GET
    @Path("findservices")
    public List<D3GraphDTO<InstanceMetadata>> findServices(@PathParam("namespace") String ns) {
        return new GraphDataGenerator(namespacesManager.getServiceLocator(ns)).execute();
    }

    @POST
    @Path("healthcheck")
    @Consumes("application/json")
    public JsonNode healthcheck(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws Exception {

        ObjectNode parentNode = new ObjectMapper().createObjectNode();
        try {
            JsonNode healthcheck = namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).healthcheck();
            parentNode.set("healthCheckResponse", healthcheck);
            return parentNode;
        } catch (FeignException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("FainException while getting healthcheck information. This normally means only a 500 status because of unhealthy service. Details: " + e.getMessage());
            }

            //try to parse the json body from the error message.
            //seems to be the only way to the body of a 500 response
            String msg = e.getMessage();
            String feignBodyPrefix = "; content:";

            if (msg.indexOf(feignBodyPrefix) > 0) {
                String jsonHealthResponse = msg.substring(msg.indexOf(feignBodyPrefix) + feignBodyPrefix.length());
                parentNode.set("healthCheckResponse", new ObjectMapper().readTree(jsonHealthResponse));
                return parentNode;
            } else {
                logger.error("FainException while getting healthcheck information. Unable to parse the healt hcheck result from the message: " + e.getMessage());
                throw e;
            }
        }
    }

    @POST
    @Path("metrics")
    @Consumes("application/json")
    public JsonNode findMetrics(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws Exception {
        return namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).metrics();
    }

    /**
     * A method to display values of a given metric field name for all known services.
     *
     * @param ns the namespace as it is configured in {@link FrontendConfig#getNamespaces()}.
     * @param filter the filter is the name of the property key. No wildcards are allowed only exact matches.
     * @return the filter result. Example call and result:
     *
     * <pre>http://yourserver:andPort/api/overview/{namespace}/metric-filter?filter=jvm.threads.runnable.count,org.apache.http.conn.ClientConnectionManager.Client.pending-connections</pre>
     *
     * <pre>{
    "filterResult": [
    {
    "service": {
    "name": "myService1",
    "baseUrl": "http://10.10.130.195:8091"
    },
    "metrics": {
    "jvm.threads.runnable.count": {
    "value": 24
    },
    "org.apache.http.conn.ClientConnectionManager.Client.pending-connections": null
    }
    },
    {
    "service": {
    "name": "myService2",
    "baseUrl": "http://10.10.230.195:8082"
    },
    "metrics": {
    "jvm.threads.runnable.count": {
    "value": 25
    },
    "org.apache.http.conn.ClientConnectionManager.Client.pending-connections": {
    "value": 0
    }
    }
    }
    ]
    }</pre>
     *
     * @throws Exception the exception in case of any problems
     */
    @GET
    @Path("metric-filter")
    @Produces("application/json")
    public JsonNode filterMetrics(@PathParam("namespace") String ns, @QueryParam("filter") String filter) throws Exception {

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
        return mapper.createObjectNode().set("filterResult", arrayNode);
    }

    @POST
    @Path("logfile/{lines}")
    @Consumes("application/json")
    public javax.ws.rs.core.Response findLogFile(@PathParam("namespace") final String ns,
                                                 @PathParam("lines") final Integer lines,
                                                 final InstanceMetadata serviceInstance) throws Exception {
        final String logfile =
                namespacesManager.getServiceLocator(ns).buildNoSerialize(serviceInstance, LogFile.class).getLogFile();
        return new ReadLogFileCommand(logfile, lines).execute();
    }

    @POST
    @Path("ping")
    @Consumes("application/json")
    public String ping(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws Exception {
        return namespacesManager.getServiceLocator(ns).buildAdminNoSerialize(serviceInstance, AdminMetrics.class).ping();
    }

}
