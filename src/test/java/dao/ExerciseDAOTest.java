package dao;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import models.DatabaseConnection;
import models.Exercise;
import models.ExerciseEntry;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ExerciseDAO.java.
 * Each test matches the name of the method in the ExerciseDAO class and has descriptive comments.
 * @author Isabella Castillo
 */
class ExerciseDAOTest {


    private ExerciseDAO dao;
    
    private static final DateTimeFormatter DB_DATE_FORMAT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @BeforeEach
    void setUp() {

        dao = new ExerciseDAO();
    }


    @AfterEach
    void tearDown() {

        try {

            // Deleting test entries after each test as to not corrupt database, but also test the real thing
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();

            for (ExerciseEntry entry : entries) {
                
                dao.deleteExerciseEntry(entry.getLogID());
            }
        } catch (SQLException e) {
            System.err.println("tearDown failed: " + e.getMessage());
        }
    }


    @Test
    void getExerciseList() {

        try {

            // Assign list to variable and assert that the list is not empty (should be predefined in database)
            List<Exercise> exercises = dao.getExerciseList();
            assertFalse(exercises.isEmpty(), "Exercise list should not be empty");

        } catch (SQLException e) {
            fail("SQL error in getExerciseList: " + e.getMessage());
        }
    }


    @Test
    void getExerciseByID() {

        try {

            // Creating an exercise with ID of 1 (default 'Deep Breathing') and ensuring it exists and equals what it should.
            Exercise exercise1 = dao.getExerciseByID(1);
            assertNotNull(exercise1, "Exercise with ID 1 should exist");
            assertEquals("Deep Breathing", exercise1.getExerciseName());

            // Creating an exercise with ID of 2 (default 'Progressive Muscle Relaxation') and ensuring it exists and equals what it should.
            Exercise exercise2 = dao.getExerciseByID(2);
            assertNotNull(exercise2, "Exercise with ID 2 should exist");
            assertEquals("Progressive Muscle Relaxation", exercise2.getExerciseName());

            // Creating an exercise with ID of 3 (default 'Box Breathing') and ensuring it exists and equals what it should.
            Exercise exercise3 = dao.getExerciseByID(3);
            assertNotNull(exercise3, "Exercise with ID 3 should exist");
            assertEquals("Box Breathing", exercise3.getExerciseName());

        } catch (SQLException e) {
            fail("SQL error in getExerciseByID: " + e.getMessage());
        }
    }


    @Test
    void getExerciseEntryByID() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry
            ExerciseEntry testEntry1 = new ExerciseEntry(0, 1, 1, 2, currTime.minusMinutes(10), currTime);
            dao.insertExerciseEntry(testEntry1);

            // Retrieving all entries to find our ID we are looking for
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();
            ExerciseEntry inserted = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Inserted exercise entry not found."));

            // Perform test and verify retrieved results to check for matching ID (minus the time portion since milliseconds can cause inequality within tests)
            ExerciseEntry retrieved = dao.getExerciseEntryByID(inserted.getLogID());
            assertNotNull(retrieved, "Exercise entry with ID " + inserted.getLogID() + " should exist");

            assertEquals(inserted.getLogID(), retrieved.getLogID(), "Exercise entry ID should be the same");
            assertEquals(inserted.getExerciseID(), retrieved.getExerciseID(), "Exercise ID should be the same");
            assertEquals(inserted.getMoodBeforeID(), retrieved.getMoodBeforeID(), "Mood before ID should be the same");
            assertEquals(inserted.getMoodAfterID(), retrieved.getMoodAfterID(), "Mood after ID should be the same");

        } catch (SQLException e) {
            fail("SQL error in getExerciseEntryByID: " + e.getMessage());
        }
    }


    @Test
    void getAllExerciseEntries() {

        try {

            // Creating and inserting the first test entry (exercise = Deep Breathing, moodBefore = Relaxed, moodAfter = Content)
            ExerciseEntry testEntry1 = new ExerciseEntry(0, 1, 1, 2, (LocalDateTime.now().minusMinutes(5)), (LocalDateTime.now()));
            dao.insertExerciseEntry(testEntry1);

            // Creating and inserting the second test entry (exercise = Progressive Muscle Relaxation, moodBefore = Relaxed, moodAfter = Content)
            ExerciseEntry testEntry2 = new ExerciseEntry(0, 2, 1, 2, (LocalDateTime.now().minusMinutes(5)), (LocalDateTime.now()));
            dao.insertExerciseEntry(testEntry2);

            // Creating and inserting the third test entry (exercise = Progressive Muscle Relaxation, moodBefore = Relaxed, moodAfter = Content)
            ExerciseEntry testEntry3 = new ExerciseEntry(0, 3, 1, 2, (LocalDateTime.now().minusMinutes(5)), (LocalDateTime.now()));
            dao.insertExerciseEntry(testEntry3);

            // Perform test by checking that all entries are present in the results
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();

            boolean isEntry1Found = entries.stream().anyMatch(entry -> entry.getExerciseID() == 1 && entry.getMoodBeforeID() == 1 && entry.getMoodAfterID() == 2);
            boolean isEntry2Found = entries.stream().anyMatch(entry -> entry.getExerciseID() == 2 && entry.getMoodBeforeID() == 1 && entry.getMoodAfterID() == 2);
            boolean isEntry3Found = entries.stream().anyMatch(entry -> entry.getExerciseID() == 3 && entry.getMoodBeforeID() == 1 && entry.getMoodAfterID() == 2);

            assertTrue(isEntry1Found, "First test resource was not found in getAllExerciseEntries result.");
            assertTrue(isEntry2Found, "Second test resource was not found in getAllExerciseEntries result.");
            assertTrue(isEntry3Found, "Third test resource was not found in getAllExerciseEntries result.");

            // Verify that we have at least 2 resources in the list (to ensure list capability works)
            assertTrue(entries.size() >= 3, "Expected at least 3 resources in the favorites list");

        } catch (SQLException e) {
            fail("SQL error in getAllExerciseEntries: " + e.getMessage());
        }
    }


    @Test
    void getExerciseEntriesByDateRange() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Creating and inserting a test entry that SHOULD be within the test range (exercise = Deep Breathing, moodBefore = Relaxed, moodAfter = Content)
            ExerciseEntry testEntry1 = new ExerciseEntry(0, 1, 1, 2, currTime.minusHours(1), currTime.minusHours(1).plusMinutes(5));
            dao.insertExerciseEntry(testEntry1);

            // Creating and inserting a test entry that SHOULD NOT be within the test range (exercise = Box Breathing, moodBefore = Relaxed, moodAfter = Content)
            ExerciseEntry testEntry2 = new ExerciseEntry(0, 3, 1, 2, currTime.minusDays(3), currTime.minusDays(3).plusMinutes(5));
            dao.insertExerciseEntry(testEntry2);

            // Defining the range start and end time (that will match the first test entry and not the second)
            LocalDateTime rangeStart = currTime.minusHours(2);

            List<ExerciseEntry> searchResults = dao.getExerciseEntriesByDateRange(rangeStart, currTime);

            // Checking that testEntry1 is in results and that testEntry2 is NOT in the results of the search (not testing time equivalence due to millisecond differences cause inequality)
            boolean hasMatching = searchResults.stream().anyMatch(entry -> entry.getExerciseID() == testEntry1.getExerciseID() && entry.getMoodBeforeID() == testEntry1.getMoodBeforeID()
                                  && entry.getMoodAfterID() == testEntry1.getMoodAfterID());

            boolean hasNonMatching = searchResults.stream().anyMatch(entry -> entry.getExerciseID() == testEntry2.getExerciseID() && entry.getMoodBeforeID() == testEntry2.getMoodBeforeID()
                                  && entry.getMoodAfterID() == testEntry2.getMoodAfterID());

            assertTrue(hasMatching, "Test entry 1 should be included within the search results.");
            assertFalse(hasNonMatching, "Test entry 2 should NOT be included within the search results.");

        } catch (SQLException e) {
            fail("SQL error in getExerciseEntriesByDateRange: " + e.getMessage());
        }
    }


    @Test
    public boolean insertExerciseEntry(ExerciseEntry entry) throws SQLException {
        String sql = """
            INSERT INTO exercise_entries
              (exercise_id, mood_before_id, mood_after_id, start_time, end_time)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection c = DatabaseConnection.connect();
             PreparedStatement p = c.prepareStatement(sql)) {

            p.setInt(1, entry.getExerciseID());
            if (entry.getMoodBeforeID() != null) p.setInt(2, entry.getMoodBeforeID());
            else                             p.setNull(2, Types.INTEGER);

            if (entry.getMoodAfterID()  != null) p.setInt(3, entry.getMoodAfterID());
            else                              p.setNull(3, Types.INTEGER);

            p.setString(4, entry.getStartTime().format(DB_DATE_FORMAT));
            p.setString(5, entry.getEndTime().format(DB_DATE_FORMAT));

            // returns number of rows changed; >0 means success
            return p.executeUpdate() > 0;
        }
    }


    @Test
    void updateMoodBeforeExercise() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            ExerciseEntry testEntry1 = new ExerciseEntry(0, 1, 1, 2, currTime.minusHours(1), currTime.minusHours(1).plusMinutes(5));
            dao.insertExerciseEntry(testEntry1);

            // Creating an updated version of the test entry with the same ID and different
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();
            ExerciseEntry insertedEntry = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Updating the entry and checking if it was successful or not, should return true
            boolean updateSuccess = dao.updateMoodBeforeExercise(insertedEntry.getLogID(), 3);
            assertTrue(updateSuccess, "The resource should have been updated successfully");

            // Double checking that the resource was actually updated by verifying contents via ID
            List<ExerciseEntry> updatedEntryList = dao.getAllExerciseEntries();
            ExerciseEntry updatedEntry = updatedEntryList.stream().filter(entry -> entry.getLogID() == insertedEntry.getLogID()).findFirst().orElseThrow(() -> new AssertionError("Failed to find updated test resource"));
            assertEquals(3, updatedEntry.getMoodBeforeID(), "Mood before should now be of ID 3");

        } catch (SQLException e) {
            fail("SQL error in updateMoodBeforeExercise:" + e.getMessage());
        }
    }


    @Test
    void updateMoodAfterExercise() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test entry while checking that it was inserted, should return true
            ExerciseEntry testEntry1 = new ExerciseEntry(0, 1, 1, 2, currTime.minusHours(1), currTime.minusHours(1).plusMinutes(5));
            dao.insertExerciseEntry(testEntry1);

            // Creating an updated version of the test entry with the same ID and different mood
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();
            ExerciseEntry insertedEntry = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test resource"));

            // Updating the entry and checking if it was successful or not, should return true
            boolean updateSuccess = dao.updateMoodAfterExercise(insertedEntry.getLogID(), 3);
            assertTrue(updateSuccess, "The resource should have been updated successfully");

            // Double checking that the resource was actually updated by verifying contents via ID
            List<ExerciseEntry> updatedEntryList = dao.getAllExerciseEntries();
            ExerciseEntry updatedEntry = updatedEntryList.stream().filter(entry -> entry.getLogID() == insertedEntry.getLogID()).findFirst().orElseThrow(() -> new AssertionError("Failed to find updated test resource"));
            assertEquals(3, updatedEntry.getMoodAfterID(), "Mood after should now be of ID 3");

        } catch (SQLException e) {
            fail("SQL error in updateMoodAfterExcercise:" + e.getMessage());
        }
    }


    @Test
    void deleteExerciseEntry() {

        try {

            LocalDateTime currTime = LocalDateTime.now();

            // Declaring and inserting test exercise entry while checking that it was inserted successfully
            ExerciseEntry testEntry = new ExerciseEntry(0, 1, 1, 2, currTime.minusMinutes(5), currTime);
            dao.insertExerciseEntry(testEntry);

            // Finding the inserted entry to get its ID
            List<ExerciseEntry> entries = dao.getAllExerciseEntries();
            ExerciseEntry insertedEntry = entries.stream().findFirst().orElseThrow(() -> new AssertionError("Failed to find inserted test exercise entry"));

            // Checking that the entry exists in database before deletion
            ExerciseEntry entryBeforeDeletion = dao.getExerciseEntryByID(insertedEntry.getLogID());
            assertNotNull(entryBeforeDeletion, "Exercise entry should exist before deletion");

            // Deleting test entry
            boolean deletionSuccess = dao.deleteExerciseEntry(insertedEntry.getLogID());
            assertTrue(deletionSuccess, "The exercise entry should have been deleted successfully");

            // Making sure it is no longer in the database
            ExerciseEntry entryAfterDeletion = dao.getExerciseEntryByID(insertedEntry.getLogID());
            assertNull(entryAfterDeletion, "Exercise entry should not exist after deletion");

        } catch (SQLException e) {
            fail("SQL error in deleteExerciseEntry:" + e.getMessage());
        }
    }
}