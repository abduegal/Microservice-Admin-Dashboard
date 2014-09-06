package com.aegal.frontend.resources;

import com.aegal.frontend.FrontendConfig;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.io.File;
import java.util.Collection;

/**
 * Deploys new applications.
 * User: A.Egal
 * Date: 8/14/14
 * Time: 10:21 PM
 */
@Path("/deployment")
public class DeployResource {

    private FrontendConfig config;

    public DeployResource(FrontendConfig config) {
        this.config = config;
    }

    @GET
    @Path("namespaces")
    @Produces("application/json")
    public Collection<String> getNameSpaces() {
        return config.getNamespaces();
    }

}
