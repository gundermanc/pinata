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
    /** User's first name. */
    public String firstName;
    /** User's last name. */
    public String lastName;
    /** User's gender. */
    private Gender gender;
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
     * @param password The user's password.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param gender The user's MALE or FEMALE gender String.
     * @param birthday The user's birthday.
     * @return A new User object containing the created user.
     */
    public static User create(HttpClient client,
                              String username,
                              String password,
                              String firstName,
                              String lastName,
                              Gender gender,
                              Date birthday,
                              String email) throws ClientException {

        // Create JSON request object.
        CreateUserRequest request = new CreateUserRequest(username,
                                                          password,
                                                          firstName,
                                                          lastName,
                                                          gender.name(),
                                                          birthday,
                                                          email);
        // Send request.
        UserResponse response
            = UsersClient.doCreateUserRequest(client, request);

        try {
            return new User(response.user, response.firstName, response.lastName,
                            Gender.valueOf(response.gender),
                            response.joinDate, response.birthday,
                            response.email);
        } catch (IllegalArgumentException ex) {
            throw new ClientException(ClientStatus.HTTP_MALFORMED_RESPONSE);
        }
    }

    /**
     * Deletes the given user. NOTE: you must be logged in as that user
     * or posess ROLE_ADMIN rights to be able to delete this user.
     * @throws ClientException If something goes wrong locally. If server
     * reports an error, this client exception will contain an ApiException
     * with the details. Fails with ACCESS_DENIED if you try to delete a user
     * other than yourself.
     * @param client The HttpClient.
     * @param username The user to delete.
     */
    public static void delete(HttpClient client,
                              String username) throws ClientException {
        UsersClient.doDeleteUserRequest(client, username);
    }

    /**
     * Deletes this user. NOTE: you must be logged in as this user
     * or posess ROLE_ADMIN rights to be able to delete this user.
     * @throws ClientException If something goes wrong locally. If server
     * reports an error, this client exception will contain an ApiException
     * with the details. Fails with ACCESS_DENIED if you try to delete a user
     * other than yourself.
     * @param client The HttpClient.
     */
    public void delete(HttpClient client) throws ClientException {
        delete(client, this.getUsername());
    }

    /**
     * Get the user's unique username.
     * @return The user's username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the user's first name.
     * @return The user's first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Gets the user's last name.
     * @return The user's last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Get's the user's gender.
     * @return Gender of the user.
     */
    public Gender getGender() {
        return this.gender;
    }

    /**
     * Get the date that the user joined the network.
     * @return The join date.
     */
    public Date getJoinDate() {
        return this.joinDate;
    }

    /**
     * Get user's birthday.
     * @return Birthday.
     */
    public Date getBirthday() {
        return this.birthday;
    }

    /**
     * Get the user's email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Creates a new User OM object. Constructor is private because User
     * objects can only be created internally from REST calls.
     * @param username The user's username.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param gender The user's gender.
     * @param password The user's password.
     * @param birthday The user's birthday.
     * @param email The user's email.
     */
    private User(String username, String firstName, String lastName,
                 Gender gender, Date joinDate, Date birthday, String email) {
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
