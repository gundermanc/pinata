package com.pinata.service.api.v1;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.objectmodel.UserSession;
import com.pinata.service.objectmodel.OMUtil;

import com.pinata.shared.*;

/**
 * Api Resource for creating a new session, a.k.a. logging in and out.
 * @author Christian Gunderman
 */
@Path("v1/users/{username}/sessions")
public class UsersSessions1Resource {

    /** Injected information about the URI of the current request. */
    @Context
    UriInfo uriInfo;

    /**
     * Creates a new session for the given user with the given password.
     * @throws ApiException If database error occurs, password is incorrect,
     * or some other known error occurs.
     * @param username The username of the person logging in. This param
     * is part of the URL but is ignored. Actual username comes in via Json.
     * @param jsonBody The Json request.
     * @return 201 CREATED upon completion.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSession(@PathParam("username")String username,
                                  String jsonBody) throws ApiException {
        CreateUserSessionRequest request = new CreateUserSessionRequest();
        request.deserializeFrom(jsonBody);

        SQLConnection sql = SQLConnection.connectDefault();

        UserSession session = null;
        try {
            session = UserSession.start(sql,
                                        request.user,
                                        request.pass);
        } finally {
            sql.close();
        }

        URI newUri = uriInfo.getRequestUriBuilder()
            .path(session.getSessionId().toString()).build();

        UserSessionResponse response
            = session.toResponse(ApiStatus.CREATED);

        return Response.created(newUri)
            .entity(response.serialize()).build();
    }

    /**
     * Gets a session's info. This end point requires authentication
     * and can only be accessed by ROLE_ADMIN users and the owner
     * of the session being browsed.
     * @param sessionHeader The authentication header. Feed into
     * UserSession.resume().
     * @param username The username from the URL.
     * @param sessionId The unique session identifier.
     * @return 200 OK.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{sessionId}")
    public Response getSession(@HeaderParam(UserSession.HEADER)String sessionHeader,
                               @PathParam("username")String username,
                               @PathParam("sessionId")String sessionId)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        UserSession session = null;
        UserSession desired = null;
        try {
            session = UserSession.resume(sql, sessionHeader);
            desired = UserSession.resume(sql, username, sessionId);

            OMUtil.adminOrOwnerCheck(session.getUser(), desired.getUser());

        } finally {
            sql.close();
        }

        UserSessionResponse response = desired.toResponse(ApiStatus.OK);
        return Response.ok(response.serialize()).build();
    }

    /**
     * Deletes a single session with the given Id. Requires authentication
     * and can only be accessed by ROLE_ADMIN users and the owner of the
     * session being deleted.
     * @param sessionHeader The session authentication header.
     * @param username The username of the session owner.
     * @param sessionId The id of the session to be deleted.
     * @return 200 OK.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{sessionId}")
    public Response deleteSession(@HeaderParam(UserSession.HEADER)String sessionHeader,
                                  @PathParam("username")String username,
                                  @PathParam("sessionId") String sessionId)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        UserSession session = null;
        UserSession foresaken = null;
        try {
            session = UserSession.resume(sql, sessionHeader);
            foresaken = UserSession.resume(sql, username, sessionId);

            OMUtil.adminOrOwnerCheck(session.getUser(), foresaken.getUser());
            
            foresaken.end(sql);
        } finally {
            sql.close();
        }

        UserSessionResponse response = foresaken.toResponse(ApiStatus.OK);
        return Response.ok(response.serialize()).build();
    }

    /**
     * Deletes all of a user's sessions. Requires authentication by the session
     * owner or a ROLE_ADMIN user.
     * @param sessionHeader The authentication information of the caller.
     * @param username The username of the session owner.
     * @return 200 OK.
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteAllSessions(@HeaderParam(UserSession.HEADER)String sessionHeader,
                                      @PathParam("username")String username)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        try {
            UserSession session = UserSession.resume(sql, sessionHeader);

            if (!session.getUser().getUsername().equals(username) &&
                !session.getUser().isRole("ROLE_ADMIN")) {
                throw new ApiException(ApiStatus.ACCESS_DENIED);
            }

            UserSession.endAll(sql, username);
        } finally {
            sql.close();
        }

        // TODO: we should probably return the sessions that were killed.
        ApiResponse response = new ApiResponse(ApiStatus.OK);
        return Response.ok(response.serialize()).build();
    }
}
