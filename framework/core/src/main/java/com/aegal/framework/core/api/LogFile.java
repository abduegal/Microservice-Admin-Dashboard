package com.aegal.framework.core.api;

import feign.RequestLine;

import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * User: A.Egal
 * Date: 9/6/14
 * Time: 5:47 PM
 */
public interface LogFile {

    @RequestLine("GET /api/logfile")
    @Produces("text/plain")
    String getLogFile();
}
