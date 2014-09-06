package com.aegal.framework.core.resources;

import io.dropwizard.Configuration;
import io.dropwizard.logging.AppenderFactory;
import io.dropwizard.logging.FileAppenderFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.*;

/**
 * returns the logfile name.
 * User: A.Egal
 * Date: 9/6/14
 * Time: 5:35 PM
 */
@Path("/api/logfile")
public class LogFileResource {

    private String currentLogFilename = "";

    public LogFileResource(Configuration configuration) {
        for (AppenderFactory appenderFactory : configuration.getLoggingFactory().getAppenders()) {
            if (appenderFactory instanceof FileAppenderFactory) {
                FileAppenderFactory fileAppenderFactory = (FileAppenderFactory) appenderFactory;
                currentLogFilename = fileAppenderFactory.getCurrentLogFilename();
            }
        }
    }

    @GET
    @Produces("text/plain")
    public String get() throws Exception {
        return currentLogFilename;
    }



}
