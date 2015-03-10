package com.pinata.shared;

import flexjson.JSON;

/**
 * JSON Request for creating a session.
 * @author Christian Gunderman
 */
public class CreateUserSessionRequest extends ApiRequest {

    /** Session requester's username. */
    @JSON(include=true, name="user")
    public String user;

    /** Session requester's password.  */
    @JSON(include=true, name="pass")
    public String pass;

    /**
     * Creates a new CreateSessionRequest with the provided field values.
     * @param user User name of the new user.
     * @param pass The new user's password.
     */
    public CreateUserSessionRequest(String user,
                                    String pass) {
        this.user = user;
        this.pass = pass;
    }

    /**
     * Creates a CreateUserRequest object with all null fields.
     */
    public CreateUserSessionRequest() { }
}
