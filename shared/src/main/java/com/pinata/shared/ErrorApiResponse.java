package com.pinata.shared;

import flexjson.JSON;

/**
 * JSON Response passed to client from server upon error.
 * @author Christian Gunderman
 */
public class ErrorApiResponse extends ApiResponse {

    /** Inner debug exception. */
    @JSON(include=false)
    private final Throwable innerException;

    /**
     * Allow for empty ErrorApiResponse so we can deserialize into
     * it client side.
     */
    public ErrorApiResponse() {
        this(null, null);
    }

    /**
     * Creates a new ErrorApiResponse.
     * @param status The ApiStatus object containing the error description.
     * @param cause The exception that caused the error.
     */
    public ErrorApiResponse(ApiStatus status, Throwable innerException) {
        super(status);
        this.innerException = innerException;
    }

    /**
     * The message contained in the inner exception. This information is
     * intended for debugging purposes only and is NOT to be consumed.
     * @return Inner exception message.
     */
    @JSON(include=true, name="inner_exception_message")
    public String getInnerExceptionMessageJSON() {
        return this.innerException != null
            ? this.innerException.getMessage() : null;
    }

    /**
     * The class name of the inner exception. This information is intended
     * for debugging purposes only and is NOT to be consumed.
     * @return Inner exception class.
     */
    @JSON(include=true, name="inner_exception")
    public String getInnerExceptionJSON() {
        return this.innerException != null
            ? this.innerException.getClass().getName() : null;
    }
}
