package com.pinata.service.api.v1;

import java.util.Date;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.objectmodel.Event;
import com.pinata.service.objectmodel.User;
import com.pinata.service.objectmodel.UserSession;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.CreateEventRequest;
import com.pinata.shared.EventResponse;

/**
 * Events resource used for the creation of events. v1.
 * @author Elliot Essman
 */
@Path("v1/events")
public class Events1Resource {

    /** Injected information about the URI of the current request. */
    @Context
    UriInfo uriInfo;

    /**
     * Post Request. Creates a new event in the database and returns a
     * CreateEventResponse, or an ErrorApiResponse in JSON.
     * @param jsonBody The POST json body.
     * @return The input values in a CreateUserResponse with SUCCESS,
     * or an ErrorApiResponse upon an error.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response post(@HeaderParam(UserSession.HEADER) String sessionHeader, String jsonBody) throws ApiException {

        CreateEventRequest request = new CreateEventRequest();
        request.deserializeFrom(jsonBody);

        SQLConnection sql = SQLConnection.connectDefault();

        Event event = null;
        try {
            UserSession session = UserSession.resume(sql, sessionHeader);

            event = Event.create(sql,
                               request.name,
                               request.location,
                               request.date,
                               request.byob, 
                               session.getUser());
        } finally {
            sql.close();
        }

        URI newEventUri = uriInfo.getRequestUriBuilder()
            .path(event.getName()).build();

        EventResponse eventResponse = event.toResponse(ApiStatus.CREATED);

        return Response.created(newEventUri)
            .entity(eventResponse.serialize()).build();
    }

    /**
     * GET request. Pulls down event's details.
     * @param eventID ID of the event to pull down.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{eventID}")
    public Response get(@PathParam("eventID") int eventID)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        Event event = null;
        try {
            event = Event.lookup(sql, eventID);
        } finally {
            sql.close();
        }

        EventResponse eventResponse = event.toResponse(ApiStatus.OK);
        return Response.ok(eventResponse.serialize()).build();
    }

    /**
     * DELETE request. Deletes the specified event and returns it's details.
     * @param eventID ID of the event to delete
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{eventID}")
    public Response delete(@PathParam("eventID") int eventID)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        Event event = null;
        try {
            event = Event.lookup(sql, eventID);
            event.delete(sql, eventID);
        } finally {
            sql.close();
        }

        EventResponse eventResponse = event.toResponse(ApiStatus.DELETED);
        return Response.ok(eventResponse.serialize()).build();
    }
}
