package com.pinata.shared;

import flexjson.JSON;

/**
 * JSON Response passed to client from server after request.
 * @author Christian Gunderman
 */
public class ApiResponse extends Json {

    /** The status prepended to this JSON Response */
    @JSON(include=false)
    private final ApiStatus status;

    /**
     * Creates a new Response object.
     * @param status The status of the request.
     */
    public ApiResponse(ApiStatus status) {
        super();
        this.status = status;
    }

    /**
     * Flexjson serialization method.
     * @return The String form of the application status.
     */
    @JSON(include=true, name="status")
    public String getStatusJSON() {
        return this.status.status;
    }

    /**
     * Flexjson serialization method.
     * @return The error code for the current state.
     */
    @JSON(include=true, name="code")
    public int getCodeJSON() {
        return this.status.code;
    }

    /**
     * Flexjson serialization method.
     * @return The HTTP response code associated
     * with current status/error.
     */
    @JSON(include=true, name="http_code")
    public int getHttpCodeJSON() {
        return this.status.httpCode;
    }

    /**
     * Flexjson serialization method.
     * @return The error message String.
     */
    @JSON(include=true, name="message")
    public String getMessageJSON() {
        return this.status.message;
    }
}
