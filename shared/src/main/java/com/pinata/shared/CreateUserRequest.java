package com.pinata.shared;

import java.util.Date;

import flexjson.JSON;

/**
 * JSON Request for Creating a user.
 * @author Christian Gunderman
 */
public class CreateUserRequest extends ApiRequest {

    /** New user's username. */
    @JSON(include=true, name="user")
    public String user;

    /** New user's password. */
    @JSON(include=true, name="pass")
    public String pass;

    /** New user's gender." */
    @JSON(include=true, name="gender")
    public String gender;

    /** New user's birthday. */
    @JSON(include=true, name="birthday")
    public Date birthday;

    /**
     * Creates a new CreateUserRequest with the provided field values.
     * @param user User name of the new user.
     * @param pass The new user's password.
     * @param gender Either MALE or FEMALE.
     * @param birthday The new user's birthday.
     */
    public CreateUserRequest(String user,
                             String pass,
                             String gender,
                             Date birthday) {
        this.user = user;
        this.pass = pass;
        this.gender = gender;
        this.birthday = birthday;
    }

    /**
     * Creates a CreateUserRequest object with all null fields.
     */
    public CreateUserRequest() { }
}
