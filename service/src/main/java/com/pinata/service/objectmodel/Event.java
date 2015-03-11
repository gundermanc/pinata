package com.pinata.service.objectmodel;

import java.util.Date;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.EventResponse;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.datatier.EventsTable;

/**
 * Event API, used for all operations pertaining to a user and he or her
 * relations.
 * @author Christian Gunderman
 */
public class Event {

    /** Minumim name length. */
    private static int NAME_MIN = 5;
    /**
     * Maximum name length. You MUST make sure that this value is less
     * than or equal to the column width in EventsTable.
     */
    private static int NAME_MAX = 30;
    /** Location minimum length */
    private static int LOC_MIN = 0;
    /** Maximum Location length. */
    private static int LOC_MAX = 200;
    /** Event's id. */
    private int eid;
    /** Event's name */
    private String name;
    /** Event's location */
    private String location;
    /** Event's date */
    private Date date;
    /** Event's bring your own beer. */
    private Boolean byob;

    /**
     * Creates a new Event and stores in data tier.
     * @throws ApiException If a parameter is null or not within the accepted
     * set of values or a database error occurs.
     * @param sql The connection to the database.
     * @param name The name of the event.
     * @param location A description of where the event is.
     * @param date The date for the event.
     * @param byob Short for bring your own beer.
     * @return A new Event object containing the created Event.
     */
    public static Event create(SQLConnection sql,
                               String name,
                               String location,
                               Date date,
                               Boolean byob) throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(name);
        OMUtil.nullCheck(location);
        OMUtil.nullCheck(date);
        OMUtil.nullCheck(byob);

        // Check name length.
        if (name.length() > NAME_MAX || name.length() < NAME_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_EVENT_NAME_LENGTH);
        }

        // Check location length.
        if (location.length() > LOC_MAX || location.length() < LOC_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_EVENT_LOC_LENGTH);
        }

        // Check for future dates.
        Date today = new Date();
        if (!today.after(date)) {
            throw new ApiException(ApiStatus.APP_INVALID_EVENT_DATE);
        }

        // Query DB.
        int eid = EventsTable.insertEvent(sql, name, location, date, byob);
        return new Event(eid, name, location, date, byob);
    }

    /**
     * Looks up an event in the datatier and returns it as a Event object.
     * @throws ApiException With APP_EVENT_NOT_EXIST if event doesn't exist     * , or another code if SQL error occurs.
     * @param sql The SQL connection.
     * @param eid The id of the event to look up.
     * @return A new event object for the event.
     */
    public static Event lookup(SQLConnection sql, int eid)
        throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(eid);

        ResultSet result = EventsTable.lookupEvent(sql, eid);

        // Build Event object.
        try {
            // Get the first (and only) row or throw if event not exist.
            if (!result.next()) {
                throw new ApiException(ApiStatus.APP_EVENT_NOT_EXIST);
            }
            
            return new Event(result.getInt("eid"),
                            result.getString("name"),
                            result.getString("location"),
                            result.getDate("date"),
                            result.getBoolean("byob"));
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes an event.
     * @throws ApiException If a database error occurs.
     * @param sql The SQL connection.
     * @param username The eid of the event to delete.
     */
    public static void delete(SQLConnection sql, int eid)
        throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(eid);

        // Try to delete
        EventsTable.deleteEvent(sql, eid);
    }

    /**
     * Deletes this event.
     * @throws ApiException If a database error occurs.
     * @param sql The SQL connection.
     */
    public void delete(SQLConnection sql) throws ApiException {
        Event.delete(sql, this.getID());
    }
    
    /**
     * Gets even't id.
     * @return Unique event id.
     */
    public int getID() {
        return this.eid;
    }

    /**
     * Gets event's name.
     * @return Event's name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Gets event's location as a string.
     * @return The location of the event.
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Gets event's date
     * @return The date of the event.
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * Gets whether or not the event is bring your own beer.
     * @return The byob status of the event.
     */
    public boolean getByob() {
        return this.byob;
    }

    public EventResponse toResponse(ApiStatus status) {
        return new EventResponse(status,
                                this.getID(),
                                this.getName(),
                                this.getLocation(),
                                this.getDate(),
                                this.getByob());
    }

    /**
     * Creates a new Event OM object. Constructor is private because Event
     * objects can only be created internally from data tier or calls to create().
     * @param eid The event's id.
     * @param name The event's name.
     * @param location The event's location.
     * @param date The event's date.
     * @param byob The byob status of the event.
     */
    private Event(int eid, String name, String location, Date date, boolean byob) {
        this.eid = eid;
        this.name = name;
        this.location = location;
        this.date = date;
        this.byob = byob;
    }
}
