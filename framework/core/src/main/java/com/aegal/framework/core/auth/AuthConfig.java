package com.aegal.framework.core.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 7:51 PM
 */
public class AuthConfig {

    @JsonProperty("servicename")
    private String servicename;

    @JsonProperty("path")
    private String path;

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
