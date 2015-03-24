package com.pinata.service.datatier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.Date;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

/**
 * Wrapper class for SQL Events Table. Ideally there should be a 1:1 mapping
 * between SQL queries and methods in this file. Methods in this file, although
 * public, need maintain only basic database integrity. Exhaustive error
 * checking is done in Event.java in objectmodel.
 */
public class EventsTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE Events(" +
        "  eid INT NOT NULL AUTO_INCREMENT," +
        "  name VARCHAR(30) NOT NULL," +
        "  location VARCHAR(200) NOT NULL," +
        "  date DATETIME NOT NULL," +
        "  byob BOOLEAN NOT NULL," +
        "  hostID MEDIUMINT NOT NULL, " +
        "  PRIMARY KEY(eid), " +
        "  FOREIGN KEY(hostID) REFERENCES User(uid) ON DELETE CASCADE " +
        ")";

    /** Create events query. */
    private static final String INSERT_EVENT_QUERY =
        "INSERT INTO Events (name, location, date, byob, hostID)" +
        " VALUES (?,?,?,?,?)";

    /** Lookup event query. */
    private static final String LOOKUP_EVENT_QUERY =
        "SELECT * FROM Events WHERE eid=?";

    /** hosted events query. */
    private static final String HOSTED_EVENTS_QUERY =
        "SELECT * FROM Events WHERE hostID=?";

    /** Delete event query. */
    private static final String DELETE_EVENT_QUERY =
        "DELETE FROM Events WHERE eid=?";

    /**
     * Creates this table, failing if it already exists.
     * @param sql The SQLConnection instance. Probably requires root.
     */
    public static void create(SQLConnection sql) throws ApiException {
        try {
            Connection connection = sql.connection;
            PreparedStatement createStatement
                = connection.prepareStatement(CREATE_TABLE_QUERY);
            createStatement.execute();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Inserts a new event into this table. Note: this method does not
     * perform exhaustive checks. That should be done by caller.
     * @throws ApiException Thrown if an SQLException occurs.
     * @param sql The SQLConnection.
     * @param name The name of the event.
     * @param location A description of where the event is.
     * @param date The date for the event.
     * @param byob Short for bring your own beer.
     * @param hostID The uid of the host of the event
     * @return The id of the inserted event.
     */
    public static int insertEvent(SQLConnection sql,
                                  String name,
                                  String location,
                                  Date date,
                                  Boolean byob, 
                                  int hostID) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_EVENT_QUERY,
                                              Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(1, name);
            insertStatement.setString(2, location);
            insertStatement.setDate(3, new java.sql.Date(date.getTime()));
            insertStatement.setBoolean(4, byob);
            insertStatement.setInt(5, hostID);
            insertStatement.execute();
            //return the id of the new event
            ResultSet rs = insertStatement.getGeneratedKeys();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Looks up an event in the database and returns the associated ResultSet.
     * @throws ApiException If a SQL error occurs.
     * @param sql The database connection.
     * @param eid The id of the event to look up.
     * @return user A ResultSet containing the event. Should contain only a
     * single row.
     */
    public static ResultSet lookupEvent(SQLConnection sql, int eid)
        throws ApiException {

        Connection connection = sql.connection;

        try {
            PreparedStatement hostedStatement =
                connection.prepareStatement(LOOKUP_EVENT_QUERY);
            hostedStatement.setInt(1, eid);

            return hostedStatement.executeQuery();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Looks up all events in the database hosted by the host and returns the associated ResultSet.
     * @throws ApiException If a SQL error occurs.
     * @param sql The database connection.
     * @param hostID The id of the host to look up.
     * @return A ResultSet containing the events.
     */
    public static ResultSet eventsByHost(SQLConnection sql, int hostID)
        throws ApiException{

        Connection connection = sql.connection;

        try {
            PreparedStatement lookupStatement =
                connection.prepareStatement(HOSTED_EVENTS_QUERY);
            lookupStatement.setInt(1, hostID);

            return lookupStatement.executeQuery();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }

    }

    /**
     * Deletes an event in the database.
     * @throws ApiException If a SQL error occurs or unable to delete.
     * @param sql The database connection.
     * @param eid The id of the event to delete.
     */
    public static void deleteEvent(SQLConnection sql, int eid)
        throws ApiException {

        Connection connection = sql.connection;

        try {
            PreparedStatement deleteStatement =
                connection.prepareStatement(DELETE_EVENT_QUERY);
            deleteStatement.setInt(1, eid);

            // Execute and check that deletion was successful.
            if (deleteStatement.executeUpdate() != 1) {
                throw new ApiException(ApiStatus.APP_EVENT_NOT_EXIST);
            }
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
