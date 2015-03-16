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

    /** New user's first name. */
    @JSON(include=true, name="first_name")
    public String firstName;

    /** New user's last name. */
    @JSON(include=true, name="last_name")
    public String lastName;

    /** New user's gender." */
    @JSON(include=true, name="gender")
    public String gender;

    /** New user's birthday. */
    @JSON(include=true, name="birthday")
    public Date birthday;

    /** New user's email address. */
    @JSON(include=true, name="email")
    public String email;


    /**
     * Creates a new CreateUserRequest with the provided field values.
     * @param user User name of the new user.
     * @param pass The new user's password.
     * @param firstName The new user's first name.
     * @param lastName The new user's last name.
     * @param gender Either MALE or FEMALE.
     * @param birthday The new user's birthday.
     * @param email The user's email address.
     */
    public CreateUserRequest(String user,
                             String pass,
                             String firstName,
                             String lastName,
                             String gender,
                             Date birthday,
                             String email) {
        this.user = user;
        this.pass = pass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.birthday = birthday;
        this.email = email;
    }

    /**
     * Creates a CreateUserRequest object with all null fields.
     */
    public CreateUserRequest() { }
}
