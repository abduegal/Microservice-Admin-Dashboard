package com.aegal.framework.core.resources;

import io.dropwizard.jetty.MutableServletContextHandler;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * returns the admin port.
 * User: A.Egal
 * Date: 8/10/14
 * Time: 6:01 PM
 */
@Path("/api/adminport")
public class AdminPortResource {

    private MutableServletContextHandler servletContextHandler;

    public AdminPortResource(MutableServletContextHandler servletContextHandler) {
        this.servletContextHandler = servletContextHandler;
    }

    @GET
    @Produces("application/json")
    public Integer get() throws Exception {
        final ServerSocketChannel channel =
                (ServerSocketChannel) servletContextHandler.getServer().getConnectors()[1].getTransport();
        final InetSocketAddress socket = (InetSocketAddress) channel.getLocalAddress();
        return socket.getPort();
    }
}
