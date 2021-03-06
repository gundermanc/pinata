package com.pinata.shared;

/**
 * ApiStatus and error codes and messages.
 * @author Christian Gunderman
 */
public enum ApiStatus {

    /*
     * DANGER!!!!
     * Make sure that the first param for each value is UNIQUE when merging.
     */

    // Success codes.
    OK(200, 200, "Success."),
    CREATED(201, 201, "Created."),
    DELETED(225, 200, "Deleted."),

    // Client errors.
    MALFORMED_REQUEST(400, 400, "Server received malformed request."),
    INVALID_SESSION_HEADER(401, 400, "Malformed request. Invalid session header."),
    INVALID_SESSION(402, 400, "Invalid session."),
    JSON_DS_ERROR(403, 400, "Unable to deserialize JSON request."),
    NOT_FOUND(404, 404, "Server resource not found."),
    INVALID_GENDER(405, 400, "Invalid gender. Gender must be MALE or FEMALE."),

    // Server errors.
    UNKNOWN_ERROR(500, 500, "Unknown error occurred."),
    DATABASE_ERROR(501, 500, "Error processing database request."),
    INSTALL_ERROR(502, 500, "Incorrect server configuration."),
    NO_SQL(503, 500, "No SQL instance provided."),
    ACCESS_DENIED(503, 403, "Access denied."),

    // App logic errors.
    APP_INVALID_USER_LENGTH(701, 400, "Username is either too short or too long."),
    APP_INVALID_PASS_LENGTH(702, 400, "Password is either too short or too long."),
    APP_INVALID_BIRTHDAY(703, 400, "Birthday is in the future."),
    APP_USERNAME_TAKEN(704, 400, "A user with this username already exists."),
    APP_USER_NOT_EXIST(705, 400, "The requested user does not exist."),
    APP_INVALID_EMAIL(706, 400, "Email address is invalid."),
    APP_INVALID_USERNAME(707, 400, "Username contains invalid character(s)."),
    APP_INVALID_PASSWORD(708, 403, "Invalid password."),
    APP_EVENT_NOT_EXIST(709, 400, "No events found with given id."),
    APP_INVALID_EVENT_NAME_LENGTH(710, 400, "Event name is either too short or too long."),
    APP_INVALID_EVENT_LOC_LENGTH(711, 400, "Event location is either too short or too long."),
    APP_INVALID_EVENT_DATE(712, 400, "Event date is in the past."),
    APP_INVALID_ROLE_LENGTH(707, 400, "Invalid Role ID length."),
    APP_INVALID_ROLE(708, 400, "Invalid Role ID. Must start with ROLE_"),
    APP_INVALID_ROLE_DESCRIPTION_LENGTH(709, 400, "Invalid Role description length."),
    APP_ROLE_ID_TAKEN(710, 400, "Can't add Role. Role already exists."),
    APP_USER_HAS_ROLE_DUPLICATE(711, 400, "User already has Role, or Role doesn't exist."),
    APP_USER_NOT_HAVE_ROLE(712, 400, "User does not have Role."),
    APP_ROLE_NOT_EXIST(713, 400, "Role does not exist."),
    APP_INVALID_NAME(714, 400, "First name or last name is too short or too long.");

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
