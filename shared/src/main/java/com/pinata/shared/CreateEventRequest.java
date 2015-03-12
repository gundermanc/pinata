package com.pinata.shared;

import java.util.Date;

import flexjson.JSON;

/**
 * JSON Request for Creating a event.
 */
public class CreateEventRequest extends ApiRequest {

    /** New event's name. */
    @JSON(include=true, name="name")
    public String name;

    /** New event's location. */
    @JSON(include=true, name="location")
    public String location;

    /** New event's byob." */
    @JSON(include=true, name="byob")
    public boolean byob;

    /** New event's date. */
    @JSON(include=true, name="date")
    public Date date;

    /**
     * Creates a new CreateEventRequest with the provided field values.
     * @param name Event name of the new event.
     * @param location The new event's location.
     * @param byob Either true or false.
     * @param date The new event's date.
     */
    public CreateEventRequest(String name,
                             String location,
                             boolean byob,
                             Date date) {
        this.name = name;
        this.location = location;
        this.byob = byob;
        this.date = date;
    }

    /**
     * Creates a CreateEventRequest object with all null fields.
     */
    public CreateEventRequest() { }
}
