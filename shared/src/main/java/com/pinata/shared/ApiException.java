package com.pinata.shared;

/**
 * ApiException for errors originating on the server side.
 * @author Christian Gunderman
 */
public class ApiException extends Exception {

    /** Application status code. */
    public final ApiStatus status;

    /**
     * Creates a new ApiException.
     * @param status The application status that is representative of the
     * error.
     */
    public ApiException(ApiStatus status) {
        this(status, null);
    }

    /**
     * Creates a new ApiException.
     * @param status The application status that is representative of the
     * error.
     * @param cause The exception that caused this exception.
     */
    public ApiException(ApiStatus status, Throwable cause) {
        super(status.message, cause);
        this.status = status;
    }

    /**
     * Gets string representation of this exception.
     * @return The enum name of the error associated with this exception.
     */
    @Override
    public String toString() {
        return this.status.status;
    }
}
