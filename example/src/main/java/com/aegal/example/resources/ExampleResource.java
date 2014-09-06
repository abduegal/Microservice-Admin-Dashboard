package com.aegal.example.resources;

import com.yammer.tenacity.core.TenacityCommand;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import static com.aegal.example.ExampleService.DependencyKeys;

/**
 * User: A.Egal
 * Date: 9/6/14
 * Time: 10:27 PM
 */
@Path("/")
public class ExampleResource {

    @GET
    public String get() {
        return new TenacityCommand<String>(DependencyKeys.Action) {
            @Override
            protected String run() throws Exception {
                return "example";
            }
        }.execute();
    }
}
