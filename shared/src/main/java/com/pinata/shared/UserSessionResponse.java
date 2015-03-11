package com.pinata.shared;

import java.util.UUID;

import flexjson.JSON;

/**
 * CreateUserSession JSON Response from server to client.
 * @author Christian Gunderman
 */
public class UserSessionResponse extends ApiResponse {

    /** Username. */
    @JSON(include=true, name="user")
    public String user;

    /** Password, always null. We NEVER pass it back. */
    @JSON(include=true, name="pass")
    public String pass = null;

    /** Session ID. */
    @JSON(include=true, name="session_id")
    public String sessionId;

    /**
     * Creates a new UserSessionResponse object.
     * @param status The status of the operation.
     * @param user The username of the new user.
     * @param gender MALE or FEMALE.
     * @param joinDate The date the new user joined.
     * @param birthday The birthday of the new user.
     */
    public UserSessionResponse(ApiStatus status,
                               String user,
                               UUID sessionId) {
        super(status);
        this.user = user;
        this.sessionId = sessionId != null ? sessionId.toString() : null;
    }

    /**
     * Creates uninitialized UserSessionResponse
     * for client side deserialization.
     */
    public UserSessionResponse() {
        this(null, null, null);
    }
}
