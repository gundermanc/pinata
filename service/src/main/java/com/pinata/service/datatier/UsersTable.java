package com.pinata.service.datatier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

import java.util.Date;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

/**
 * Wrapper class for SQL Users Table. Ideally there should be a 1:1 mapping
 * between SQL queries and methods in this file. Methods in this file, although
 * public, need maintain only basic database integrity. Exhaustive error
 * checking is done in User.java in objectmodel.
 * @author Christian Gunderman
 */
public class UsersTable {

    /** Creates this table. */
    private static final String CREATE_TABLE_QUERY =
        "CREATE TABLE Users(" +
        "  user VARCHAR(25) NOT NULL," +
        "  pass VARCHAR(64) NOT NULL," +
        "  gender VARCHAR(6) NOT NULL," +
        "  join_date DATETIME NOT NULL," +
        "  birth_date DATETIME NOT NULL," +
        "  PRIMARY KEY(user)," +
        "  INDEX USING HASH(user)" +
        ")";

    /** Create user query. */
    private static final String INSERT_USER_QUERY =
        "INSERT INTO Users (user, pass, gender, join_date, birth_date)" +
        " VALUES (?,?,?,?,?)";

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
     */
    public static void insertUser(SQLConnection sql,
                                  String user,
                                  String pass,
                                  String gender,
                                  Date joinDate,
                                  Date birthDate) throws ApiException {
        Connection connection = sql.connection;

        try {
            PreparedStatement insertStatement
                = connection.prepareStatement(INSERT_USER_QUERY);
            insertStatement.setString(1, user);
            insertStatement.setString(2, pass);
            insertStatement.setString(3, gender);
            insertStatement.setDate(4, new java.sql.Date(joinDate.getTime()));
            insertStatement.setDate(5, new java.sql.Date(birthDate.getTime()));

            insertStatement.execute();
        } catch (SQLIntegrityConstraintViolationException ex) {
            // We have no foreign or unique keys other than primary
            // so this can only be thrown for duplicate users.
            throw new ApiException(ApiStatus.APP_USERNAME_TAKEN, ex);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
