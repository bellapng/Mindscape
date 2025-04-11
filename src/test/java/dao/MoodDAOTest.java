package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

import models.MoodEntry;
import models.Mood;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for MoodDAO.java.
 * Each test matches the name of the method in the MoodDAO class and has descriptive comments.
 * @author Isabella Castillo
 */
class MoodDAOTest {


    private MoodDAO dao;


    @BeforeEach
    void setUp() {

        dao = new MoodDAO();
    }


    @AfterEach
    void tearDown() {

        try {

            List<MoodEntry> entries = dao.getAllMoodEntries();

            for (MoodEntry entry : entries) {

                dao.deleteMoodEntry(entry.getEntryID());
            }
        } catch (SQLException e) {
            System.err.println("tearDown failed: " + e.getMessage());
        }
    }


    @Test
    void getMoodList() {

        try {

            // Assign list to variable and assert that the list is not empty (should be predefined in database)
            List<Mood> moods = dao.getMoodList ();
            assertFalse(moods.isEmpty(), "Mood list should not be empty");

        } catch (SQLException e) {
            fail("SQL error in getMoodList: " + e.getMessage());
        }
    }


    @Test
    void getMoodEntryByID() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry
            MoodEntry testEntry = new MoodEntry(0, 1, "Missed dose", currTime);
            dao.insertMoodEntry(testEntry);

            // Retrieving all entries to find our ID we are looking for
            List<MoodEntry> entries = dao.getAllMoodEntries();
            MoodEntry inserted = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Inserted mood entry not found."));

            // Perform test and verify retrieved results to check for matching ID (minus the time portion since milliseconds can cause inequality within tests)
            MoodEntry retrieved = dao.getMoodEntryByID(inserted.getEntryID());
            assertNotNull(retrieved, "Mood entry should exist");

            assertEquals(inserted.getEntryID(), retrieved.getEntryID(), "Mood entry ID should be the same");
            assertEquals(inserted.getMoodID(), retrieved.getMoodID(), "Mood ID should be the same");
            assertEquals(inserted.getTag(), retrieved.getTag(), "Tagshould be the same");

        } catch (SQLException e) {
            fail("SQL error in getMoodEntryByID: " + e.getMessage());
        }
    }


    @Test
    void getMoodByID() {

        try {

            // Creating a mood with ID of 1 (1. Relaxed) and ensuring it exists and equals what it should.
            Mood mood1 = dao.getMoodByID(1);
            assertNotNull(mood1, "Mood with ID 1 should exist");
            assertEquals("Relaxed", mood1.getMoodName());

            // Creating a mood with ID of 15 (15. Bored) and ensuring it exists and equals what it should.
            Mood mood15 = dao.getMoodByID(15);
            assertNotNull(mood15, "Mood with ID 15 should exist");
            assertEquals("Bored", mood15.getMoodName());

        } catch (SQLException e) {
            fail("SQL error in getMoodByID: " + e.getMessage());
        }
    }


    @Test
    void getAllMoodEntries() {

        LocalDateTime currTime = LocalDateTime.now();

        try {

            // Creating and inserting the first test entry
            MoodEntry testEntry1 = new MoodEntry(0, 1, "Tag 1", currTime);
            dao.insertMoodEntry(testEntry1);

            // Creating and inserting the second test entry
            MoodEntry testEntry2 = new MoodEntry(0, 2, "Tag 2", LocalDateTime.now().minusMinutes(8));
            dao.insertMoodEntry(testEntry2);

            // Creating and inserting the third test entry
            MoodEntry testEntry3 = new MoodEntry(0, 3, "Tag 3", LocalDateTime.now().minusMinutes(5));
            dao.insertMoodEntry(testEntry3);

            // Perform test by checking that all mood entries are present in the results
            List<MoodEntry> entries = dao.getAllMoodEntries();

            boolean isEntry1Found = entries.stream().anyMatch(entry -> entry.getMoodID() == 1 && entry.getTag().equals("Tag 1"));
            boolean isEntry2Found = entries.stream().anyMatch(entry -> entry.getMoodID() == 2 && entry.getTag().equals("Tag 2"));
            boolean isEntry3Found = entries.stream().anyMatch(entry -> entry.getMoodID() == 3 && entry.getTag().equals("Tag 3"));

            assertTrue(isEntry1Found, "First mood entry was not found in getAllMoodEntries result.");
            assertTrue(isEntry2Found, "Second mood entry was not found in getAllMoodEntries result.");
            assertTrue(isEntry3Found, "Third mood entry was not found in getAllMoodEntries result.");

            // Verify that we have at least 3 mood entries in the list
            assertTrue(entries.size() >= 3, "Expected at least 3 mood entries in the list");

        } catch (SQLException e) {
            fail("SQL error in getAllMoodEntries: " + e.getMessage());
        }
    }


    @Test
    void getMoodEntriesByDateRange() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Creating and inserting a test entry that SHOULD be within the test range
            MoodEntry testEntry1 = new MoodEntry(0, 1, "Tag 1", currTime.minusHours(1));
            dao.insertMoodEntry(testEntry1);

            // Creating and inserting a test entry that SHOULD NOT be within the test range
            MoodEntry testEntry2 = new MoodEntry(0, 2, "Tag 2", currTime.minusDays(3));
            dao.insertMoodEntry(testEntry2);

            // Defining the range start and end time (that will match the first test entry and not the second)
            LocalDateTime rangeStart = currTime.minusHours(2);

            List<MoodEntry> searchResults = dao.getMoodEntriesByDateRange(rangeStart, currTime);

            // Checking that testEntry1 is in results and that testEntry2 is NOT in the results of the search (not testing time equivalence due to millisecond differences cause inequality)
            boolean hasMatching = searchResults.stream().anyMatch(entry -> entry.getMoodID() == 1 && entry.getTag().equals("Tag 1"));
            boolean hasNonMatching = searchResults.stream().anyMatch(entry -> entry.getMoodID() == 2 && entry.getTag().equals("Tag 2"));

            assertTrue(hasMatching, "Test entry 1 should be included within the search results.");
            assertFalse(hasNonMatching, "Test entry 2 should NOT be included within the search results.");

        } catch (SQLException e) {
            fail("SQL error in getMoodEntriesByDateRange: " + e.getMessage());
        }
    }


    @Test
    void getMostFrequentMoods() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Inserting multiple entries with different occurences to simulate different frequencies (5x relaxed, 3x anxious, 1x bored)
            for (int i = 0; i < 5; i++) {
                dao.insertMoodEntry(new MoodEntry(0, 1, "Relaxed entries", currTime.minusHours(i * 3)));
            }

            for (int i = 0; i < 3; i++) {
                dao.insertMoodEntry(new MoodEntry(0, 10, "Anxious entries", currTime.minusHours(i * 11)));
            }

            dao.insertMoodEntry(new MoodEntry(0, 15, "Bored entry", currTime.minusMinutes(15)));

            // Requesting top 3 most frequent moods and get key list (we will verify order is correct too)
            Map<String, Integer> freq = dao.getMostFrequentMoods(3);
            assertEquals(3, freq.size(), "Expected 3 most frequent moods");
            List<String> keyList = new ArrayList<>(freq.keySet());

            // Checking correct order and amount
            assertEquals("Relaxed", keyList.get(0), "Relaxed should be the most frequent mood.");
            assertEquals(5, freq.get("Relaxed"), "Relaxed should have count 5.");

            assertEquals("Anxious", keyList.get(1), "Anxious should be second.");
            assertEquals(3, freq.get("Anxious"), "Anxious should have count 3.");

            assertEquals("Bored", keyList.get(2), "Bored should be third.");
            assertEquals(1, freq.get("Bored"), "Bored should have count 1.");

        } catch (SQLException e) {
            fail("SQL error in getMostFrequentMoods: " + e.getMessage());
        }
    }


    @Test
    void insertMoodEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            MoodEntry testEntry1 = new MoodEntry(0, 1, "Tag 1", currTime);
            boolean insertionSuccess = dao.insertMoodEntry(testEntry1);
            assertTrue(insertionSuccess, "The test entry should have been inserted successfully");

            // Double checking that the resource was actually inserted by calling getAllMoodEntries (not testing time equivalence due to millisecond differences cause inequality)
            List<MoodEntry> entries = dao.getAllMoodEntries();
            boolean isEntryInserted = entries.stream().anyMatch(entry -> entry.getMoodID() == 1 && entry.getTag().equals("Tag 1"));
            assertTrue(isEntryInserted, "The inserted resource should be found in the database");

        } catch (SQLException e) {
            fail("SQL error in insertExerciseEntry: " + e.getMessage());
        }
    }


    @Test
    void updateMoodEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            MoodEntry testEntry = new MoodEntry(0, 1, "Tag 1", currTime);
            dao.insertMoodEntry(testEntry);

            // Creating an updated version of the test entry with the same ID and different mood
            List<MoodEntry> entries = dao.getAllMoodEntries();
            MoodEntry insertedEntry = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Updating entry and checking if it was successful or not, should return true
            MoodEntry updatedEntry = new MoodEntry(insertedEntry.getEntryID(), 2, "Tag 2", currTime);
            boolean updateSuccess = dao.updateMoodEntry(insertedEntry);
            assertTrue(updateSuccess, "The mood entry should have been updated successfully");

            // Checking that the mood entry was actually updated by verifying contents via ID
            MoodEntry retrievedEntry = dao.getMoodEntryByID(insertedEntry.getEntryID());
            assertNotNull(retrievedEntry, "Updated resource should be retrievable");

            assertEquals(2, updatedEntry.getMoodID(), "Mood ID should be the same");
            assertEquals("Tag 2", updatedEntry.getTag(), "Tag should be the same");

        } catch (SQLException e) {
            fail("SQL error in updateMoodEntry:" + e.getMessage());
        }
    }


    @Test
    void deleteMoodEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            MoodEntry testEntry = new MoodEntry(0, 1, "Tag 1", currTime);
            dao.insertMoodEntry(testEntry);

            // Finding the inserted entry to get its ID
            List<MoodEntry> entries = dao.getAllMoodEntries();
            MoodEntry insertedEntry = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test mood entry"));

            // Checking that the entry exists in database before deletion
            MoodEntry entryBeforeDeletion = dao.getMoodEntryByID(insertedEntry.getEntryID ());
            assertNotNull(entryBeforeDeletion, "Mood entry should exist before deletion");

            // Deleting test entry
            boolean deletionSuccess = dao.deleteMoodEntry(insertedEntry.getEntryID());
            assertTrue(deletionSuccess, "The mood entry should have been deleted successfully");

            // Making sure it is no longer in the database
            MoodEntry entryAfterDeletion = dao.getMoodEntryByID(insertedEntry.getEntryID());
            assertNull(entryAfterDeletion, "Mood entry should not exist after deletion");

        } catch (SQLException e) {
            fail("SQL error in deleteMoodEntry:" + e.getMessage());
        }
    }
}