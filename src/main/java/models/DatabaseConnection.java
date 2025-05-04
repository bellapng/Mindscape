package models;

import java.sql.*;

/**
 * Class to establish a method to connect to local SQLite database hosted on user's system.
 * @author Isabella Castillo
 */
public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:src/main/java/database/mindscape.sqlite";

    // To prevent accidental object instantiation
    private DatabaseConnection() {}

    /**
     * Establishes a connection to local, user-hosted SQLite database.
     * Can be used like: Connection conn = DatabaseConnection.connect();
     * This method should not be instantiated (ie, using keyword 'new').
     *
     * @return Connection   Returns a connection to the local database.
     * @throws SQLException If an error occurs.
     */
    public static Connection connect() throws SQLException {

        return DriverManager.getConnection(DATABASE_URL);
    }
}