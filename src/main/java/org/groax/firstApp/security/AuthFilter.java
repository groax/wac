package org.groax.firstApp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        boolean isSecure = containerRequestContext.getSecurityContext().isSecure();

        String authHeader = containerRequestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            MySecurityContext msc = new MySecurityContext("Unknown", "guest", isSecure);
            containerRequestContext.setSecurityContext(msc);
            return;
        }

        String token = authHeader.substring("Bearer".length()).trim();

        try {

            JwtParser parser = Jwts.parser().setSigningKey(AuthResource.key);
            Claims claim = parser.parseClaimsJws(token).getBody();

            String user = claim.getSubject();
            String role = claim.get("role").toString();

            MySecurityContext msc = new MySecurityContext(user, role, isSecure);
            containerRequestContext.setSecurityContext(msc);
        } catch(JwtException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
            System.out.println("Invalid jwt token.");
        }
    }
}
