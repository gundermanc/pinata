package com.pinata.service.api.v1;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.WebApplicationException;

import java.sql.SQLException;
import com.pinata.service.datatier.SQLConnection;
import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

/**
 * Version1 Resource.
 * @author Christian Gunderman
 */
@Path("v1")
public class Version1Resource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get() throws ApiException {
        return Response.ok("API v1").build();
    }
}
