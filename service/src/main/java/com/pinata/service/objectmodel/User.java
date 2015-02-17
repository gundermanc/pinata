package com.pinata.service.objectmodel;

import java.util.Date;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;

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
    /** Username. */
    private String username;
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
     * @param password The user's password.
     * @param birthday The user's birthday.
     * @return A new User object containing the created user.
     */
    public static User create(SQLConnection sql,
                              String username,
                              String password,
                              Date birthday) throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);
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

        // Check for future birthdays.
        Date today = new Date();
        if (!today.after(birthday)) {
            throw new ApiException(ApiStatus.APP_INVALID_BIRTHDAY);
        }

        // Query DB.
        Date joinDate = new Date();
        UsersTable.insertUser(sql, username, OMUtil.sha256(password), joinDate, birthday);

        return new User(username, joinDate, birthday);
    }
    
    /**
     * Gets username.
     * @return Unique username String.
     */
    public String getUsername() {
        return this.username;
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

    /**
     * Creates a new User OM object. Constructor is private because User
     * objects can only be created internally from data tier or calls to create().
     * @param username The user's username.
     * @param password The user's password.
     * @param birthday The user's birthday.
     */
    private User(String username, Date joinDate, Date birthday) {
        this.username = username;
        this.joinDate = joinDate;
        this.birthday = birthday;
    }

}
