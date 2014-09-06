package com.aegal.framework.core.api;

import com.aegal.framework.core.auth.domain.AuthUser;
import feign.RequestLine;

import javax.inject.Named;
import javax.ws.rs.Produces;

/**
 * User: A.Egal
 * Date: 9/1/14
 * Time: 9:50 PM
 */
public interface Authentication {

    @RequestLine("GET {path}/{token}")
    @Produces("application/json")
    AuthUser authenticate(@Named("path") String path, @Named("token") String token);
}
