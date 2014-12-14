package com.aegal.frontend.resources;

import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.frontend.commands.*;
import com.aegal.frontend.dto.D3GraphDTO;
import com.aegal.frontend.dto.HealthCheckDTO;
import com.aegal.frontend.dto.MetricsDTO;
import com.aegal.frontend.srv.NamespacesManager;
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
    public List<D3GraphDTO<MicroserviceMetaData>> findServices(@PathParam("namespace") String ns) {
        return new BuildGraphServiceCommand(namespacesManager.getServiceLocator(ns)).execute();
    }

    @POST
    @Path("healthcheck")
    @Consumes("application/json")
    public HealthCheckDTO healthcheck(@PathParam("namespace") String ns, MicroserviceMetaData serviceInstance) {
        return new HealthCheckCommand(ns, serviceInstance, namespacesManager).execute();
    }

    @POST
    @Path("metrics")
    @Consumes("application/json")
    public MetricsDTO findMetrics(@PathParam("namespace") String ns, MicroserviceMetaData serviceInstance) {
        return MetricsCommand.create(ns, namespacesManager).findOne(serviceInstance).execute();
    }


    @GET
    @Path("metric-filter")
    @Produces("application/json")
    public MetricsDTO filterMetrics(@PathParam("namespace") String ns, @QueryParam("filter") String filter) throws Exception {
        return MetricsCommand.create(ns, namespacesManager).search(filter).execute();
    }

    @POST
    @Path("logfile/{lines}")
    @Consumes("application/json")
    public javax.ws.rs.core.Response findLogFile(@PathParam("namespace") final String ns,
                                                 @PathParam("lines") final Integer lines,
                                                 final MicroserviceMetaData serviceInstance) throws Exception {
        return new LogFileCommand(ns, lines, serviceInstance, namespacesManager).execute();
    }

    @POST
    @Path("ping")
    @Consumes("application/json")
    public String ping(@PathParam("namespace") String ns, MicroserviceMetaData serviceInstance) throws Exception {
        return new PingCommand(namespacesManager, serviceInstance, ns).execute();
    }

}
