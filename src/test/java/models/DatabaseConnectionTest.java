package models;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

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