package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import models.JournalEntry;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for JournalDAO.java.
 * Each test matches the name of the method in the JournalDAO class and has descriptive comments.
 * @author Isabella Castillo
 */
class JournalDAOTest {


    private JournalDAO dao;


    @BeforeEach
    void setUp() {

        dao = new JournalDAO();
    }


    @AfterEach
    void tearDown() {

        try {

            // Deleting test entries after each test as to not corrupt database, but also test the real thing
            List<JournalEntry> entries = dao.getAllJournalEntries();

            for (JournalEntry entry : entries) {

                dao.deleteJournalEntry(entry.getJournalID());
            }
        } catch (SQLException e) {
            System.err.println("tearDown failed: " + e.getMessage());
        }
    }


    @Test
    void getAllJournalEntries() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Creating and entering 2 mock journal entries to ensure the list is returned
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is a test.", currTime);
            dao.insertJournalEntry(entry1);

            JournalEntry entry2 = new JournalEntry(0, "Test Entry 2", "This is also a test.", currTime);
            dao.insertJournalEntry(entry2);

            // Perform test by checking that all entries are present in the results
            List<JournalEntry> results = dao.getAllJournalEntries();

            boolean isEntry1Found = results.stream().anyMatch(entry -> entry.getTitle().equals("Test Entry 1"));
            boolean isEntry2Found = results.stream().anyMatch(entry -> entry.getTitle().equals("Test Entry 2"));

            assertTrue(isEntry1Found, "First test entry was not found in getAllJournalEntries result.");
            assertTrue(isEntry2Found, "Second test entry was not found in getAllJournalEntries result.");

        } catch (SQLException e) {
            fail("SQL error in getAllJournalEntries: " + e.getMessage());
        }
    }


    @Test
    void getJournalEntryByID() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is a test.", currTime);
            dao.insertJournalEntry(entry1);

            // Retrieving all entries to find our ID we are looking for
            List<JournalEntry> entries = dao.getAllJournalEntries();
            JournalEntry inserted = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Inserted exercise entry not found."));

            // Perform test and verify retrieved results to check for matching ID (minus the time portion since milliseconds can cause inequality within tests)
            JournalEntry retrieved = dao.getJournalEntryByID(inserted.getJournalID());
            assertNotNull(retrieved, "Journal entry with ID " + inserted.getJournalID() + " should exist");

            assertEquals(inserted.getJournalID(), retrieved.getJournalID(), "Journal ID should be the same");
            assertEquals(inserted.getTitle(), retrieved.getTitle(), "Title should be the same");
            assertEquals(inserted.getTextEntry(), retrieved.getTextEntry(), "Text entry should be the same");

        } catch (SQLException e) {
            fail("SQL error in getJournalEntryByID: " + e.getMessage());
        }
    }


    @Test
    void searchJournalEntries() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Creating and inserting a entry that SHOULD match the test search
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is the first test.", currTime);
            dao.insertJournalEntry(entry1);

            // Creating and inserting a entry that SHOULD NOT match the test search
            JournalEntry entry2 = new JournalEntry(0, "Test Entry 2", "This is the second test.", currTime);
            dao.insertJournalEntry(entry2);

            // Searching for proper test resource and ensuring it is found in the search results
            String searchKeyword = "first";
            List<JournalEntry> searchResults = dao.searchJournalEntries(searchKeyword);

            // Checking that the correct entry is found in the search results
            boolean isMatchingEntryFound = searchResults.stream().anyMatch(entry -> entry.getTitle().equals("Test Entry 1") && entry.getTextEntry().equals("This is the first test."));

            // Verifying that the incorrect entry is NOT found in the search results
            boolean isNonMatchingEntryFound = searchResults.stream().anyMatch(entry -> entry.getTitle().equals("Test Entry 2") && entry.getTextEntry().equals("This is the second test."));

            assertTrue(isMatchingEntryFound, "The entry containing the keyword 'first' should be found in search results");
            assertFalse(isNonMatchingEntryFound, "The entry not containing the keyword 'first' should NOT be found in the search results");

            // Testing that the search is NOT case sensitive
            List<JournalEntry> uppercaseSearchResults = dao.searchJournalEntries("FIRST");
            boolean isMatchingResourceFoundInUpperSearch = uppercaseSearchResults.stream().anyMatch(entry -> entry.getTitle().equals("Test Entry 1") && entry.getTextEntry().equals("This is the first test."));
            assertTrue(isMatchingResourceFoundInUpperSearch, "Search should be case-insensitive");

        } catch (SQLException e) {
            fail("SQL error in searchJournalEntries: " + e.getMessage());
        }
    }


    @Test
    void getJournalEntriesByDateRange() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Creating and inserting a journal entry that SHOULD be within the test range
            JournalEntry entry1 = new JournalEntry(0, "Journal Entry 1", "This is a journal entry within range.", currTime.minusHours(1));
            dao.insertJournalEntry(entry1);

            // Creating and inserting a journal entry that SHOULD NOT be within the test range
            JournalEntry entry2 = new JournalEntry(0, "Journal Entry 2", "This is a journal entry outside range.", currTime.minusDays(3));
            dao.insertJournalEntry(entry2);

            // Defining the date range that will match the first journal entry but not the second
            LocalDateTime rangeStart = currTime.minusHours(2);
            LocalDateTime rangeEnd = currTime;

            // Retrieving entries that fall within the specified date range
            List<JournalEntry> searchResults = dao.getJournalEntriesByDateRange(rangeStart, rangeEnd);

            // Ensuring that the first entry is included in the results
            boolean hasMatchingEntry = searchResults.stream().anyMatch(entry -> entry.getTitle().equals(entry1.getTitle()) && entry.getTextEntry().equals(entry1.getTextEntry()));

            // Ensuring that the second entry is NOT included in the results
            boolean hasNonMatchingEntry = searchResults.stream().anyMatch(entry -> entry.getTitle().equals(entry2.getTitle()) && entry.getTextEntry().equals(entry2.getTextEntry()));

            assertTrue(hasMatchingEntry, "The first journal entry should be included within the search results.");
            assertFalse(hasNonMatchingEntry, "The second journal entry should NOT be included within the search results.");

        } catch (SQLException e) {
            fail("SQL error in getJournalEntriesByDateRange: " + e.getMessage());
        }
    }


    @Test
    void insertJournalEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is a test.", currTime);
            boolean insertionSuccess = dao.insertJournalEntry(entry1);
            assertTrue(insertionSuccess, "The resource should have been inserted successfully");

            // Double checking that the resource was actually inserted by calling getAllJournalEntries (not testing time equivalence due to millisecond differences cause inequality)
            List<JournalEntry> entries = dao.getAllJournalEntries();
            boolean isEntryInserted = entries.stream().anyMatch(entry -> entry.getTitle().equals(entry1.getTitle()) && entry.getTextEntry().equals(entry1.getTextEntry()));
            assertTrue(isEntryInserted, "The inserted entry should be found in the database");

        } catch (SQLException e) {
            fail("SQL error in insertJournalEntry: " + e.getMessage());
        }
    }


    @Test
    void updateJournalEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is a test.", currTime);
            dao.insertJournalEntry(entry1);

            // Finding the inserted entry to get its ID
            List<JournalEntry> entries = dao.getAllJournalEntries();
            JournalEntry inserted = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Inserted journal entry not found."));

            // Creating an updated version of the test entry with the same ID and checking if the update was successful
            JournalEntry updatedEntry = new JournalEntry(inserted.getJournalID(), "Test Entry 1 Updated", "This is an updated test.", currTime);
            boolean updateSuccess = dao.updateJournalEntry(updatedEntry);
            assertTrue(updateSuccess, "The resource should have been updated successfully");

            // Checking that the entry was actually updated by verifying contents via ID (without time check due to milliseconds)
            JournalEntry retrieved = dao.getJournalEntryByID(inserted.getJournalID());
            assertNotNull(retrieved, "Journal entry should exist");

            assertEquals(updatedEntry.getJournalID(), retrieved.getJournalID(), "Journal ID should be the same");
            assertEquals(updatedEntry.getTitle(), retrieved.getTitle(), "Title should be the same");
            assertEquals(updatedEntry.getTextEntry(), retrieved.getTextEntry(), "Text entry should be the same");

        } catch (SQLException e) {
            fail("SQL error in updateJournalEntry: " + e.getMessage());
        }
    }


    @Test
    void deleteJournalEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            JournalEntry entry1 = new JournalEntry(0, "Test Entry 1", "This is a test.", currTime);
            dao.insertJournalEntry(entry1);

            // Finding the inserted entry to get its ID
            List<JournalEntry> entries = dao.getAllJournalEntries();
            JournalEntry inserted = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Inserted journal entry not found."));

            // Checking that the entry exists in database before deletion
            JournalEntry entryBeforeDeletion = dao.getJournalEntryByID(inserted.getJournalID());
            assertNotNull(entryBeforeDeletion, "Journal entry should exist before deletion");

            // Deleting entry
            boolean deletionSuccess = dao.deleteJournalEntry(inserted.getJournalID());
            assertTrue(deletionSuccess, "The resource should have been deleted successfully");

            // Making sure it is no longer in the database
            JournalEntry entryAfterDeletion = dao.getJournalEntryByID(inserted.getJournalID());
            assertNull(entryAfterDeletion, "Journal entry should not exist after deletion");

        } catch (SQLException e) {
            fail("SQL error in deleteJournalEntry: " + e.getMessage());
        }
    }
}