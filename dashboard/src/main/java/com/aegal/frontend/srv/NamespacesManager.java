package com.aegal.frontend.srv;

import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.discovery.MicroserviceDiscoveryBundle;
import com.aegal.framework.core.discovery.MicroserviceMetaData;
import com.aegal.frontend.FrontendConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import io.dropwizard.discovery.core.JacksonInstanceSerializer;
import io.dropwizard.discovery.health.CuratorHealthCheck;
import io.dropwizard.discovery.manage.CuratorManager;
import io.dropwizard.setup.Environment;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages the different namespaces in the config.yml.
 * User: A.Egal
 * Date: 8/15/14
 * Time: 8:51 PM
 */
public class NamespacesManager {

    private FrontendConfig config;
    private Map<String, ServiceLocator> namespaceWithServiceDiscovery = new HashMap<>();

    public NamespacesManager(FrontendConfig config, Environment environment) {
        this.config = config;
        build(environment);
    }

    private void build(Environment environment) {
        final JacksonInstanceSerializer<MicroserviceMetaData> serializer = new JacksonInstanceSerializer<>(
                environment.getObjectMapper(), new TypeReference<ServiceInstance<MicroserviceMetaData>>() {
        });

        for (String namespace : config.getNamespaces()) {
            final CuratorFramework framework = CuratorFrameworkFactory
                    .builder()
                    .connectionTimeoutMs(
                            (int) config.getDiscoveryFactory().getConnectionTimeout().toMilliseconds())
                    .sessionTimeoutMs(
                            (int) config.getDiscoveryFactory().getSessionTimeout().toMilliseconds())
                    .retryPolicy(config.getDiscoveryFactory().getRetryPolicy())
                    .compressionProvider(config.getDiscoveryFactory().getCompressionProvider())
                    .connectString(config.getDiscoveryFactory().getQuorumSpec())
                    .canBeReadOnly(config.getDiscoveryFactory().isReadOnly())
                    .namespace(namespace).build();

            environment.lifecycle().manage(new CuratorManager(framework));
            environment.healthChecks().register("curator", new CuratorHealthCheck(framework));

            ServiceDiscovery<MicroserviceMetaData> discovery = ServiceDiscoveryBuilder.builder(MicroserviceMetaData.class)
                    .basePath("/" + this.config.getDiscoveryFactory().getBasePath())
                    .client(framework)
                    .serializer(serializer).build();

            namespaceWithServiceDiscovery.put(namespace, ServiceLocator.getInstance(discovery));
        }
    }

    public ServiceLocator getServiceLocator(String namespace) {
        return namespaceWithServiceDiscovery.get(namespace);
    }

}
