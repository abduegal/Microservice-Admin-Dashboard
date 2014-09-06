package com.aegal.frontend;

import com.aegal.framework.core.MicroserviceConfig;
import com.aegal.framework.core.database.DbConfig;
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


    @Override
    public DbConfig getDatabaseConfig() {
        return null;
    }

    public List<String> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

}
