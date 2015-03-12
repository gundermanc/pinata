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
 * Wrapper class for SQL Users Table. Ideally there should be a 1:1 mapping
 * between SQL queries and methods in this file. Methods in this file, although
 * public, need maintain only basic database integrity. Exhaustive error
 * checking is done in User.java in objectmodel.
 * @author Christian Gunderman
 */
public abstract class UserTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE User(" +
        "  uid MEDIUMINT AUTO_INCREMENT," +
        "  user VARCHAR(25) NOT NULL," +
        "  pass VARCHAR(64) NOT NULL," +
        "  gender VARCHAR(6) NOT NULL," +
        "  join_date DATETIME NOT NULL," +
        "  birth_date DATETIME NOT NULL," +
        "  email VARCHAR(320) NOT NULL," + //smtp email max. is 320 chars
        "  PRIMARY KEY(uid)," +
        "  UNIQUE (user)," +
        "  INDEX USING HASH(user)" +
        ")";

    /** Create user query. */
    private static final String INSERT_USER_QUERY =
        "INSERT INTO User (user, pass, gender, join_date, birth_date, email)" +
        " VALUES (?,?,?,?,?,?)";

    /** Lookup user query. */
    private static final String LOOKUP_USER_QUERY =
        "SELECT * FROM User U, UserRole R, UserHasRole H WHERE U.user=? " +
        " AND U.uid=H.uid AND H.rid=R.rid";

    /** Delete user query. */
    private static final String DELETE_USER_QUERY =
        "DELETE FROM User WHERE user=?";

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
            createStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Inserts a new user into this table. Note: this method does not
     * perform exhaustive checks. That should be done by caller.
     * @throws ApiException Thrown if username is already taken or if
     * a SQLException occurs.
     * @param sql The SQLConnection.
     * @param user The username of the new user.
     * @param pass The password of the new user.
     * @param gender A value up to 6 chars. Should be MALE or FEMALE.
     * @param joinDate The date that the new user joined.
     * @param birthDate The new user's birthday.
     * @param email The new user's email address.
     * @return The uid of the new user.
     */
    public static int insertUser(SQLConnection sql,
                                 String user,
                                 String pass,
                                 String gender,
                                 Date joinDate,
                                 Date birthDate,
                                 InternetAddress email) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_USER_QUERY);
            insertStatement.setString(1, user);
            insertStatement.setString(2, pass);
            insertStatement.setString(3, gender);
            insertStatement.setDate(4, new java.sql.Date(joinDate.getTime()));
            insertStatement.setDate(5, new java.sql.Date(birthDate.getTime()));
            insertStatement.setString(6, email.getAddress());

            insertStatement.execute();

            // Get autoincrement row id.
            ResultSet result = insertStatement.getGeneratedKeys();
            result.next();

            int uid = result.getInt(1);
            
            insertStatement.close();

            return uid;
        } catch (SQLIntegrityConstraintViolationException ex) {
            // We have no foreign or unique keys other than primary
            // so this can only be thrown for duplicate users.
            throw new ApiException(ApiStatus.APP_USERNAME_TAKEN, ex);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Looks up a user in the database and returns the associated ResultSet with
     * with additional rows for each security UserRole the user has.
     * @throws ApiException If a SQL error occurs.
     * @param sql The database connection.
     * @param user The user to look up.
     * @return user A ResultSet containing the user. Should contain only a
     * single row.
     */
    public static ResultSet lookupUserWithRoles(SQLConnection sql, String user)
        throws ApiException {

        Connection connection = sql.connection;

        try {
            PreparedStatement lookupStatement =
                connection.prepareStatement(LOOKUP_USER_QUERY);
            lookupStatement.setString(1, user);

            return lookupStatement.executeQuery();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes a user in the database.
     * @throws ApiException If a SQL error occurs or unable to delete.
     * @param sql The database connection.
     * @param user The user to look up.
     */
    public static void deleteUser(SQLConnection sql, String user)
        throws ApiException {

        Connection connection = sql.connection;

        try {
            PreparedStatement deleteStatement =
                connection.prepareStatement(DELETE_USER_QUERY);
            deleteStatement.setString(1, user);

            // Execute and check that deletion was successful.
            if (deleteStatement.executeUpdate() != 1) {
                deleteStatement.close();
                throw new ApiException(ApiStatus.APP_USER_NOT_EXIST);
            }

            deleteStatement.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
