package com.aegal.framework.core.discovery;

import com.aegal.framework.core.MicroserviceConfig;
import io.dropwizard.Configuration;
import io.dropwizard.discovery.AbstractDiscoveryBundle;
import io.dropwizard.discovery.DiscoveryFactory;
import io.dropwizard.discovery.client.DiscoveryClient;
import io.dropwizard.discovery.client.DiscoveryClientManager;
import io.dropwizard.discovery.core.CuratorAdvertiser;
import io.dropwizard.discovery.core.ServiceInstanceFactory;
import io.dropwizard.jetty.MutableServletContextHandler;
import io.dropwizard.logging.AppenderFactory;
import io.dropwizard.logging.FileAppenderFactory;
import io.dropwizard.setup.Environment;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by vagrant on 12/13/14.
 */
public class MicroserviceDiscoveryBundle<T extends MicroserviceConfig> extends
        AbstractDiscoveryBundle<MicroserviceConfig, MicroserviceMetaData> {

    private String version;
    private String logFileName;
    private Environment environment;
    private ServiceDiscovery<MicroserviceMetaData> discovery;

    private Map<String, DiscoveryClient<MicroserviceMetaData>> cache = new HashMap<>();

    @Override
    public Class<MicroserviceMetaData> getPayloadClass() {
        return MicroserviceMetaData.class;
    }

    @Override
    public DiscoveryFactory getDiscoveryFactory(MicroserviceConfig microserviceConfig) {
        return microserviceConfig.getDiscoveryFactory();
    }

    @Override
    public void run(@Nonnull MicroserviceConfig configuration, @Nonnull Environment environment) throws Exception {
        this.version = configuration.getVersion();
        this.environment = environment;
        this.logFileName = getLogFileName(configuration);
        super.run(configuration, environment);
    }

    @Override
    public CuratorAdvertiser<MicroserviceMetaData> getCuratorAdvertiser(DiscoveryFactory discoveryFactory,
                                                                        ServiceDiscovery<MicroserviceMetaData> discovery) {
        this.discovery = discovery;
        ServiceInstanceFactory<MicroserviceMetaData> serviceInstanceFactory =
                new MicroserviceServiceInstanceFactory(version, environment.getAdminContext(), logFileName);
        return new CuratorAdvertiser<MicroserviceMetaData>(discoveryFactory, discovery, serviceInstanceFactory);
    }

    public DiscoveryClient<MicroserviceMetaData> getDiscoveryClient(String servicename) {
        if (cache.containsKey(servicename)) {
            return cache.get(servicename);
        }

        DiscoveryClient<MicroserviceMetaData> discoveryClient =
                this.newDiscoveryClient(servicename, new RoundRobinStrategy<MicroserviceMetaData>());
        cache.put(servicename, discoveryClient);
        environment.lifecycle().manage(new DiscoveryClientManager<>(discoveryClient));

        return discoveryClient;
    }

    public ServiceDiscovery<MicroserviceMetaData> getServiceDiscovery() {
        return discovery;
    }

    private String getLogFileName(Configuration configuration) {
        for (AppenderFactory appenderFactory : configuration.getLoggingFactory().getAppenders()) {
            if (appenderFactory instanceof FileAppenderFactory) {
                FileAppenderFactory fileAppenderFactory = (FileAppenderFactory) appenderFactory;
                return fileAppenderFactory.getCurrentLogFilename();
            }
        }
        return "";
    }

    public static MicroserviceDiscoveryBundle build(ServiceDiscovery<MicroserviceMetaData> serviceDiscovery) {
        MicroserviceDiscoveryBundle<MicroserviceConfig> bundle = new MicroserviceDiscoveryBundle<>();
        bundle.discovery = serviceDiscovery;
        return bundle;
    }
}
