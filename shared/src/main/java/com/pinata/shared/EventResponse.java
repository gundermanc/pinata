package com.pinata.shared;

import java.util.Date;

import flexjson.JSON;

/**
 * Event JSON Response from server to client.
 * @author Elliot Essman
 */
public class EventResponse extends ApiResponse {

    /** EventID. */
    @JSON(include=true, name="event_id")
    public int eventID;

    /** Event name */
    @JSON(include=true, name="name")
    public String name;

    /** Event location */
    @JSON(include=true, name="location")
    public String location;

    /** Event date and time */
    @JSON(include=true, name="date")
    public Date date;

    /** Event is byob? */
    @JSON(include=true, name="byob")
    public boolean byob;

    /** ID of event's host */
    @JSON(include=true, name="hostID")
    public int hostID;


    /**
     * Creates uninitialized EventResponse for client side deserialization.
     */
    public EventResponse() {
        this(null, -1, null, null, null, false, -1);
    }

    /**
     * Creates a new EventResponse object.
     * @param status The status of the operation.
     * @param eventID The ID of the new event.
     * @param name The name of the event.
     * @param location The location of the event.
     * @param date The date and time of the new event.
     * @param byob Bring your own beer to the event.
     */
    public EventResponse(ApiStatus status,
                        int eventID,
                        String name,
                        String location,
                        Date date,
                        boolean byob,
                        int hostID) {
        super(status);
        this.eventID = eventID;
        this.name = name;
        this.location = location;
        this.date = date;
        this.byob = byob;
        this.hostID = hostID;
    }
}
