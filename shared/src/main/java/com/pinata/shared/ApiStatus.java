package com.pinata.shared;

/**
 * ApiStatus and error codes and messages.
 * @author Christian Gunderman
 */
public enum ApiStatus {
    // Success codes.
    OK(200, 200, "Success."),
    CREATED(201, 201, "Created."),
    DELETED(225, 200, "Deleted."),

    // Client errors.
    MALFORMED_REQUEST(400, 400, "Server received malformed request."),
    JSON_DS_ERROR(401, 400, "Unable to deserialize JSON request."),
    NOT_FOUND(404, 404, "Server resource not found."),
    INVALID_GENDER(408, 400, "Invalid gender. Gender must be MALE or FEMALE."),

    // Server errors.
    UNKNOWN_ERROR(500, 500, "Unknown error occurred."),
    DATABASE_ERROR(501, 500, "Error processing database request."),
    INSTALL_ERROR(502, 500, "Incorrect server configuration."),
    NO_SQL(503, 500, "No SQL instance provided."),

    // App logic errors.
    APP_INVALID_USER_LENGTH(701, 400, "Username is either too short or too long."),
    APP_INVALID_PASS_LENGTH(702, 400, "Password is either too short or too long."),
    APP_INVALID_BIRTHDAY(703, 400, "Birthday is in the future."),
    APP_USERNAME_TAKEN(704, 400, "A user with this name already exists."),
    APP_USER_NOT_EXIST(704, 400, "The requested user does not exist.");

    /** The String name of the state (OK, MALFORMED_REQUEST, ...) */
    public final String status;
    /** The integer code for this error or state. */
    public final int code;
    /** The http code that should be returned from this error. */
    public final int httpCode;
    /** The String message explaining this error. */
    public final String message;

    /**
     * Constructor for error messages and status codes.
     * @param code The unique integer error code.
     * @param httpCode One of the standardized http return codes.
     * @param message The message for this error or state.
     */
    ApiStatus(int code, int httpCode, String message) {
        this.status = this.name();
        this.code = code;
        this.httpCode = httpCode;
        this.message = message;
    }
}
