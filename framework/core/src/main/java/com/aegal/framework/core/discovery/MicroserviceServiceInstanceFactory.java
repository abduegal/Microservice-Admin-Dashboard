package com.aegal.framework.core.discovery;

import io.dropwizard.discovery.core.CuratorAdvertiser;
import io.dropwizard.discovery.core.ServiceInstanceFactory;
import io.dropwizard.jetty.MutableServletContextHandler;
import org.apache.curator.x.discovery.ServiceInstance;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

/**
 * Created by vagrant on 12/13/14.
 */
public class MicroserviceServiceInstanceFactory implements ServiceInstanceFactory<MicroserviceMetaData> {

    private final String version;
    private final MutableServletContextHandler adminContext;
    private final String logfileName;

    public MicroserviceServiceInstanceFactory(@Nonnull final String version,
                                              @Nonnull final MutableServletContextHandler adminContext,
                                              @Nonnull final String logfileName){
        this.version = version;
        this.adminContext = adminContext;
        this.logfileName = logfileName;
    }

    @Override
    public Class<MicroserviceMetaData> getPayloadClass() {
        return MicroserviceMetaData.class;
    }

    @Override
    public ServiceInstance<MicroserviceMetaData> build(String serviceName,
                                                       CuratorAdvertiser<MicroserviceMetaData> advertiser) throws Exception {
        final int adminPort = getAdminport();
        MicroserviceMetaData metadata = new MicroserviceMetaData(
                advertiser.getInstanceId(),
                advertiser.getListenAddress(),
                advertiser.getListenPort(),
                version,
                adminPort,
                logfileName,
                adminPort,
                "/healthcheck",
                adminPort,
                "/metrics",
                adminPort,
                "/ping");
        return ServiceInstance.<MicroserviceMetaData>builder().name(serviceName)
                .address(advertiser.getListenAddress())
                .port(advertiser.getListenPort())
                .id(advertiser.getInstanceId().toString()).payload(metadata)
                .build();
    }

    private int getAdminport() {
        final ServerSocketChannel channel =
                (ServerSocketChannel) adminContext.getServer().getConnectors()[1].getTransport();
        final InetSocketAddress socket;

        try {
            socket = (InetSocketAddress) channel.getLocalAddress();
            return socket.getPort();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
