package com.aegal.frontend.dto;

import java.util.Date;

/**
 * DTO for a SubscribeManager
 * Created by vagrant on 10/20/14.
 */
public class SubscribeRequest {
    private final String servicename;
    private final String address;
    private final int port;
    private final String namespace;
    private final int timeToLive;
    private final long timeOfRequest;

    private boolean active;

    public SubscribeRequest(String servicename, String address, int port, String namespace, int timeToLive) {
        this.timeToLive = timeToLive;
        this.address = address;
        this.port = port;
        this.servicename = servicename;
        this.namespace = namespace;
        this.timeOfRequest = new Date().getTime();
    }

    public String getServicename() {
        return servicename;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getTimeToLive() {
        return timeToLive;
    }

    public long getTimeOfRequest() {
        return timeOfRequest;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isExpired() {
        return new Date().getTime() > timeOfRequest + (timeToLive * 1000);
    }

    public String getKey() {
        return String.format("%s/%s", namespace, servicename);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
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
}
