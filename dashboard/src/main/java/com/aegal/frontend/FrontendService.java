package com.aegal.frontend;

import com.aegal.framework.core.MicroserviceBundle;
import com.aegal.frontend.resources.DeployResource;
import com.aegal.frontend.resources.OverviewResource;
import com.aegal.frontend.resources.SubscribeResource;
import com.aegal.frontend.resources.SystemResource;
import com.aegal.frontend.srv.NamespacesManager;
import com.aegal.frontend.srv.SubscribeManager;
import com.yammer.tenacity.core.bundle.TenacityBundleBuilder;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * Frontends services that runs the angular application.
 * User: A.Egal
 * Date: 8/9/14
 * Time: 7:48 PM
 */
public class FrontendService extends Application<FrontendConfig> {

    private MicroserviceBundle<? super FrontendConfig> microserviceBundle;

    public static void main(String[] args) throws Exception{
        new FrontendService().run(args);
    }

    @Override
    public void initialize(Bootstrap<FrontendConfig> bootstrap) {
        bootstrap.addBundle(new AssetsBundle("/static/", "/", "index.html"));
        microserviceBundle = new MicroserviceBundle<>();
        bootstrap.addBundle(microserviceBundle);
        bootstrap.addBundle(TenacityBundleBuilder.newBuilder()
                .propertyKeyFactory(DependencyKeys.getTenacityPropertyKeyFactory())
                .propertyKeys(DependencyKeys.values())
                .build());
    }

    @Override
    public void run(FrontendConfig configuration, Environment environment) throws Exception {
        environment.jersey().setUrlPattern("/api/*");

        NamespacesManager namespacesManager = new NamespacesManager(configuration, environment);
        environment.jersey().register(new OverviewResource(namespacesManager));
        environment.jersey().register(new DeployResource(configuration));
        environment.jersey().register(new SystemResource());

        SubscribeManager subscribeManager = new SubscribeManager(namespacesManager);
        environment.jersey().register(new SubscribeResource(subscribeManager));

        new Thread(subscribeManager).start();
    }
}
