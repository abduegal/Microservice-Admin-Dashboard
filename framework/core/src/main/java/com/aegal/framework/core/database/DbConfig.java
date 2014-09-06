package com.aegal.framework.core.database;

import io.dropwizard.setup.Bootstrap;

/**
 * User: A.Egal
 * Date: 8/8/14
 * Time: 4:50 PM
 */
public interface DbConfig {

    public void bootstrap(Bootstrap bootstrap);

    public void connect() throws DatabaseNotConfiguratedException;

}
