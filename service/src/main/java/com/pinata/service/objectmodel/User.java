package com.pinata.service.objectmodel;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.pinata.shared.ApiException;
import com.pinata.shared.ApiStatus;
import com.pinata.shared.UserResponse;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.datatier.UserTable;
import com.pinata.service.datatier.UserRoleTable;
import com.pinata.service.datatier.UserHasRoleTable;

import javax.mail.internet.*;

/**
 * User API, used for all operations pertaining to a user and his or her
 * relations.
 * @author Christian Gunderman
 */
public class User {

    /** Minimum user account length. */
    private static final int USER_MIN = 6;
    /**
     * Maximum user account length. You MUST make sure that this value is less
     * than or equal to the column width in UserTable.
     */
    private static final int USER_MAX = 25;
    /** Password minimum length */
    private static final int PASS_MIN = 6;
    /** Maximum password length. */
    private static final int PASS_MAX = 100;
    /** First name minimum length. */
    private static final int FIRST_NAME_MIN = 2;
    /** First name maximum length. */
    private static final int FIRST_NAME_MAX = 25;
    /** Last name minimum length. */
    private static final int LAST_NAME_MIN = 2;
    /** Last name maximum length. */
    private static final int LAST_NAME_MAX = 25;
    /** Maximum email length. */
    private static final int EMAIL_MAX = 320;
    /** MALE user id. */
    private static final String GENDER_MALE = "MALE";
    /** FEMALE user id. */
    private static final String GENDER_FEMALE = "FEMALE";


    /** Unique User id integer. */
    private int uid;
    /** Username. */
    private String username;
    /** Password hashes. */
    private String passwordHash;
    /** First name. */
    private String firstName;
    /** Last name. */
    private String lastName;
    /** User's gender. */
    private String gender;
    /** Date user joined. */
    private Date joinDate;
    /** User's birthday. */
    private Date birthday;
    /** User's email address. */
    private String email;
    /** User's security roles. */
    private Set<Role> roles;

    /**
     * Creates a new user and stores in data tier.
     * @throws ApiException If a parameter is null or not within the accepted
     * set of values or a database error occurs.
     * @param sql The connection to the database.
     * @param username The user's username.
     * @param password The user's password.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param gender The user's MALE or FEMALE gender String.
     * @param birthday The user's birthday.
     * @param email The user's email address.
     * @return A new User object containing the created user.
     */
    public static User create(SQLConnection sql,
                              String username,
                              String password,
                              String firstName,
                              String lastName,
                              String gender,
                              Date birthday,
                              String emailStr) throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);
        OMUtil.nullCheck(password);
        OMUtil.nullCheck(firstName);
        OMUtil.nullCheck(lastName);
        OMUtil.nullCheck(gender);
        OMUtil.nullCheck(birthday);
        OMUtil.nullCheck(emailStr);

        // Check username length.
        username = username.toLowerCase();
        if (username.length() > USER_MAX || username.length() < USER_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_USER_LENGTH);
        }
        // Check username characters.
        if(OMUtil.invalidChars(username)){
            throw new ApiException(ApiStatus.APP_INVALID_USERNAME);
        }

        // Check password length.
        if (password.length() > PASS_MAX || password.length() < PASS_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_PASS_LENGTH);
        }
        // Check for spaces
        if(password.contains(" ")){
            throw new ApiException(ApiStatus.APP_INVALID_PASSWORD);
        }

        // Check first name length.
        if (firstName.length() > FIRST_NAME_MAX ||
            firstName.length() < FIRST_NAME_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_NAME);
        }

        // Check last name length.
        if (lastName.length() > LAST_NAME_MAX ||
            lastName.length() < LAST_NAME_MIN) {
            throw new ApiException(ApiStatus.APP_INVALID_NAME);
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

        // Check for valid email and convert to internet address.
        if(emailStr.length() > EMAIL_MAX) {
            throw new ApiException(ApiStatus.APP_INVALID_EMAIL);
        }
        InternetAddress emailAddr = null;
        try{
            emailAddr = new InternetAddress(emailStr, /*Strict:*/ true);
            emailAddr.validate();
        } catch(AddressException e){
            throw new ApiException(ApiStatus.APP_INVALID_EMAIL);
        }

        // Hash password.
        password = OMUtil.sha256(password);

        // Insert user query.
        Date joinDate = new Date();
        int uid = UserTable.insertUser(sql, username, password,
                                       firstName, lastName, gender, joinDate,
                                       birthday, emailAddr);

        // User role query.
        // NOTE: if you remove this line of code, you will break lookup which
        // assumes every user has at least one role.
        UserHasRoleTable.insertUserHasRole(sql,
                                           uid,
                                           UserRoleTable.ROLE_USER_ID);
        
        return new User(uid, username, password, firstName, lastName, gender,
                        joinDate, birthday, emailStr);
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

        ResultSet result = UserTable.lookupUserWithRoles(sql, username);

        // Build User object.
        try {
            // Get the first (and only) row or throw if user not exist.
            if (!result.next()) {
                throw new ApiException(ApiStatus.APP_USER_NOT_EXIST);
            }
            
            User user = new User(result.getInt("uid"),
                                 result.getString("user"),
                                 result.getString("pass"),
                                 result.getString("first_name"),
                                 result.getString("last_name"),
                                 result.getString("gender"),
                                 result.getDate("join_date"),
                                 result.getDate("birth_date"),
                                 result.getString("email"));

            // Populate user roles.
            // NOTE: this implementation assumes that every user is AT LEAST
            // one role.
            do {
                user.roles.add(new Role(result.getString("role"),
                                        result.getString("description")));
            } while(result.next());

            result.close();
            return user;
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Deletes a user.
     * @throws ApiException If a database error occurs.
     * @param sql The SQL connection.
     * @param username The username of the user to delete.
     */
    public static void delete(SQLConnection sql, String username)
        throws ApiException {

        // Null check everything:
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);

        // Try to delete
        UserTable.deleteUser(sql, username);
    }

    /**
     * Deletes this user.
     * @throws ApiException If a database error occurs.
     * @param sql The SQL connection.
     */
    public void delete(SQLConnection sql) throws ApiException {
        User.delete(sql, this.getUsername());
    }
    
    /**
     * Gets username.
     * @return Unique username String.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets this user's first name.
     * @return First name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Gets this user's last name.
     * @return Last name.
     */
    public String getLastName() {
        return this.lastName;
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

    /**
     * Gets the user's email address.
     * @return The user's email address.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Gets the user's password hash. This method is intentionally
     * package protected to keep password hashes from leaving the objectmodel.
     * @return The SHA256 hashed user password.
     */
    String getPasswordHash() {
        return this.passwordHash;
    }

    /**
     * Gets the user's table UID. This method is intentionally package
     * protected.
     * @return The user's AUTO_INCREMENT id.
     */
    int getUid() {
        return this.uid;
    }

    /**
     * Checks if two User objects refer to the same User account.
     * @param o The object to compare.
     * @return True if they are the same user.
     */
    @Override
    public boolean equals(Object o) {
        return ((User)o).getUid() == this.getUid();
    }

    /**
     * Reformats the User as a JsonSerializable UserResponse
     * object.
     * @param status The ApiStatus representing the success/failure.
     * @return JsonSerializable Object.
     */
    public UserResponse toResponse(ApiStatus status) {
        return new UserResponse(status,
                                this.getUsername(),
                                this.getFirstName(),
                                this.getLastName(),
                                this.getGender(),
                                this.getJoinDate(),
                                this.getBirthday(),
                                this.getEmail());
    }

    /**
     * Gets the security roles that this user is authorized for.
     * @return An unmodifiable collection of Roles.
     */
    public Collection<Role> getRoles() {
        return Collections.unmodifiableCollection(this.roles);
    }

    /**
     * Checks to see if this user is of the specified Role.
     * @return True if this user is the specified role.
     */
    public boolean isRole(String role) {
        return this.roles.contains(new Role(role, null));
    }

    /**
     * Grants this user the specified role.
     * @throws ApiException If database error occurs or Role doesn't exist
     * or User already has given Role.
     * @param sql The SQLConnection.
     * @param role A unique role id string.
     * @return The Role object that was added to the User's Roles.
     */
    public Role addRole(SQLConnection sql, String role) throws ApiException {
        // Clean inputs.
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(role);

        try {
            // Add Role to the user in the DB.
            UserHasRoleTable.insertUserHasRole(sql, this.uid, role);

            // Query Role from DB to get the description.
            ResultSet roleResult = UserRoleTable.lookupUserRole(sql,
                                                                role);

            // Check that results were returned.
            // This should be redundant, but good to have just in case.
            if (!roleResult.next()) {
                throw new ApiException(ApiStatus.APP_USER_HAS_ROLE_DUPLICATE);
            }

            // Create a wrapper object.
            Role newRole = new Role(roleResult.getString("role"),
                                    roleResult.getString("description"));

            // Add wrapper object to Roles set for user.
            this.roles.add(newRole);

            return newRole;
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Removes a user's security Role.
     * @throws ApiException If user doesn't have the requested Role.
     * @param role The unique ROLE_[name] id.
     */
    public void removeRole(SQLConnection sql, String role) throws ApiException {

        // Clean inputs.
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(role);

        // Remove Role from the Role set.
        Role roleToRemove = new Role(role, null);

        // Check if we have this Role first.
        if (!this.roles.contains(roleToRemove)) {
            throw new ApiException(ApiStatus.APP_USER_NOT_HAVE_ROLE);
        }

        // Remove Role from user in database.
        UserHasRoleTable.deleteUserHasRole(sql, this.uid, role);
    }

    /**
     * A Security Role. 
     * Get a user's Roles with getRoles() or isRole().
     * @author Christian Gunderman
     */
    public static class Role {
        /** Minimum length for a Role id. */
        private static int  ROLE_MIN = 7;
        /** Maximum length for a Role id. */
        private static int ROLE_MAX = 25;
        /** Beginning of all ROLE ids. */
        private static String ROLE_PREFIX = "ROLE_";
        /** The unique role id. */
        public final String id;
        /** The role description message. */
        public final String description;

        /**
         * Creates a new Role and stores in the database.
         * @throws ApiException If database error occurs or a role with
         * the given id already exists.
         * @param role The unique role id string ROLE_[name] with [name] at
         * least 2 characters, no more than 20.
         * @param description The 255 character description of the role.
         * @return The created Role.
         */
        public static Role create(SQLConnection sql,
                                  String role,
                                  String description) throws ApiException {

            // Clean inputs.
            OMUtil.sqlCheck(sql);
            OMUtil.nullCheck(role);
            OMUtil.nullCheck(description);

            // Check Role string length.
            if (role.length() < ROLE_MIN || role.length() > ROLE_MAX) {
                throw new ApiException(ApiStatus.APP_INVALID_ROLE_LENGTH);
            }

            // Check Role prefix.
            if (!role.startsWith(ROLE_PREFIX)) {
                throw new ApiException(ApiStatus.APP_INVALID_ROLE);
            }

            // Create Role in database.
            UserRoleTable.insertUserRole(sql, role, description);

            return new Role(role, description);
        }

        /**
         * Deletes the Role identified by the given Role id.
         * @throws ApiException If database error or the Role doesn't exist.
         * @param sql The connection to the SQL database.
         * @param Unique Role id ROLE_[name].
         */
        public static void delete(SQLConnection sql,
                                  String role) throws ApiException {
            // Clean inputs.
            OMUtil.sqlCheck(sql);
            OMUtil.nullCheck(role);

            UserRoleTable.deleteUserRole(sql, role);
        }

        /**
         * Checks to see if this role is equalivalent to another based upon
         * their id.
         * @return True if the same role, false otherwise.
         */
        @Override
        public boolean equals(Object o) {
            return this.id.equals(((Role)o).id);
        }

        /**
         * Hashes the Role based upon its id.
         * @return A semi-unique hash integer.
         */
        @Override
        public int hashCode() {
            return this.id.hashCode();
        }

        /**
         * Creates a new Role object. Only used inside of User class. No public
         * construction.
         * @param id The unique role id.
         * @param description The role description string.
         */
        private Role(String id, String description) {
            this.id = id;
            this.description = description;
        }
    }

    /**
     * Creates a new User OM object. Constructor is private because User
     * objects can only be created internally from data tier or calls to create().
     * @param username The user's username.
     * @param password The user's password.
     * @param gender The user's gender, MALE or FEMALE.
     * @param firstName The user's first name.
     * @param lastName The user's last name.
     * @param birthday The user's birthday.
     */
    private User(int uid, String username, String passwordHash,
                 String firstName, String lastName,
                 String gender, Date joinDate,
                 Date birthday, String email) {
        this.uid = uid;
        this.username = username;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.joinDate = joinDate;
        this.birthday = birthday;
        this.email = email;
        this.roles = new HashSet<Role>();
    }
}
