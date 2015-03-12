package com.pinata.service.objectmodel;

import java.util.UUID;

import com.pinata.service.datatier.SQLConnection;
import com.pinata.service.datatier.UserSessionTable;

import com.pinata.shared.*;

/**
 * Public API for UserSessions. Sessions track login and logout for users
 * of the system.
 * @author Christian Gunderman
 */
public class UserSession {

    /** Pinata session HTTP header. Contains auth info. [user];[id] */
    public static final String HEADER = "Pinata-Session";
    /** The unique session ID. */
    private final UUID sessionId;
    /** The User object of the session owner. */
    private final User sessionUser;

    /**
     * Starts a new session and makes a record of it in the database.
     * @throws ApiException If database error occurs, password don't match,
     * user doesn't exist, etc.
     * @param sql The SQLConnection.
     * @param username The username of the user starting the session.
     * @param password The password of the user startign the session.
     * @return A session object to encapsulate the open session info.
     */
    public static UserSession start(SQLConnection sql,
                                    String username,
                                    String password) throws ApiException {
        // Clean up inputs.
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);
        OMUtil.nullCheck(password);

        // Lookup user.
        // Throws if invalid username.
        User user = User.lookup(sql, username);

        // Check if passwords match.
        if (!OMUtil.sha256(password).equals(user.getPasswordHash())) {
            throw new ApiException(ApiStatus.APP_INVALID_PASSWORD);
        }

        // Generate session UUID.
        UUID sessionId = UUID.randomUUID();

        // Create new session.
        UserSessionTable.insertSession(sql, user.getUid(), sessionId);

        return new UserSession(sessionId, user);
    }

    /**
     * Resumes a currently open session. This should be done each call that
     * requires authentication. This is the preferred method for resume.
     * @param sql The SQLConnection.
     * @param httpSessionHeader The HTTP auth header.
     * @return A session object to encapsulate the open session info.
     */
    public static UserSession resume(SQLConnection sql,
                                     String httpSessionHeader)
        throws ApiException {
        OMUtil.sqlCheck(sql);

        if (httpSessionHeader == null) {
            throw new ApiException(ApiStatus.ACCESS_DENIED);
        }

        String[] sessionParams = httpSessionHeader.split(";");

        if (sessionParams.length != 2) {
            throw new ApiException(ApiStatus.INVALID_SESSION_HEADER);
        }

        try {
            return resume(sql, sessionParams[0], sessionParams[1]);
        } catch (ApiException ex) {

            // Rethrow with more relevant exceptions.
            if (ex.status == ApiStatus.APP_USER_NOT_EXIST) {
                throw new ApiException(ApiStatus.INVALID_SESSION_HEADER);
            }

            throw ex;
        }

    }

    /**
     * Resumes a currently open session.
     * @param sql The SQLConnection.
     * @param username The username of the user.
     * @param sessionId A String serialized UUID.
     * @return A session object to encapsulate the open session info.
     */
    public static UserSession resume(SQLConnection sql,
                                     String username,
                                     String sessionId) throws ApiException {
        OMUtil.nullCheck(sessionId);

        try {
            return resume(sql, username, UUID.fromString(sessionId));
        } catch (IllegalArgumentException ex) {
            // Session ID is not a valid UUID.
            throw new ApiException(ApiStatus.MALFORMED_REQUEST);
        }
    }

    /**
     * Resumes a currently open session.
     * @param sql The SQLConnection.
     * @param username The username of the user.
     * @param sessionId The sessionId UUID.
     * @return A session object to encapsulate the open session info.
     */
    public static UserSession resume(SQLConnection sql,
                                     String username,
                                     UUID sessionId) throws ApiException {
        OMUtil.sqlCheck(sql);
        OMUtil.nullCheck(username);
        OMUtil.nullCheck(sessionId);

        // Lookup user.
        // Throws if invalid username.
        User user = User.lookup(sql, username);

        // Check if a session exists for this user.
        if (!UserSessionTable.sessionExists(sql, user.getUid(), sessionId)) {
            throw new ApiException(ApiStatus.INVALID_SESSION);
        }

        return new UserSession(sessionId, user);
    }

    /**
     * Ends all currently open sessions for the given user.
     * @param sql The SQLConnection.
     * @param username The username of the user.
     */
    public static void endAll(SQLConnection sql, String username) throws ApiException {
        UserSessionTable.deleteAllSessions(sql, username);
    }

    /**
     * Ends all currently open sessions (including this one)
     * for this session's user.
     * @param sql The SQLConnection.
     * @param username The username of the user.
     */
    public void endAll(SQLConnection sql) throws ApiException {
        UserSessionTable.deleteAllSessions(sql, this.sessionUser.getUid());
    }

    /**
     * Ends this session.
     * @param sql The SQLConnection.
     */
    public void end(SQLConnection sql) throws ApiException {
        UserSessionTable.deleteSession(sql,
                                       this.sessionUser.getUid(),
                                       this.sessionId);
    }

    /**
     * Gets this session's unique ID.
     */
    public UUID getSessionId() {
        return this.sessionId;
    }

    /**
     * Get session owner.
     * @return The user that started this session.
     */
    public User getUser() {
        return this.sessionUser;
    }

    /**
     * Converts session to Json response.
     * @param status The system status to place in the response.
     * @return Json serializable response object.
     */
    public UserSessionResponse toResponse(ApiStatus status) {
        return new UserSessionResponse(status,
                                       this.getUser().getUsername(),
                                       this.sessionId);
    }

    /**
     * Creates UserSession object. This constructor is private because
     * UserSession objects should only be created by start() and resume().
     * @param sessionId the unique session id.
     * @param sessionUser The User object of the session owner.
     */
    private UserSession(UUID sessionId, User sessionUser) {
        this.sessionId = sessionId;
        this.sessionUser = sessionUser;
    }
}
