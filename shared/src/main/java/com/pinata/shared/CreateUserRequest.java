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

    /** New user's birthday. */
    @JSON(include=true, name="birthday")
    public Date birthday;
}
