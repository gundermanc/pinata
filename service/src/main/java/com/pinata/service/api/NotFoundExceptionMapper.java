package com.pinata.service.api;

import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import com.sun.jersey.api.NotFoundException;

import com.pinata.shared.ApiStatus;
import com.pinata.shared.ErrorApiResponse;

/**
 * NotFoundException mapper. Maps resource not found into appropriate API error
 * in JSON serialized format.
 * @author Christian Gunderman
 */
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {

    /**
     * Serializes NotFoudnException to JSON ErrorApiResponse.
     */
    @Override
    @Produces(MediaType.APPLICATION_JSON)
    public Response toResponse(NotFoundException ex) {
        return Response.status(ApiStatus.NOT_FOUND.httpCode)
            .entity(new ErrorApiResponse(ApiStatus.NOT_FOUND, ex)
                    .serialize()).build();
    }
}
