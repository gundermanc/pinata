package com.pinata.service.api;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiResponse;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.ErrorApiResponse;

/**
 * UncheckedException mapper. Maps unchecked exceptions (bugs) to a
 * general server error message.
 * @author Christian Gunderman
 */
@Provider
public class UncheckedExceptionMapper implements ExceptionMapper<Throwable> {

    /**
     * Serializes Unchecked exceptions to generic error message.
     */
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(Throwable ex) {
        return Response.status(ApiStatus.UNKNOWN_ERROR.httpCode)
            .entity(new ErrorApiResponse(ApiStatus.UNKNOWN_ERROR, ex).serialize())
            .build();
    }
}
