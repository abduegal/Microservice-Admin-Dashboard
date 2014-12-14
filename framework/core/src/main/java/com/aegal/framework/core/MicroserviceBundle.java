package com.aegal.framework.core;

import com.aegal.framework.core.discovery.MicroserviceDiscoveryBundle;
import com.aegal.framework.core.resources.ConnectionsResource;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
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
public class MicroserviceBundle<T extends MicroserviceConfig> implements ConfiguredBundle<T> {

    protected MicroserviceDiscoveryBundle discoveryBundle = new MicroserviceDiscoveryBundle();

    @Override
    public void initialize(final Bootstrap<?> bootstrap) {
        bootstrap.addBundle(discoveryBundle);
    }

    @Override
    public void run(MicroserviceConfig configuration, Environment environment) throws Exception {

        ServiceLocator.createInstance(discoveryBundle);

        if (configuration.isAllowOrigin()) {
            enableAllowOrigin(environment);
        }

        environment.jersey().register(new ConnectionsResource());
        FeignBuilder.setObjectMapper(environment.getObjectMapper());

    }

    protected void enableAllowOrigin(Environment environment){
        FilterRegistration.Dynamic filter = environment.servlets().addFilter("CORS", CrossOriginFilter.class);
        FilterRegistration.Dynamic filterAdmin = environment.admin().addFilter("CORS", CrossOriginFilter.class);
        allowOrigin(filter);
        allowOrigin(filterAdmin);
    }

    private static void allowOrigin(FilterRegistration.Dynamic filter) {
        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter("allowedOrigins", "*"); // allowed origins comma separated
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowedMethods", "GET,PUT,POST,DELETE,OPTIONS,HEAD");
        filter.setInitParameter("preflightMaxAge", "5184000"); // 2 months
        filter.setInitParameter("allowCredentials", "true");
    }

}
