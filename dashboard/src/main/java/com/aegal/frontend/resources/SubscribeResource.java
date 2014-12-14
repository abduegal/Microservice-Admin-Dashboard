package com.aegal.frontend.resources;

import com.aegal.frontend.dto.SubscribeRequest;
import com.aegal.frontend.srv.SubscribeManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by vagrant on 10/20/14.
 * Enables other (micro)services to subscribe to this dashboard.
 */
@Path("/subscribe")
public class SubscribeResource {

    private SubscribeManager subscribeManager;

    public SubscribeResource(SubscribeManager subscribeManager) {
        this.subscribeManager = subscribeManager;
    }

    /**
     * Enables other services to subscribe.
     * You must repeat calling this resource before the TTL limit expires.
     * {
     *   "servicename": "servicename",
     *   "address": "localhost",
     *   "port": "12345",
     *   "namespace": "myapp",
     *   "timeToLive": "60",
     *   "version": "1.0",
     *   "logFileLocation": "/tmp/logfile.log",
     *   "adminPort": "54321"
     * }
     */
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response subscribe(SubscribeRequest subscribeRequest) {
        subscribeManager.addSubscription(subscribeRequest);

        return Response.ok("ok").build();
    }

}
