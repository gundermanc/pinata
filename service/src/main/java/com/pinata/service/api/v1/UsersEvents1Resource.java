package com.pinata.service.api.v1;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;
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
import com.pinata.shared.EventsResponse;

/**
 * Events resource used for managing events v1.
 * @author Elliot Essman
 */
@Path("v1/users/{username}/events")
public class UsersEvents1Resource {

    /** Injected information about the URI of the current request. */
    @Context
    UriInfo uriInfo;

    /**
     * GET request. Gets the events this user is hosting.
     * User should be logged in so no info is required.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam(UserSession.HEADER) String sessionHeader)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        List<Event> events = null;
        try {
            UserSession session = UserSession.resume(sql, sessionHeader);
            events = Event.myEvents(sql, session.getUser());
        } finally {
            sql.close();
        }
        
        List<EventResponse> eventList = new LinkedList<EventResponse>();
        for(Event e : events){
            eventList.add(e.toResponse(ApiStatus.OK));
        }
        EventsResponse eventsResponse = new EventsResponse(ApiStatus.OK, eventList);
        return Response.ok(eventsResponse.serialize()).build();
    }
}
