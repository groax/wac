package org.groax.firstApp.security;


import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;
import org.groax.firstApp.persistence.UserPostgresDaoImpl;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.security.Key;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Path("/auth")
public class AuthResource {
    final static public Key key = MacProvider.generateKey();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response authUser(@FormParam("username") String username, @FormParam("password") String password) {
        UserPostgresDaoImpl ud = new UserPostgresDaoImpl();

        System.out.println(123);

        String role = ud.findRoleForUser(username, password);

        if (role == null) {
            Map<String, String> message = new HashMap<String, String>();
            message.put("error", "No users found!");
            return Response.status(403).entity(message).build();
//            throw new IllegalArgumentException("No user found!");
        }

        String token = createToken(username, role);

        Map<String, String> message = new HashMap<String, String>();

        message.put("jwt", token);

        return Response.ok().entity(message).build();
    }

    private String createToken(String username, String role) throws JwtException {
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.HOUR, 2);


        return Jwts.builder()
                .setSubject(username)
                .setExpiration(expiration.getTime())
                .claim("role", role)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

}
