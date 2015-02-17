package com.pinata.service.datatier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.pinata.shared.ApiStatus;
import com.pinata.shared.ApiException;

/**
 * Connection to SQL Server. Manages database USER, PASSWORD, LOGIN, URL,
 * an database installation/initialization.
 * @author Christian Gunderman
 */
public class SQLConnection  {
    /** Delete old database query. */
    private static final String DROP_DB_QUERY = "DROP DATABASE IF EXISTS PinataAPI";
    /** Create database query. */
    private static final String CREATE_DB_QUERY = "CREATE DATABASE PinataAPI";
    /** Use database query. */
    private static final String USE_DB_QUERY = "USE PinataAPI";
    /** Create tomcat user query. */
    private static final String CREATE_USER_QUERY =
        "GRANT ALL PRIVILEGES ON PinataAPI.* TO tomcat@localhost " +
        "IDENTIFIED BY 'a@d$jvd34df'";

    /** Default login information. */
    public static final String LOGIN_URL = "jdbc:mysql://localhost/";
    /** Default username. */
    public static final String LOGIN_USER = "tomcat";
    /** Default password. Should be changed after running install_db.sh script*/
    public static final String LOGIN_PASS = "a@d$jvd34df";
    /** SQL Database connection. */
    public final Connection connection;

    /**
     * Creates a new SQL connection.
     * @param addr The JDBC protocol address of the database.
     * @param username The DB username.
     * @param password The DB password.
     */
    private SQLConnection(String addr,
                          String username,
                          String password) throws ApiException {
        try {
            // Piece of shite work-around java needs for this to work.
            Class.forName("com.mysql.jdbc.Driver");

            // Open connection to the DB.
            this.connection = DriverManager.getConnection(addr,
                                                          username,
                                                          password);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        } catch (ClassNotFoundException ex) {
            throw new ApiException(ApiStatus.INSTALL_ERROR, ex);
        } 
    }

    /**
     * Selects the Application Database as the current one.
     * @throws ApiException Thrown if there is a database error.
     */
    private void selectDatabase() throws ApiException {
        try {
            this.connection.prepareStatement(USE_DB_QUERY).execute();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Closes this SQLConnection to prevent leaks.
     * @throws ApiException Thrown if there is a database error.
     */
    public void close() throws ApiException {
        try {
            this.connection.close();
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }

    /**
     * Connects to the default database.
     * @param user The username to use.
     * @param pass The password to use.
     * @throws ApiException Thrown if an error occurs while selecting the
     * database or connecting.
     * @return The SQLConnection.
     */
    public static SQLConnection connect(String user,
                                        String pass) throws ApiException {
        SQLConnection instance = new SQLConnection(LOGIN_URL,
                                          user,
                                          pass);
        return instance;    
    }

    /**
     * Gets default SQLConnection.
     * @throws ApiException Thrown if unable to connect to the DB
     * or if unable to select the database.
     * @return The SQLConnection.
     */
    public static SQLConnection connectDefault() throws ApiException {

        SQLConnection connection = new SQLConnection(LOGIN_URL,
                                                     LOGIN_USER,
                                                     LOGIN_PASS);
        connection.selectDatabase();

        return connection;
    }

    /**
     * Wipes the ENTIRE server database and reinstalls all tables from
     * scratch. Be careful! This cannot be undone.
     * @param sql The SQLConnection to use. This must be done as a
     * privileged or root user.
     */        
    public static void install(SQLConnection sql) throws ApiException {
        try {
            Connection connection = sql.connection;

            connection.prepareStatement(DROP_DB_QUERY).execute();
            connection.prepareStatement(CREATE_DB_QUERY).execute();
            sql.selectDatabase();
            connection.prepareStatement(CREATE_USER_QUERY).execute();
            
            // Create Tables.
            UsersTable.create(sql);
        } catch (SQLException ex) {
            throw new ApiException(ApiStatus.DATABASE_ERROR, ex);
        }
    }
}
