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

            // Deleting test entries after each test as to not corrupt database, but also test the real thing
            List<FavoriteResource> favorites = dao.getAllFavorites();

            for (FavoriteResource r : favorites) {
                if (r.getName().contains("Test")) {
                    dao.deleteFavorite(r.getResourceID());
                }
            }
        } catch (SQLException e) {
            System.err.println("tearDown failed: " + e.getMessage());
        }
    }

    @Test
    void getAllFavorites() {

        try {

            // Creating and inserting the first testResource
            FavoriteResource testResource1 = new FavoriteResource(0, "Test Resource 1", "123 Test Address", "123-456-7890", "http://testresource1.com");
            dao.insertFavorite(testResource1);

            // Creating and inserting the second testResource
            FavoriteResource testResource2 = new FavoriteResource(0, "Test Resource 2", "456 Trial Address", "098=765-4321", "http://testresource2.com");
            dao.insertFavorite(testResource2);

            // Perform test by checking that both test resources are present in the results.
            List<FavoriteResource> favorites = dao.getAllFavorites();

            boolean isResource1Found = favorites.stream().anyMatch(resource -> resource.getName().equals("Test Resource 1") &&
                                       resource.getAddress().equals("123 Test Address") && Objects.equals(resource.getPhoneNumber(), "123-456-7890")
                                       && Objects.equals(resource.getWebsite(), "http://testresource1.com"));

            boolean isResource2Found = favorites.stream().anyMatch(resource -> resource.getName().equals("Test Resource 2") &&
                                       resource.getAddress().equals("456 Trial Address") && Objects.equals(resource.getPhoneNumber(), "098=765-4321")
                                       && Objects.equals(resource.getWebsite(), "http://testresource2.com"));

            assertTrue(isResource1Found, "First test resource was not found in getAllFavorites result.");
            assertTrue(isResource2Found, "Second test resource was not found in getAllFavorites result.");

            // Verify that we have at least 2 resources in the list (to ensure list capability works)
            assertTrue(favorites.size() >= 2, "Expected at least 2 resources in the favorites list");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }

    @Test
    void getFavoriteByID() {

        try {

            // Declaring and inserting test resource
            FavoriteResource testResource = new FavoriteResource(0, "Test Resource", "123 Test Address", "123-456-7890", "http://testresource.com");
            dao.insertFavorite(testResource);

            // Retrieving all favorites to find our ID we are looking for
            List<FavoriteResource> allFavorites = dao.getAllFavorites();
            FavoriteResource insertedTestResource = allFavorites.stream().filter(r -> r.getName().equals("Test Resource"))
                                                .findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Perform test and verify retrieved results to check for matching ID
            FavoriteResource foundResource = dao.getFavoriteByID(insertedTestResource.getResourceID());

            assertNotNull(foundResource, "Retrieved resource should not be null");
            assertEquals(insertedTestResource.getResourceID(), foundResource.getResourceID(), "Resource IDs should match");
            assertEquals("Test Resource", foundResource.getName(), "Resource names should match");
            assertEquals("123 Test Address", foundResource.getAddress(), "Resource addresses should match");
            assertEquals("123-456-7890", foundResource.getPhoneNumber(), "Resource phone numbers should match");
            assertEquals("http://testresource.com", foundResource.getWebsite(), "Resource websites should match");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }


    }

    @Test
    void searchFavorites() {

        try {

            // Creating and inserting a resource that SHOULD match the test search
            FavoriteResource matchingResource = new FavoriteResource(0, "Test Clinic Resource", "123 Clinic Lane", "123-456-7890", "http://testclinic.com");
            dao.insertFavorite(matchingResource);

            // Creating and inserting a resource that SHOULD NOT match the test search
            FavoriteResource nonMatchingResource = new FavoriteResource(0, "Test Hospital Resource", "456 Hospital Drive", "098-765-4321", "http://testhospital.com");
            dao.insertFavorite(nonMatchingResource);

            // Searching for proper test resource and ensuring it is found in the search results
            String searchKeyword = "Clinic";
            List<FavoriteResource> searchResults = dao.searchFavorites(searchKeyword);

            boolean isMatchingResourceFound = searchResults.stream().anyMatch(resource -> resource.getName().equals("Test Clinic Resource") &&
                                              resource.getAddress().equals("123 Clinic Lane") && Objects.equals(resource.getPhoneNumber(), "123-456-7890") &&
                                              Objects.equals(resource.getWebsite(), "http://testclinic.com"));

            // Checking that the incorrect test resource is NOT found in the search results
            boolean isNonMatchingResourceFound = searchResults.stream().anyMatch(resource -> resource.getName().equals("Test Hospital Resource") &&
                                                 resource.getAddress().equals("456 Hospital Drive") && Objects.equals(resource.getPhoneNumber(), "098-765-4321") &&
                                                 Objects.equals(resource.getWebsite(), "http://testhospital.com"));

            assertTrue(isMatchingResourceFound, "The resource with 'Clinic' should be found in search results");
            assertFalse(isNonMatchingResourceFound, "The resource without 'Clinic' should NOT be found in search results");

            // Testing that the search is NOT case sensitive
            List<FavoriteResource> lowercaseSearchResults = dao.searchFavorites("clinic");
            boolean isMatchingResourceFoundInLowercaseSearch = lowercaseSearchResults.stream().anyMatch(resource -> resource.getName().equals("Test Clinic Resource"));
            assertTrue(isMatchingResourceFoundInLowercaseSearch, "Search should be case-insensitive");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }

    @Test
    void isResourceAlreadyFavorited() {

        try {

            // Declaring and inserting test resource
            FavoriteResource testResource = new FavoriteResource(0, "Test Resource", "123 Test Address", "123-456-7890", "http://testresource.com");
            dao.insertFavorite(testResource);

            // Checking if the valid favorited test resource is already favorited, should return true
            boolean isAlreadyFavorited = dao.isResourceAlreadyFavorited("Test Resource", "123 Test Address");
            assertTrue(isAlreadyFavorited, "Resource with matching name and address should be detected as already favorited");

            // Checking if the invalid favorited test resource is already favorited, should return false
            boolean nonExistentResource = dao.isResourceAlreadyFavorited("Nonexistent Resource", "000 Nowhere Lane");
            assertFalse(nonExistentResource, "Nonexistent resource should not be detected as already favorited");

            // Checking for cases of partial matches - should return false
            boolean partialMatch = dao.isResourceAlreadyFavorited("Test Resource", "321 Trial Lane");
            assertFalse(partialMatch, "Resource with matching name but different address should not be detected as already favorited");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }

    @Test
    void insertFavorite() {

        try {

            // Declaring and inserting test resource while checking that it was inserted, should return true
            FavoriteResource testResource = new FavoriteResource(0, "Test Resource", "123 Test Address", "123-456-7890", "http://testresource.com");
            boolean insertionSuccess = dao.insertFavorite(testResource);
            assertTrue(insertionSuccess, "The resource should have been inserted successfully");

            // Checking that the resource was actually inserted by calling getAllFavorites
            List<FavoriteResource> favorites = dao.getAllFavorites();
            boolean isResourceInserted = favorites.stream().anyMatch(resource -> resource.getName().equals("Test Resource") &&
                                         resource.getAddress().equals("123 Test Address") && Objects.equals(resource.getPhoneNumber(), "123-456-7890")
                                         && Objects.equals(resource.getWebsite(), "http://testresource.com"));

            assertTrue(isResourceInserted, "The inserted resource should be found in the database");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }

    @Test
    void updateFavorite() {

        try {

            // Declaring and inserting test resource while checking that it was inserted, should return true
            FavoriteResource testResource = new FavoriteResource(0, "Test Resource", "123 Test Address", "123-456-7890", "http://testresource.com");
            boolean insertSuccess = dao.insertFavorite(testResource);
            assertTrue(insertSuccess, "The resource should have been updated successfully");

            // Finding the inserted resource to get its ID
            List<FavoriteResource> favorites = dao.getAllFavorites();
            FavoriteResource insertedResource = favorites.stream().filter(r -> r.getName().equals("Test Resource")).findFirst()
                                                .orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Creating an updated version of the test resource with the same ID
            FavoriteResource updatedResource = new FavoriteResource(insertedResource.getResourceID(), "Test Resource Updated", "456 Update Street"
                                               , "098-765-4321", "http://testresourceupdated.com");

            // Updating resource and checking if it was successful or not, should return true
            boolean updateSuccess = dao.updateFavorite(updatedResource);
            assertTrue(updateSuccess, "The resource should have been updated successfully");

            // Checking that the resource was actually updated by verifying contents via ID
            FavoriteResource retrievedResource = dao.getFavoriteByID(insertedResource.getResourceID());
            assertNotNull(retrievedResource, "Updated resource should be retrievable");

            assertEquals("Test Resource Updated", retrievedResource.getName(), "Name should be updated");
            assertEquals("456 Update Street", retrievedResource.getAddress(), "Address should be updated");
            assertEquals("098-765-4321", retrievedResource.getPhoneNumber(), "Phone number should be updated");
            assertEquals("http://testresourceupdated.com", retrievedResource.getWebsite(), "Website should be updated");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }

    @Test
    void deleteFavorite() {

        try {

            // Declaring and inserting test resource while checking that it was inserted, should return true
            FavoriteResource testResource = new FavoriteResource(0, "Test Resource", "123 Test Address", "123-456-7890", "http://testresource.com");
            boolean insertSuccess = dao.insertFavorite(testResource);
            assertTrue(insertSuccess, "The resource should have been updated successfully");

            // Finding the inserted resource to get its ID
            List<FavoriteResource> favorites = dao.getAllFavorites();
            FavoriteResource insertedResource = favorites.stream().filter(r -> r.getName().equals("Test Resource") && r.getAddress().equals("123 Test Address"))
                                                .findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Checking that the resource exists in database before deletion
            FavoriteResource resourceBeforeDeletion = dao.getFavoriteByID(insertedResource.getResourceID());
            assertNotNull(resourceBeforeDeletion, "Resource should exist before deletion");

            // Deleting test resource
            boolean deletionSuccess = dao.deleteFavorite(insertedResource.getResourceID());
            assertTrue(deletionSuccess, "The resource should have been deleted successfully");

            // Making sure it is no longer in the database
            FavoriteResource resourceAfterDeletion = dao.getFavoriteByID(insertedResource.getResourceID());
            assertNull(resourceAfterDeletion, "Resource should not exist after deletion");

        } catch (SQLException e) {
            fail("An SQL exception occurred: " + e.getMessage());
        }
    }
}