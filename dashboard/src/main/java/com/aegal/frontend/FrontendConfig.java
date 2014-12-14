package com.aegal.frontend;

import com.aegal.framework.core.MicroserviceConfig;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * User: A.Egal
 * Date: 8/9/14
 * Time: 8:05 PM
 */
public class FrontendConfig extends MicroserviceConfig {

    @JsonProperty("namespaces")
    private List<String> namespaces;

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

}
