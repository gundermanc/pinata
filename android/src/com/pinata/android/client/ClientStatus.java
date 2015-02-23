package com.pinata.android.client;

/**
 * ClientStatus and error messages.
 * @author Christian Gunderman
 */
public enum ClientStatus {
    // Success codes.
    OK(200, "Success."),

    // App Errors.
    APP_CANCELLED(300, "Cancelled."),
    APP_MUST_CHOOSE_GENDER(301, "You must first choose a gender."),

    // HTTP Client Errors.
    HTTP_UNKNOWN_ERROR(400, "Unknown error. Unable to reach server."),

    // HTTP Server API Errors.
    API_ERROR(500, "Server error.");

    /** The String name of the state (OK, MALFORMED_REQUEST, ...) */
    public final String status;
    /** The integer code for this error or state. */
    public final int code;
    /** The String message explaining this error. */
    public final String message;

    /**
     * Constructor for error messages.
     * @param code The unique integer error code.
     * @param message The message for this error or state.
     */
    ClientStatus(int code, String message) {
        this.status = this.name();
        this.code = code;
        this.message = message;
    }
}
