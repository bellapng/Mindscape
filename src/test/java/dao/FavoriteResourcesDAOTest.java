package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.*;
import models.FavoriteResource;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FavoriteResourcesDAO.java.
 * @author Isabella Castillo
 */
class FavoriteResourcesDAOTest {

    private FavoriteResourcesDAO dao;

    @BeforeEach
    void setUp() {

        dao = new FavoriteResourcesDAO();
    }

    @AfterEach
    void tearDown() {

        try {

            // Deleting test entries after each test to not corrupt database.
            List<FavoriteResource> favorites = dao.getAllFavorites();

            for (FavoriteResource r : favorites) {
                if (r.getName().contains("Test") || r.getName().contains("To Be") || r.getName().contains("Unique") || r.getName().contains("Old") || r.getName().contains("Find Me") || r.getName().contains("Library")) {
                    dao.deleteFavorite(r.getResourceID());
                }
            }
        } catch (SQLException e) {
            System.err.println("tearDown failed: " + e.getMessage());
        }
    }

    @Test
    void getAllFavorites() {
    }

    @Test
    void getFavoriteByID() {
    }

    @Test
    void searchFavorites() {
    }

    @Test
    void isResourceAlreadyFavorited() {
    }

    @Test
    void insertFavorite() {
    }

    @Test
    void updateFavorite() {
    }

    @Test
    void deleteFavorite() {
    }
}