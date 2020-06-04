package org.groax.firstApp.security;

import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

public class MySecurityContext implements SecurityContext {
    private final String username;
    private final String role;
    private final boolean isSecure;


    public MySecurityContext(String username, String role, boolean isSecure) {
        this.username = username;
        this.role = role;
        this.isSecure = isSecure;
    }

    @Override
    public Principal getUserPrincipal() {
        return new Principal() {
            public String getName() {
                return username;
            }
        };
    }

    @Override
    public boolean isUserInRole(String s) {
        return s.equals(this.role);
    }

    @Override
    public boolean isSecure() {
        return isSecure;
    }

    @Override
    public String getAuthenticationScheme() {
        return "Bearer";
    }
}
