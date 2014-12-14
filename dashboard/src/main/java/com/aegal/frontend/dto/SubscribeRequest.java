package com.aegal.frontend.dto;

import java.util.Date;

/**
 * DTO for a SubscribeManager
 * Created by vagrant on 10/20/14.
 */
public class SubscribeRequest {
    public String servicename;
    public String address;
    public int port;
    public String namespace;
    public int timeToLive;
    public long timeOfRequest = new Date().getTime();

    public String version;
    public String logFileLocation;
    private Integer adminPort;

    public boolean active;

    private Integer healthcheckport;
    private String healthcheckaddress;

    private Integer metricsport;
    private String metricsaddress;

    private Integer pingport;
    private String pingaddress;

    public boolean isExpired() {
        return new Date().getTime() > timeOfRequest + (timeToLive * 1000);
    }

    public String getKey() {
        return String.format("%s/%s", namespace, servicename);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SubscribeRequest that = (SubscribeRequest) o;

        if (port != that.port) return false;
        if (address != null ? !address.equals(that.address) : that.address != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (servicename != null ? !servicename.equals(that.servicename) : that.servicename != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = servicename != null ? servicename.hashCode() : 0;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        result = 31 * result + port;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    public void setAdminPort(Integer adminPort) {
        this.adminPort = adminPort;
    }

    public Integer getAdminPort() {
        return adminPort != null ? adminPort : port;
    }

    public Integer getHealthcheckport() {
        return healthcheckport != null ? healthcheckport : getAdminPort();
    }

    public void setHealthcheckport(Integer healthcheckport) {
        this.healthcheckport = healthcheckport;
    }

    public String getHealthcheckaddress() {
        return healthcheckaddress != null ? healthcheckaddress: "/";
    }

    public void setHealthcheckaddress(String healthcheckaddress) {
        this.healthcheckaddress = healthcheckaddress;
    }

    public Integer getMetricsport() {
        return metricsport != null ? metricsport : getAdminPort();
    }

    public void setMetricsport(Integer metricsport) {
        this.metricsport = metricsport;
    }

    public String getMetricsaddress() {
        return metricsaddress != null ? metricsaddress: "/";
    }

    public void setMetricsaddress(String metricsaddress) {
        this.metricsaddress = metricsaddress;
    }

    public Integer getPingport() {
        return pingport != null ? pingport : getAdminPort();
    }

    public void setPingport(Integer pingport) {
        this.pingport = pingport;
    }

    public String getPingaddress() {
        return pingaddress != null ? pingaddress: "/";
    }

    public void setPingaddress(String pingaddress) {
        this.pingaddress = pingaddress;
    }
}
