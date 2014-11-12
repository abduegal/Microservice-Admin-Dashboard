package com.aegal.frontend.resources;

import com.aegal.frontend.dto.SubscribeRequest;
import com.aegal.frontend.srv.SubscribeManager;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
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
     *
     * @param servicename the servicename
     * @param address     the location of the service I.E. http://192.168.1.2
     * @param port        I.E. 1234
     * @param timeToLive  time to live in seconds (default 120 seconds)
     * @return "ok" or "failure"
     */
    @POST
    @Path("{ns}/{servicename}/{address}/{port}/{ttl}")
    public Response subscribe(@PathParam("ns") String nameserver,
                              @PathParam("servicename") String servicename,
                              @PathParam("address") String address,
                              @PathParam("port") int port,
                              @DefaultValue("120") @PathParam("ttl") int timeToLive) {
        subscribeManager.addSubscription(new SubscribeRequest(servicename, address, port, nameserver, timeToLive));

        return Response.ok("ok").build();
    }

}
