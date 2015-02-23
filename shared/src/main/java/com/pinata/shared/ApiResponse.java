package com.pinata.shared;

import flexjson.JSON;

/**
 * JSON Response passed to client from server after request.
 * @author Christian Gunderman
 */
public class ApiResponse extends Json {

    /** Server Status Enum as String. */
    @JSON(include=true, name="status")
    public String status;

    /** Server error code integer. */
    @JSON(include=true, name="code")
    public int code;

    /** HTTP response code as integer. */
    @JSON(include=true, name="http_code")
    public int httpCode;

    /** Status message as String. */
    @JSON(include=true, name="message")
    public String message;

    /**
     * Creates a new Response object.
     * @param status The status of the request.
     */
    public ApiResponse(ApiStatus status) {
        super();

        // Accept null status.
        if (status == null) {
            return;
        }

        this.status = status.status;
        this.code = status.code;
        this.httpCode = status.httpCode;
        this.message = status.message;
    }
}
