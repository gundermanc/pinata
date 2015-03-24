package com.pinata.shared;

import java.util.Date;
import java.util.List;

import flexjson.JSON;

/**
 * Multi-event JSON Response from server to client.
 * @author Elliot Essman
 */
public class EventsResponse extends ApiResponse {

    /** list of events */
    @JSON(include=true, name="events")
    public List<EventResponse> events;

    /**
     * Creates uninitialized EventsResponse for client side deserialization.
     */
    public EventsResponse() {
        this(null, null);
    }

    /**
     * Creates a new EventsResponse object.
     * @param status The status of the operation.
     * @param events The event responses.
     */
    public EventsResponse(ApiStatus status,
                        List<EventResponse> events) {
        super(status);
        this.events = events;
    }
}
