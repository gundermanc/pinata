package com.pinata.service.api;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.pinata.shared.ApiException;
import com.pinata.shared.ErrorApiResponse;

/**
 * ApiException mapper. Maps ApiExceptions thrown in resources into appropriate
 * JSON serialized format.
 * @author Christian Gunderman
 */
@Provider
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    /**
     * Serializes ApiExceptions to JSON ErrorApiResponse.
     */
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(ApiException ex) {
        return Response.status(ex.status.httpCode)
            .entity(new ErrorApiResponse(ex.status, ex.getCause()).serialize()).build();
    }
}
