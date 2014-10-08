package com.aegal.frontend.resources;

import com.aegal.framework.core.api.AdminMetrics;
import com.aegal.framework.core.api.LogFile;
import com.aegal.frontend.dto.D3GraphDTO;
import com.aegal.frontend.srv.GraphDataGenerator;
import com.aegal.frontend.srv.NamespacesManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ge.snowizard.discovery.core.InstanceMetadata;

import feign.FeignException;

import javax.ws.rs.*;
import javax.ws.rs.core.StreamingOutput;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public JsonNode healthcheck(@PathParam("namespace") String ns, InstanceMetadata serviceInstance) throws
    Exception {

	ObjectNode parentNode = new ObjectMapper().createObjectNode();
	try{
	    JsonNode healthcheck = namespacesManager.getServiceLocator(ns).buildAdmin(serviceInstance, AdminMetrics.class).healthcheck();
	    parentNode.set("healthCheckResponse", healthcheck);
	    return parentNode;
	}catch(FeignException e){
	    if(logger.isDebugEnabled()){
		logger.debug("FainException while getting healthcheck information. This normally means only a 500 status because of unhealthy service. Details: " +  e.getMessage());
	    }
	    
	    //try to parse the json body from the error message.
	    //seems to be the only way to the body of a 500 response
	    String msg = e.getMessage();
	    String feignBodyPrefix = "; content:";

	    if(msg.indexOf(feignBodyPrefix)>0){
		String jsonHealthResponse = msg.substring(msg.indexOf(feignBodyPrefix) + feignBodyPrefix.length());
		parentNode.set("healthCheckResponse", new ObjectMapper().readTree(jsonHealthResponse));
		return parentNode;
	    }else{
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
