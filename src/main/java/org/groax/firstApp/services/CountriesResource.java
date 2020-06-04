package org.groax.firstApp.services;

import org.groax.firstApp.domain.Country;
import org.groax.firstApp.domain.ServiceProvider;
import org.groax.firstApp.domain.WorldService;

import javax.annotation.security.RolesAllowed;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/countries")
public class CountriesResource {
    private JsonObjectBuilder createCountryObject(Country c) {
        JsonObjectBuilder job = Json.createObjectBuilder();
        job.add("code", c.getCode());
        job.add("name", c.getName());
        job.add("capital", c.getCapital());
        job.add("surface", c.getSurface());
        job.add("government", c.getGovernment());
        job.add("lat", c.getLatitude());
        job.add("iso3", c.getIso3());
        job.add("continent", c.getContinent());
        job.add("regio", c.getRegion());
        job.add("population", c.getPopulation());
        job.add("lng", c.getLongitude());
        job.add("lng", c.getLongitude());
        return job;
    }

    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Country> getCountries(@Context SecurityContext sc) {
        WorldService ws = ServiceProvider.getWorldService();
        return ws.getAllCountries();
    }

    @GET
    @Path("{code}")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Country getCountryByCode(@Context SecurityContext sc, @PathParam("code") String code) {
        WorldService ws = ServiceProvider.getWorldService();
        return ws.getCountryByCode(code);
    }

    @POST
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCountry(@Context SecurityContext sc,
                               @FormParam("code") String code,
                               @FormParam("name") String name,
                               @FormParam("capital") String capital,
                               @FormParam("region") String region,
                               @FormParam("surface") int surface,
                               @FormParam("population") int population) {

        WorldService ws = ServiceProvider.getWorldService();
        Map<String, String> message = new HashMap<String, String>();

        System.out.println(code + name + capital + region + surface + population);

        try {
            ws.createCountry(code, name, capital, region, surface, population);
        } catch(RuntimeException e) {
            message.put("error", e.getMessage());
            return Response.status(402).entity(message).build();
        }

        return Response.ok().build();
    }


    @PUT
    @Path("{code}")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateCountryByCode(@PathParam("code") String code,
                                        @FormParam("name") String name,
                                        @FormParam("capital") String capital,
                                        @FormParam("region") String region,
                                        @FormParam("surface") int surface,
                                        @FormParam("population") int population) {

        WorldService ws = ServiceProvider.getWorldService();
        Map<String, String> messages = new HashMap<String, String>();
        int c = ws.updateCountry(code, name, capital, region, surface, population);

        if(c < 1) {
            messages.put("error", "Row(s) niet geupdated.");
            return Response.status(409).entity(messages).build();
        }

        messages.put("amount_updated", "" + c + "");

        return Response.ok(messages).build();
    }

    @DELETE
    @Path("{code}")
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Country deleteCountryByCode(@Context SecurityContext sc, @PathParam("code") String code) {
        WorldService ws = ServiceProvider.getWorldService();
        ws.deleteCountry(code);
        return null;
    }

    @GET
    @Path("/largestsurfaces")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLargestSurfaces() {
        WorldService ws = ServiceProvider.getWorldService();
        List<Country> c = ws.get10LargestSurfaces();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for (Country co : c) {
            jab.add(this.createCountryObject(co));
        }

        return jab.build().toString();
    }

    @GET
    @Path("/largestpopulations")
    @Produces(MediaType.APPLICATION_JSON)
    public String getLargestPopulations() {
        WorldService ws = ServiceProvider.getWorldService();
        List<Country> c = ws.get10LargestPopulations();
        JsonArrayBuilder jab = Json.createArrayBuilder();

        for (Country co : c) {
            jab.add(this.createCountryObject(co));
        }

        return jab.build().toString();
    }
}
