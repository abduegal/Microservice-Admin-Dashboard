package com.aegal.frontend.dto;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by vagrant on 11/12/14.
 */
public class MetricsDTO {

    /**
     * The metrics response in json format.
     */
    public JsonNode node;
    /**
     * true if metrics call was successfull.
     */
    public boolean didRun = true;

    public static MetricsDTO from(JsonNode node) {
        MetricsDTO metricsDTO = new MetricsDTO();
        metricsDTO.node = node;
        return metricsDTO;
    }

}
