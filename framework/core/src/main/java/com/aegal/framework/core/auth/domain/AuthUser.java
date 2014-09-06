package com.aegal.framework.core.auth.domain;

import java.io.Serializable;
import java.util.List;

/**
 * An authenticatable user.
 * User: A.Egal
 * Date: 8/7/14
 * Time: 8:07 PM
 */
public class AuthUser implements Serializable {

    private String username;
    private String role;
    private List<String> privileges;
    private String application;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<String> privileges) {
        this.privileges = privileges;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public static AuthUser create(String username, String role, List<String> privileges, String application) {
        AuthUser authUser = new AuthUser();
        authUser.setUsername(username);
        authUser.setRole(role);
        authUser.setPrivileges(privileges);
        authUser.setApplication(application);
        return authUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AuthUser)) return false;

        AuthUser authUser = (AuthUser) o;

        if (username != null ? !username.equals(authUser.username) : authUser.username != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return username != null ? username.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AuthUser{" +
                "username='" + username + '\'' +
                '}';
    }
}
