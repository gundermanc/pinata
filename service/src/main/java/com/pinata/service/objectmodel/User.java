package com.pinata.service.objectmodel;

import java.util.Date;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.UserResponse;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.datatier.UsersTable;

/**
 * User API, used for all operations pertaining to a user and he or her
 * relations.
 * @author Christian Gunderman
 */
public class User {

    /** Minimum user account length. */
    private static int USER_MIN = 6;
    /**
     * Maximum user account length. You MUST make sure that this value is less
     * than or equal to the column width in UserTable.
     */
    private static int USER_MAX = 25;
    /** Password minimum length */
    private static int PASS_MIN = 6;
    /** Maximum password length. */
    private static int PASS_MAX = 100;
    /** MALE user id. */
    private static String GENDER_MALE = "MALE";
    /** FEMALE user id. */
    private static String GENDER_FEMALE = "FEMALE";
    /** Username. */
    private String username;
    /** User's gender. */
    private String gender;
    /** Date user joined. */
    private Date joinDate;
    /** User's birthday. */
    private Date birthday;

    /**
     * Creates a new user and stores in data tier.
     * @throws ApiException If a parameter is null or not within the accepted
     * set of values or a database error occurs.
     * @param sql The connection to the database.
     * @param username The user's username.
     * @param gender The user's MALE or FEMALE gender String.
     * @param password The user's password.
     * @param birthday The user's birthday.
     * @return A new User object containing the created user.
     */
    public static User create(SQLConnection sql,
                              String username,
                              String password,
                              String gender,
                              Date birthday) throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);
        OMUtil.nullCheck(gender);
        OMUtil.nullCheck(password);
        OMUtil.nullCheck(birthday);

        // Check username length.
        if (username.length() > USER_MAX || username.length() < USER_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_USER_LENGTH);
        }

        // Check password length.
        if (password.length() > PASS_MAX || password.length() < PASS_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_PASS_LENGTH);
        }

        // Check gender for proper values.
        gender = gender.toUpperCase();
        if (!gender.equals(GENDER_MALE) && !gender.equals(GENDER_FEMALE)) {
            throw new ApiException(ApiStatus.INVALID_GENDER);
        }

        // Check for future birthdays.
        Date today = new Date();
        if (!today.after(birthday)) {
            throw new ApiException(ApiStatus.APP_INVALID_BIRTHDAY);
        }

        // Query DB.
        Date joinDate = new Date();
        UsersTable.insertUser(sql, username, OMUtil.sha256(password),
                              gender, joinDate, birthday);

        return new User(username, gender, joinDate, birthday);
    }

    /**
     * Looks up a user in the datatier and returns it as a User object.
     * @throws ApiException With APP_USER_NOT_EXIST if user doesn't exist,
     * or another code if SQL error occurs.
     * @param sql The SQL connection.
     * @param username The username of the user to look up.
     * @return A new user object for the user.
     */
    public static User lookup(SQLConnection sql, String username)
        throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);

        ResultSet result = UsersTable.lookupUser(sql, username);

        // Build User object.
        try {
            // Get the first (and only) row or throw if user not exist.
            if (!result.next()) {
                throw new ApiException(ApiStatus.APP_USER_NOT_EXIST);
            }
            
            return new User(result.getString("user"),
                            result.getString("gender"),
                            result.getDate("join_date"),
                            result.getDate("birth_date"));
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
    
    /**
     * Gets username.
     * @return Unique username String.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets user's gender.
     * @return User's gender as a String, MALE or FEMALE.
     */
    public String getGender() {
        return this.gender;
    }

    /**
     * Gets date user joined the system.
     * @return Joined date.
     */
    public Date getJoinDate() {
        return this.joinDate;
    }

    /**
     * Gets the user's birthday.
     * @return The day the user was born.
     */
    public Date getBirthday() {
        return this.birthday;
    }

    public UserResponse toResponse(ApiStatus status) {
        return new UserResponse(status,
                                this.getUsername(),
                                this.getGender(),
                                this.getJoinDate(),
                                this.getBirthday());
    }

    /**
     * Creates a new User OM object. Constructor is private because User
     * objects can only be created internally from data tier or calls to create().
     * @param username The user's username.
     * @param gender The user's gender, MALE or FEMALE.
     * @param password The user's password.
     * @param birthday The user's birthday.
     */
    private User(String username, String gender, Date joinDate, Date birthday) {
        this.username = username;
        this.gender = gender;
        this.joinDate = joinDate;
        this.birthday = birthday;
    }

}
