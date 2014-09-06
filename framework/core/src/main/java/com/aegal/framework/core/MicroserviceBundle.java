package com.aegal.framework.core;

import com.aegal.framework.core.auth.provider.AuthProvider;
import com.aegal.framework.core.auth.provider.AuthProviderMock;
import com.aegal.framework.core.auth.provider.MSVariableProvider;
import com.aegal.framework.core.auth.provider.MvelExpressionEvaluationMock;
import com.aegal.framework.core.auth.provider.srv.ConditionalAuthProvider;
import com.aegal.framework.core.auth.provider.srv.MvelExpressionEvaluation;
import com.aegal.framework.core.database.DatabaseNotConfiguratedException;
import com.aegal.framework.core.resources.AdminPortResource;
import com.aegal.framework.core.resources.ConnectionsResource;
import com.aegal.framework.core.resources.LogFileResource;
import com.aegal.framework.core.util.JsonConverter;
import com.aegal.framework.core.zookeeper.MSDiscoveryBundle;
import com.fasterxml.jackson.core.type.TypeReference;
import com.ge.snowizard.discovery.DiscoveryBundle;
import com.ge.snowizard.discovery.DiscoveryFactory;
import com.ge.snowizard.discovery.client.DiscoveryClient;
import com.ge.snowizard.discovery.client.DiscoveryClientManager;
import com.ge.snowizard.discovery.core.CuratorFactory;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.ge.snowizard.discovery.core.JacksonInstanceSerializer;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.auth.oauth.OAuthProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

/**
 * Bundle that launches zookeeper and registeres this microservice.
 * User: A.Egal
 * Date: 8/7/14
 * Time: 8:30 PM
 */
public class MicroserviceBundle<T extends MicroserviceConfig> implements
        ConfiguredBundle<T> {

    protected DiscoveryBundle discoveryBundleApplication;

    protected DiscoveryClient clientApplication;
    private BundleConfiguration databaseBootstrap;

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        discoveryBundleApplication = new MSDiscoveryBundle<MicroserviceConfig>() {
            public DiscoveryFactory getDiscoveryFactory(MicroserviceConfig configuration) {
                return configuration.getDiscoveryFactory();
            }
        };

        bootstrap.addBundle(discoveryBundleApplication);

        databaseBootstrap = new BundleConfiguration() {
            @Override
            public void build(MicroserviceConfig config) {
                if (config.getDatabaseConfig() != null) {
                    config.getDatabaseConfig().bootstrap(bootstrap);
                }
                JsonConverter.setObjectMapper(bootstrap.getObjectMapper());
            }
        };
    }

    @Override
    public void run(MicroserviceConfig configuration, Environment environment) throws Exception {

        databaseBootstrap.build(configuration);

        if (configuration.isAllowOrigin()) {
            enableAllowOrigin(environment);
        }

        connectDatabase(configuration);

        if (configuration.getAuthConfig() != null) {
            enableAuthentication(configuration, environment);
        }

        // do not register to zookeeper on test mode.
        if (!configuration.isTest() && configuration.getDiscoveryFactory().getServiceName() != null) {
            registerZookeeper(configuration, environment);
        }

        environment.jersey().register(new AdminPortResource(environment.getAdminContext()));
        environment.jersey().register(new ConnectionsResource());

        registerLogFileResource(configuration, environment);

        startZookeeperConnection(configuration, environment);
    }

    protected void connectDatabase(MicroserviceConfig configuration) throws DatabaseNotConfiguratedException {
        if (configuration.getDatabaseConfig() != null) {
            configuration.getDatabaseConfig().connect();
        }
    }

    protected void registerZookeeper(MicroserviceConfig configuration, Environment environment) throws Exception {
        clientApplication = discoveryBundleApplication.newDiscoveryClient(configuration.getDiscoveryFactory().getServiceName());
        environment.lifecycle().manage(new DiscoveryClientManager(clientApplication));
    }

    protected void enableAllowOrigin(Environment environment){
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        FilterRegistration.Dynamic filterAdmin = environment.admin().addFilter("CORS", CrossOriginFilter.class);
        allowOrigin(filter);
        allowOrigin(filterAdmin);
    }

    private void allowOrigin(FilterRegistration.Dynamic filter) {
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*"); // allowed origins comma separated
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS,HEAD");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");
    }

    protected void enableAuthentication(MicroserviceConfig configuration, Environment environment){

        if(!configuration.isTest()){
            environment.jersey().register(new ConditionalAuthProvider<>(
                    new OAuthProvider<>(new AuthProvider(configuration.getAuthConfig()), "secritDWP@$$"),
                    new MvelExpressionEvaluation<>(new MSVariableProvider())));

        }else{
            //for tests.
            environment.jersey().register(new ConditionalAuthProvider<>(
                    new OAuthProvider<>(new AuthProviderMock(configuration.getAuthConfig()),
                            "secret"),
                    new MvelExpressionEvaluationMock<>(new MSVariableProvider())));
        }
    }

    protected void startZookeeperConnection(MicroserviceConfig configuration, Environment environment) throws Exception{
        DiscoveryFactory discoveryFactory = discoveryBundleApplication.getDiscoveryFactory(configuration);
        final JacksonInstanceSerializer<InstanceMetadata> serializer = new JacksonInstanceSerializer<InstanceMetadata>(
                environment.getObjectMapper(), new TypeReference<ServiceInstance<InstanceMetadata>>() {
        });

        ServiceDiscovery<InstanceMetadata> discovery = ServiceDiscoveryBuilder.builder(InstanceMetadata.class)
                .basePath(
                        discoveryFactory.getBasePath())
                .client( new CuratorFactory(environment).build(discoveryFactory))
                .serializer(serializer).build();

        ServiceLocator.createInstance(discovery, environment.getObjectMapper());
        environment.healthChecks().unregister("curator");
    }

    private void registerLogFileResource(MicroserviceConfig configuration, Environment environment) {
        if (!configuration.isTest()) {
            environment.jersey().register(new LogFileResource(configuration));
        }
    }
}
