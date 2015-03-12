package com.pinata.service.datatier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.Date;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

import javax.mail.internet.InternetAddress;

/**
 * Implements SQL queries for the checking if a User has a specific role.
 * @author Christian Gunderman
 */
public abstract class UserHasRoleTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE UserHasRole(" +
        "  uid MEDIUMINT NOT NULL," +
        "  rid MEDIUMINT NOT NULL," +
        "  PRIMARY KEY(uid, rid)," +
        "  FOREIGN KEY (uid) REFERENCES User(uid) ON DELETE CASCADE," +
        "  FOREIGN KEY (rid) REFERENCES UserRole(rid) ON DELETE CASCADE" +
        ")";

    /** Mark user as having a Role. */
    private static final String INSERT_USER_HAS_ROLE_QUERY
        = "INSERT INTO UserHasRole (uid, rid) VALUES" +
        " (?, (SELECT rid FROM UserRole R WHERE R.role=?))";

    /** Mark user as having a Role by Role String id. */
    private static final String DELETE_USER_HAS_ROLE_NAME_QUERY
        = "DELETE FROM UserHasRole WHERE uid=?" +
        " AND rid=(SELECT rid FROM UserRole R WHERE R.role=?)";

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
     * Give User requested Role.
     * @throws ApiException If database error or user already has Role, or
     * requested Role doesn't exist.
     * @param uid The user's unique AUTO_INCREMENT id from the table.
     * @param roleName The String id of the Role.
     * @return The ResultSet containing the Role information.
     */
    public static ResultSet insertUserHasRole(SQLConnection sql,
                                              int uid,
                                              String roleName) throws ApiException {
        try {
            Connection connection = sql.connection;

            // Insert user role record.
            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_USER_HAS_ROLE_QUERY);
            insertStatement.setInt(1, uid);
            insertStatement.setString(2, roleName);

            insertStatement.execute();

            ResultSet result = insertStatement.getGeneratedKeys();

            return result;
        } catch (SQLIntegrityConstraintViolationException ex) {
            // User is already of given Role or Role not exist.
            throw new ApiException(
                ApiStatus.APP_USER_HAS_ROLE_DUPLICATE, ex);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Remove user from Role.
     * @throws ApiException If database error or user doesn't have Role.
     * @param uid The user's unique AUTO_INCREMENT id from the table.
     * @param rid The unique AUTO_INCREMENT id from the UserRole table.
     * @return The ResultSet containing the Role information.
     */
    public static void deleteUserHasRole(SQLConnection sql,
                                         int uid,
                                         String role) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement deleteStatement
                = connection.prepareStatement(DELETE_USER_HAS_ROLE_NAME_QUERY);
            deleteStatement.setInt(1, uid);
            deleteStatement.setString(2, role);

            deleteStatement.execute();

            deleteStatement.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            // User doesn't have Role.
            throw new ApiException(
                ApiStatus.APP_USER_NOT_HAVE_ROLE, ex);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
