package com.aegal.frontend.srv;

import com.aegal.frontend.DependencyKeys;
import com.yammer.tenacity.core.TenacityCommand;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Reads the logfile and converts it into a Stream output.
 * Created by vagrant on 10/20/14.
 */
public class ReadLogFileCommand extends TenacityCommand<Response> {

    private final String logfile;
    private final int lines;

    public ReadLogFileCommand(String logfile, int lines) {
        super(DependencyKeys.DASHBOARD_READ_LOGFILE);
        this.logfile = logfile;
        this.lines = lines;
    }

    @Override
    protected Response run() throws Exception {
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
