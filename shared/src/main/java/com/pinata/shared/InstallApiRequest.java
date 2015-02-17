package com.pinata.shared;

import flexjson.JSON;

/**
 * JSON Request for installing API. Requires the root
 * username and password so server can log in as root
 * to create and delete tables.
 * @author Christian Gunderman
 */
public class InstallApiRequest extends ApiRequest {

    /** Root username. */
    @JSON(include=true, name="root_user")
    public String rootUser;

    /** Root password. */
    @JSON(include=true, name="root_pass")
    public String rootPass;
}
