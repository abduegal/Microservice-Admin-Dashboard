package com.aegal.framework.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import feign.RequestLine;
import feign.Response;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Map;

/**
 * User: A.Egal
 * Date: 9/1/14
 * Time: 9:47 PM
 */
public interface AdminMetrics {

    @RequestLine("GET /healthcheck")
    @Produces("application/json")
    JsonNode healthcheck();

    @RequestLine("GET /metrics")
    @Produces("application/json")
    JsonNode metrics();

    @RequestLine("GET /ping")
    String ping();

}
