package com.aegal.example;

import com.aegal.example.resources.ExampleResource;
import com.aegal.framework.core.MicroserviceBundle;
import com.aegal.framework.core.tenacity.InitializeTenacity;
import com.yammer.tenacity.core.bundle.TenacityBundleBuilder;
import com.yammer.tenacity.core.properties.TenacityPropertyKey;
import com.yammer.tenacity.core.properties.TenacityPropertyKeyFactory;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

/**
 * User: A.Egal
 * Date: 9/6/14
 * Time: 10:25 PM
 */
public class ExampleService extends Application<ExampleConfiguration> {

    public static void main(String[] args) throws Exception {
        new ExampleService().run(args);
    }

    public enum DependencyKeys implements TenacityPropertyKey {
        Action;

        public static TenacityPropertyKeyFactory getTenacityPropertyKeyFactory() {
            return new TenacityPropertyKeyFactory(){
                @Override
                public TenacityPropertyKey from(String value) {
                    return DependencyKeys.valueOf(value.toUpperCase());
                }
            };
        }
    }

    @Override
    public void initialize(Bootstrap<ExampleConfiguration> bootstrap) {
        bootstrap.addBundle(new MicroserviceBundle<>());
        bootstrap.addBundle(TenacityBundleBuilder.newBuilder()
                .propertyKeyFactory(DependencyKeys.getTenacityPropertyKeyFactory())
                .propertyKeys(DependencyKeys.values())
                .build());
    }

    @Override
    public void run(ExampleConfiguration exampleConfiguration, Environment environment) throws Exception {
        environment.jersey().register(ExampleResource.class);

        InitializeTenacity.initialize(DependencyKeys.values());
    }
}
