package com.aegal.frontend.resources;

import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.framework.core.api.LogFile;
import com.aegal.frontend.dto.D3GraphDTO;
import com.aegal.frontend.srv.GraphDataGenerator;
import com.aegal.frontend.srv.NamespacesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.ge.snowizard.discovery.core.InstanceMetadata;

import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * User: A.Egal
 * Date: 8/13/14
 * Time: 7:37 PM
 */
@Path("/overview/{namespace}")
public class OverviewResource {

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
    public JsonNode healthcheck(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws
            Exception {
        return namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).healthcheck();
    }

    @POST
    @Path("metrics")
    @Consumes("application/json")
    public JsonNode findMetrics(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws Exception {
        return namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).metrics();
    }

    @POST
    @Path("logfile/{lines}")
    @Consumes("application/json")
    public javax.ws.rs.core.Response findLogFile(@PathParam("namespace") String ns,
                                                 @PathParam("lines") Integer lines,
                                                 InstanceMetadata serviceInstance) throws Exception {
        String logfile = namespacesManager.getServiceLocator(ns).buildNoSerialize(serviceInstance, LogFile.class).getLogFile();

        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {

                String[] buffer = new String[lines];

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(logfile));
                    char suffix = '\n';

                    int bufferStartIndex = 0;
                    for (String line; (line = br.readLine()) != null;) {
                        buffer[bufferStartIndex++ % buffer.length] = line + suffix;
                    }

                    for (String s : buffer) {
                        outputStream.write(s.getBytes());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null)br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        return javax.ws.rs.core.Response.ok(stream).build();

    }

    @POST
    @Path("ping")
    @Consumes("application/json")
    public String ping(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws Exception {
        return namespacesManager.getServiceLocator(ns).buildAdminNoSerialize(serviceInstance, AdminMetrics.class).ping();
    }

}
