package com.aegal.framework.core.api;

import feign.RequestLine;

import javax.ws.rs.Produces;

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
