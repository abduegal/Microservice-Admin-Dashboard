package com.aegal.framework.core.api;

import feign.RequestLine;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * User: A.Egal
 * Date: 9/1/14
 * Time: 9:29 PM
 */
public interface AdminPort {

    @RequestLine("GET /api/adminport")
    @Produces("application/json")
    Integer getAdminPort();

}
