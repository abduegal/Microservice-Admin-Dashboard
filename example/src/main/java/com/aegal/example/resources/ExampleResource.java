package com.aegal.example.resources;

import com.aegal.example.ExampleService;
import com.aegal.framework.core.ServiceLocator;
import com.aegal.framework.core.auth.domain.AuthUser;
import com.fasterxml.jackson.databind.JsonNode;
import com.yammer.tenacity.core.TenacityCommand;
import feign.RequestLine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static com.aegal.example.ExampleService.DependencyKeys;

/**
 * User: A.Egal
 * Date: 9/6/14
 * Time: 10:27 PM
 */
@Path("/")
public class ExampleResource {

    interface TokenAuth{
        @RequestLine("GET /api/test/token")
        @Produces("application/json")
        AuthUser run();
    }

    @GET
    public String get() {
        return new TenacityCommand<String>(DependencyKeys.Action) {
            @Override
            protected String run() throws Exception {
                ServiceLocator.getInstance().build("Login_service", TokenAuth.class).run();
                return "example";
            }
        }.execute();
    }
}
