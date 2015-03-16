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
import com.pinata.service.objectmodel.User;
import com.pinata.service.objectmodel.UserSession;
import com.pinata.service.objectmodel.OMUtil;

import com.pinata.shared.*;

/**
 * Users resource used for the creation of users. v1.
 * @author Christian Gunderman
 */
@Path("v1/users")
public class Users1Resource {

    /** Injected information about the URI of the current request. */
    @Context
    UriInfo uriInfo;

    /**
     * Post Request. Creates a new user in the database and returns a
     * CreateUserResponse, or an ErrorApiResponse in JSON.
     * @param jsonBody The POST json body.
     * @return The input values in a CreateUserResponse with SUCCESS,
     * or an ErrorApiResponse upon an error.
     * Note: usernames must be unique.
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(String jsonBody) throws ApiException {
        CreateUserRequest request = new CreateUserRequest();
        request.deserializeFrom(jsonBody);

        SQLConnection sql = SQLConnection.connectDefault();

        User user = null;
        try {
            user = User.create(sql,
                               request.user,
                               request.pass,
                               request.firstName,
                               request.lastName,
                               request.gender,
                               request.birthday,
                               request.email);
        } finally {
            sql.close();
        }

        URI newUserUri = uriInfo.getRequestUriBuilder()
            .path(user.getUsername()).build();

        UserResponse userResponse = user.toResponse(ApiStatus.CREATED);

        return Response.created(newUserUri)
            .entity(userResponse.serialize()).build();
    }

    /**
     * GET request. Pulls down user's profile.
     * @param username Username of the user to pull down.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public Response getUser(@PathParam("username") String username)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        User user = null;
        try {
            user = User.lookup(sql, username);
        } finally {
            sql.close();
        }

        UserResponse userResponse = user.toResponse(ApiStatus.OK);
        return Response.ok(userResponse.serialize()).build();
    }

    /**
     * DELETE request. Deletes the specified user and returns it's details.
     * Must be authenticated to hit this end point or ACCESS_DENIED will result.
     * Users may only be deleted by themselves or by an admin.
     * @param username Username of the user to delete
     */
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{username}")
    public Response deleteUser(@HeaderParam(UserSession.HEADER) String sessionHeader,
                               @PathParam("username") String username)
        throws ApiException {

        SQLConnection sql = SQLConnection.connectDefault();

        UserSession session = null;
        User foresaken = null;
        try {
            // Resume session, if a user is logged in.
            session = UserSession.resume(sql, sessionHeader);

            foresaken = User.lookup(sql, username);

            // Only delete if user is deleting themself or admin is deleting.
            OMUtil.adminOrOwnerCheck(session.getUser(), foresaken);
        } finally {
            sql.close();
        }

        UserResponse userResponse = foresaken.toResponse(ApiStatus.DELETED);
        return Response.ok(userResponse.serialize()).build();
    }
}
