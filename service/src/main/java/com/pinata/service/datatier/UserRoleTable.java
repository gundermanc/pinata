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
 * Wrapper class for SQL UserRole Table. Defines UserRoles.
 * @author Christian Gunderman
 */
public abstract class UserRoleTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE UserRole(" +
        "  rid MEDIUMINT AUTO_INCREMENT," +
        "  role VARCHAR(25) NOT NULL," +
        "  description VARCHAR(255) NOT NULL," +
        "  PRIMARY KEY(rid)," + 
        "  UNIQUE (role), " +
        "  INDEX USING HASH(role)" +
        ")";

    /** Create role query. */
    private static final String INSERT_ROLE_QUERY
        = "INSERT INTO UserRole (role, description) VALUES (?,?)";

    /** Lookup Role query. */
    private static final String LOOKUP_ROLE_QUERY
        = "SELECT * FROM UserRole WHERE role=?";

    /** Delete Role query. */
    private static final String DELETE_ROLE_QUERY
        = "DELETE FROM UserRole WHERE role=?";

    /** Admin Role ID. */
    public static final String ROLE_ADMIN_ID = "ROLE_ADMIN";

    /** Admin Role Description. */
    private static final String ROLE_ADMIN_DESCRIPTION
        = "Administrator with unrestricted access.";

    /** User Role ID. */
    public static final String ROLE_USER_ID = "ROLE_USER";

    /** User Role Description. */
    private static final String ROLE_USER_DESCRIPTION
        = "Standard user account.";

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

            // Create default Admin and User roles.
            insertUserRole(sql,
                           ROLE_ADMIN_ID,
                           ROLE_ADMIN_DESCRIPTION);
            insertUserRole(sql,
                           ROLE_USER_ID,
                           ROLE_USER_DESCRIPTION);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Adds a new UserRole to the table with the specified Role ID
     * and descriptions.
     * @param sql The SQLConnection.
     * @param roleId The unique id string for this role.
     * @param description The role's description.
     */
    public static void insertUserRole(SQLConnection sql,
                                      String roleId,
                                      String description) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_ROLE_QUERY);
            insertStatement.setString(1, roleId);
            insertStatement.setString(2, description);

            insertStatement.execute();
            insertStatement.close();
        } catch (SQLIntegrityConstraintViolationException ex) {
            // Thrown if role id already exists.
            throw new ApiException(ApiStatus.APP_ROLE_ID_TAKEN, ex);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Looks up a role by unique Role id string.
     * @param sql The SQLConnection.
     * @param roleId The id String for the role to look up.
     * @return A ResultSet containing the Role columns.
     */
    public static ResultSet lookupUserRole(SQLConnection sql,
                                           String roleId) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement lookupStatement
                = connection.prepareStatement(LOOKUP_ROLE_QUERY);

            lookupStatement.setString(1, roleId);

            return lookupStatement.executeQuery();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR);
        }
    }

    /**
     * Deletes a User Role by it's String id.
     * @param sql The SQLConnection.
     * @param roleId The unique Role string id.
     */
    public static void deleteUserRole(SQLConnection sql,
                                      String roleId) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement deleteStatement
                = connection.prepareStatement(DELETE_ROLE_QUERY);

            deleteStatement.setString(1, roleId);

            if (deleteStatement.executeUpdate() != 1) {
                throw new ApiException(ApiStatus.APP_ROLE_NOT_EXIST);
            }
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR);
        }
    }
}
