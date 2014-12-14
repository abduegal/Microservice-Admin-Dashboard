package com.aegal.framework.core.discovery;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by vagrant on 12/13/14.
 */
@Immutable
public class MicroserviceMetaData {

    private final UUID instanceId;
    private final String listenAddress;
    private final int listenPort;
    private final String version;
    private final int adminPort;
    private final String logfileName;
    private final List<MicroserviceMetaData> otherConnections = new ArrayList<>();

    private final int healthcheckport;
    private final String healthcheckaddress;

    private final int metricsport;
    private final String metricsaddress;

    private final int pingport;
    private final String pingaddress;

    @JsonCreator
    public MicroserviceMetaData(@JsonProperty("instanceId") @Nonnull final UUID instanceId,
                                @JsonProperty("listenAddress") @Nonnull final String listenAddress,
                                @JsonProperty("listenPort") @Nonnull final int listenPort,
                                @JsonProperty("version") @Nonnull final String version,
                                @JsonProperty("adminPort") @Nonnull final int adminPort,
                                @JsonProperty("logfileName") @Nonnull final String logfileName,
                                @JsonProperty("healthcheckport") @Nonnull final int healthcheckport,
                                @JsonProperty("healthcheckaddress") @Nonnull final String healthcheckaddress,
                                @JsonProperty("metricsport") @Nonnull final int metricsport,
                                @JsonProperty("metricsaddress") @Nonnull final String metricsaddress,
                                @JsonProperty("pingport") @Nonnull final int pingport,
                                @JsonProperty("pingaddress") @Nonnull final String pingaddress) {
        this.instanceId = instanceId;
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
        this.version = version;
        this.adminPort = adminPort;
        this.logfileName = logfileName;

        this.healthcheckport = healthcheckport;
        this.healthcheckaddress = healthcheckaddress;
        this.metricsport = metricsport;
        this.metricsaddress = metricsaddress;
        this.pingport = pingport;
        this.pingaddress = pingaddress;
    }

    public UUID getInstanceId() {
        return instanceId;
    }

    public String getListenAddress() {
        return listenAddress;
    }

    public int getListenPort() {
        return listenPort;
    }

    public String getVersion() {
        return version;
    }

    public int getAdminPort() {
        return adminPort;
    }

    public List<MicroserviceMetaData> getOtherConnections() {
        return otherConnections;
    }

    public String getLogfileName() {
        return logfileName;
    }

    public int getHealthcheckport() {
        return healthcheckport;
    }

    public String getHealthcheckaddress() {
        return healthcheckaddress;
    }

    public int getMetricsport() {
        return metricsport;
    }

    public String getMetricsaddress() {
        return metricsaddress;
    }

    public int getPingport() {
        return pingport;
    }

    public String getPingaddress() {
        return pingaddress;
    }
}
