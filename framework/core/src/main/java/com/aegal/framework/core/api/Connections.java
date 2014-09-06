package com.aegal.framework.core.api;

import com.ge.snowizard.discovery.core.InstanceMetadata;
import feign.RequestLine;

import javax.ws.rs.Produces;
import java.util.List;

/**
 * User: A.Egal
 * Date: 9/1/14
 * Time: 9:30 PM
 */
public interface Connections {

    @RequestLine("GET /api/connections")
    @Produces("application/json")
    List<InstanceMetadata> getConnections();

}
