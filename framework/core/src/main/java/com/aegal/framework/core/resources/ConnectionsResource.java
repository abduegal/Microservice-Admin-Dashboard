package com.aegal.framework.core.resources;

import com.aegal.framework.core.discovery.MicroserviceMetaData;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.List;

/**
 * Keeps track of connections made by this service.
 * User: A.Egal
 * Date: 8/31/14
 * Time: 9:56 PM
 */
@Path("/api/connections")
public class ConnectionsResource {

    @GET
    @Produces("application/json")
    public List<MicroserviceMetaData> getConnections() {
        return new ArrayList<>(); // ServiceLocator.getInstance().getConnections();
    }

}
