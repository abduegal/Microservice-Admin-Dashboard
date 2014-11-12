package com.aegal.frontend.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by vagrant on 11/12/14.
 */
public class HealthCheckDTO {

    /**
     * The healtcheck response in json format.
     */
    public JsonNode healthCheckResponse;
    /**
     * true if healthceck call was successfull.
     */
    public boolean didRun = true;

}
