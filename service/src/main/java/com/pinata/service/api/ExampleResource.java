package com.pinata.service.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("")
public class ExampleResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayhello() {
        return "'alo!";
    }
}