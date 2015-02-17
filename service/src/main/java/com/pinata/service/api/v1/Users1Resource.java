package com.pinata.service.api.v1;

import java.util.Date;
import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.WebApplicationException;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.objectmodel.User;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.CreateUserRequest;
import com.pinata.shared.CreateUserResponse;

/**
 * Users resource used for the creation and deletion of users. v1.
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
    public Response post(String jsonBody) throws ApiException {
        CreateUserRequest request = new CreateUserRequest();
        request.deserializeFrom(jsonBody);

        SQLConnection sql = SQLConnection.connectDefault();

        User user = null;
        try {
            user = User.create(sql,
                               request.user,
                               request.pass,
                               request.gender,
                               request.birthday);
        } finally {
            sql.close();
        }

        URI newUserUri = uriInfo.getRequestUriBuilder()
            .path(user.getUsername()).build();
        CreateUserResponse createUserResponse 
            = new CreateUserResponse(ApiStatus.CREATED,
                                     user.getUsername(),
                                     user.getGender(),
                                     user.getJoinDate(),
                                     user.getBirthday());
        return Response.created(newUserUri)
            .entity(createUserResponse.serialize()).build();
    }
}
