package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test if the connection to the database is successful or not.
 * Please ensure that the mindscape.sqlite file in src/main/java/database is listed as the DATABASE_URL
 * @author Isabella Castillo
 */
class DatabaseConnectionTest {

    @Test
    void connect() {

        try (Connection conn = DatabaseConnection.connect()) {

            assertNotNull(conn, "Connection should not be null.");
            assertTrue(conn.isValid(2), "Connection should be valid.");
        } catch (SQLException e) {

            fail("SQLException occurred: " + e.getMessage());
        }
    }
}