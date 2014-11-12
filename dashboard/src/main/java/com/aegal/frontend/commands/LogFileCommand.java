package com.aegal.frontend.commands;

import com.aegal.framework.core.api.LogFile;
import com.aegal.frontend.DependencyKeys;
import com.aegal.frontend.srv.NamespacesManager;
import com.ge.snowizard.discovery.core.InstanceMetadata;
import com.yammer.tenacity.core.TenacityCommand;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Reads the logfile for the selected service and converts the response.
 * Created by vagrant on 11/12/14.
 */
public class LogFileCommand extends TenacityCommand<Response> {

    private final String ns;
    private final Integer lines;
    private final InstanceMetadata serviceInstance;
    private final NamespacesManager namespacesManager;

    public LogFileCommand(final String ns,
                          final Integer lines,
                          final InstanceMetadata serviceInstance,
                          final NamespacesManager namespacesManager) {

        super(DependencyKeys.DASHBOARD_READ_LOGFILE);
        this.ns = ns;
        this.lines = lines;
        this.serviceInstance = serviceInstance;
        this.namespacesManager = namespacesManager;
    }

    @Override
    protected Response run() throws Exception {
        String logFile = namespacesManager.getServiceLocator(ns).buildNoSerialize(serviceInstance, LogFile.class).getLogFile();
        return readLogFiles(logFile);
    }

    @Override
    protected Response getFallback() {
        return Response.ok("Unable to read the logs").build();
    }

    /**
     * Tries to read the logfile.
     */
    private Response readLogFiles(final String logfile) {
        StreamingOutput stream = new StreamingOutput() {
            @Override
            public void write(OutputStream outputStream) throws IOException, WebApplicationException {

                String[] buffer = new String[lines];

                BufferedReader br = null;
                try {
                    br = new BufferedReader(new FileReader(logfile));
                    char suffix = '\n';

                    int bufferStartIndex = 0;
                    for (String line; (line = br.readLine()) != null; ) {
                        buffer[bufferStartIndex++ % buffer.length] = line + suffix;
                    }

                    for (String s : buffer) {
                        outputStream.write(s.getBytes());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (br != null) br.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        };

        return javax.ws.rs.core.Response.ok(stream).build();
    }
}
