package com.pinata.shared;

import java.util.Date;

import flexjson.JSON;

/**
 * CreateUser JSON Response from server to client.
 * @author Christian Gunderman
 */
public class CreateUserResponse extends ApiResponse {

    /** New user username. */
    @JSON(include=true, name="user")
    public String user;

    /** Password, always null. We NEVER pass it back. */
    @JSON(include=true, name="pass")
    public String pass = null;

    /** Gender of new user. */
    @JSON(include=true, name="gender")
    public String gender;

    /** Date new user joined. Determined serverside and passed back. */
    @JSON(include=true, name="join_date")
    public Date joinDate;

    /** New user's birthday. */
    @JSON(include=true, name="birthday")
    public Date birthday;

    /**
     * Creates a new CreateUserResponse object.
     * @param status The status of the operation.
     * @param user The username of the new user.
     * @param gender MALE or FEMALE.
     * @param joinDate The date the new user joined.
     * @param birthday The birthday of the new user.
     */
    public CreateUserResponse(ApiStatus status,
                              String user,
                              String gender,
                              Date joinDate,
                              Date birthday) {
        super(status);
        this.user = user;
        this.pass = null;
        this.gender = gender;
        this.joinDate = joinDate;
        this.birthday = birthday;
    }
}
