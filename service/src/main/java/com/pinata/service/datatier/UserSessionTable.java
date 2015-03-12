package com.pinata.service.datatier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.Date;
import java.util.UUID;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

import javax.mail.internet.InternetAddress;

/**
 * Wrapper class for SQL UserSessionTable.
 * Manages authenticated User sessions, log in, and log out.
 *
 * Table is currently configured to allow only one log in per user,
 * but can be easily reconfigured to change that by removing
 * ON DUPLICATE UPDATE from insert query and by adding session_id
 * to the primary key.
 * @author Christian Gunderman
 */
public abstract class UserSessionTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE UserSession(" +
        "  uid MEDIUMINT NOT NULL," +
        "  session_id VARCHAR(36) NOT NULL," +
        "  PRIMARY KEY(uid)," +
        "  FOREIGN KEY (uid) REFERENCES User(uid) ON DELETE CASCADE" +
        ")";

    private static final String INSERT_SESSION_QUERY
        = "INSERT INTO UserSession (uid, session_id) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE session_id=VALUES(session_id)";

    private static final String DELETE_SESSION_QUERY
        = "DELETE FROM UserSession WHERE uid=? AND session_id=?";

    private static final String DELETE_ALL_SESSIONS_QUERY
        = "DELETE FROM UserSession WHERE uid=?";

    private static final String QUERY_SESSION_QUERY
        = "SELECT * FROM UserSession WHERE uid=? AND session_id=?";

    private static final String DELETE_ALL_SESSIONS_NAME_QUERY
        = "DELETE FROM UserSession WHERE " +
        "uid=(SELECT uid FROM User WHERE user=?)";

    /**
     * Creates this table, failing if it already exists.
     * @param sql The SQLConnection instance. Probably requires root.
     */
    public static void create(SQLConnection sql) throws ApiException {
        try {
            Connection connection = sql.connection;

            // Create table.
            PreparedStatement createStatement
                = connection.prepareStatement(CREATE_TABLE_QUERY);
            createStatement.execute();
            createStatement.close();

        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Inserts a Session into the table.
     * @param sql The SQLConnection.
     * @param uid The table AUTO_INCREMENT index of the user.
     * @param uuid The session id.
     */
    public static void insertSession(SQLConnection sql,
                                     int uid,
                                     UUID uuid) throws ApiException {
        try {
            Connection connection = sql.connection;

            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_SESSION_QUERY);
            insertStatement.setInt(1, uid);
            insertStatement.setString(2, uuid.toString());

            insertStatement.execute();
            insertStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes a session.
     * @param sql The SQLConnection
     * @param uid The user AUTO_INCREMENT index integer.
     * @param sessionId The unique session id.
     */
    public static void deleteSession(SQLConnection sql,
                                     int uid,
                                     UUID sessionId) throws ApiException {
        try {
            Connection connection = sql.connection;

            PreparedStatement deleteStatement
                = connection.prepareStatement(DELETE_SESSION_QUERY);
            deleteStatement.setInt(1, uid);
            deleteStatement.setString(2, sessionId.toString());

            deleteStatement.execute();
            deleteStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes all of a user's sessions.
     * @param sql The SQLConnection
     * @param uid The user AUTO_INCREMENT index integer.
     */
    public static void deleteAllSessions(SQLConnection sql,
                                         int uid) throws ApiException {
        try {
            Connection connection = sql.connection;

            PreparedStatement deleteStatement
                = connection.prepareStatement(DELETE_ALL_SESSIONS_QUERY);
            deleteStatement.setInt(1, uid);

            deleteStatement.execute();
            deleteStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes all of user's sessions.
     * @param sql The SQLConnection
     * @param uid The user AUTO_INCREMENT index integer.
     */
    public static void deleteAllSessions(SQLConnection sql,
                                         String username) throws ApiException {
        try {
            Connection connection = sql.connection;

            PreparedStatement insertStatement
                = connection.prepareStatement(DELETE_ALL_SESSIONS_NAME_QUERY);
            insertStatement.setString(1, username);

            insertStatement.execute();
            insertStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Checks if a session exists.
     * @param sql The SQLConnection
     * @param uid The user AUTO_INCREMENT index integer.
     * @param sessionId The unique session id.
     * @return True if the session exists.
     */
    public static boolean sessionExists(SQLConnection sql,
                                        int uid,
                                        UUID sessionId) throws ApiException {
        try {
            Connection connection = sql.connection;

            PreparedStatement queryStatement
                = connection.prepareStatement(QUERY_SESSION_QUERY);
            queryStatement.setInt(1, uid);
            queryStatement.setString(2, sessionId.toString());

            ResultSet result = queryStatement.executeQuery();
            boolean sessionExists = result.next();

            queryStatement.close();

            return sessionExists;
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
