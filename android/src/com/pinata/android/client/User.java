package com.pinata.android.client;

import java.util.Date;

import com.pinata.shared.*;
import com.pinata.android.client.http.*;

/**
 * Android Client side User object.
 * @author Christian Gunderman
 */
public class User {
    /** Username. */
    private String username;
    /** User's gender. */
    private String gender;
    /** Date user joined. */
    private Date joinDate;
    /** User's birthday. */
    private Date birthday;
    /** User's email address*/
    private String email;

    /**
     * Attempts to make a REST call to create a new user on the server.
     * @throws ClientException If a parameter is null or not within the accepted
     * set of values or a database or communication error occurs.
     * @param sql The connection to the database.
     * @param username The user's username.
     * @param gender The user's MALE or FEMALE gender String.
     * @param password The user's password.
     * @param birthday The user's birthday.
     * @return A new User object containing the created user.
     */
    public static User create(HttpClient client,
                              String username,
                              String password,
                              Gender gender,
                              Date birthday,
                              String email) throws ClientException {
        // Create JSON request object.
        CreateUserRequest request = new CreateUserRequest(username,
                                                          password,
                                                          gender.name(),
                                                          birthday,
                                                          email);
        // Send request.
        UserResponse response
            = UsersClient.doCreateUserRequest(client, request);

        return new User(response.user, response.gender,
                        response.joinDate, response.birthday,
                        response.email);
    }

    /**
     * Creates a new User OM object. Constructor is private because User
     * objects can only be created internally from REST calls.
     * @param username The user's username.
     * @param gender The user's gender, MALE or FEMALE.
     * @param password The user's password.
     * @param birthday The user's birthday.
     */
    private User(String username, String gender, Date joinDate, Date birthday, String email) {
        this.username = username;
        this.gender = gender;
        this.joinDate = joinDate;
        this.birthday = birthday;
        this.email = email;
    }

    /**
     * User's Gender ENUM
     */
    public enum Gender {
        MALE,
        FEMALE
    }
}
