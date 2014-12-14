package com.aegal.framework.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import feign.RequestLine;

import javax.ws.rs.Produces;

/**
 * User: A.Egal
 * Date: 9/1/14
 * Time: 9:47 PM
 */
public interface AdminMetrics {

    @RequestLine("GET")
    @Produces("application/json")
    JsonNode healthcheck();

    @RequestLine("GET")
    @Produces("application/json")
    JsonNode metrics();

    @RequestLine("GET")
    String ping();

}
