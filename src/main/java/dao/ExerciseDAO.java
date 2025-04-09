package dao;

import models.DatabaseConnection;
import models.Exercise;
import models.ExerciseEntry;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for main program to manage exercises and exercise entries.
 * @author Isabella Castillo
 */
public class ExerciseDAO {

    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Retrieves the 3 predefined exercises from the database.
     * Useful for populating dropdowns and similar uses.
     * 
     * @return List<Exercise> A list of exercise objects.
     * @throws SQLException   If an error occurs.
     */
    public List<Exercise> getExerciseList() throws SQLException {

        List<Exercise> exercises = new ArrayList<>();
        String query = "SELECT * FROM exercises ORDER BY exercise_id";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                exercises.add(new Exercise(rs.getInt("exercise_id"), rs.getString("exercise_name"), rs.getString("exercise_description")));
            }
        }
        return exercises;
    }

    /**
     * Finds a predefined exercise by it's assigned ID.
     * Useful for display purposes.
     * 
     * @param  id           Unique ID assigned to specific exercise (1-3).
     * @return String       String consisting of matching mood name with given ID.
     * @throws SQLException If an error occurs.
     */
    public Exercise getExerciseByID(int id) throws SQLException {

        String query = "SELECT * FROM exercises WHERE exercise_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Exercise(rs.getInt("exercise_id"), rs.getString("exercise_name"), rs.getString("exercise_description"));
            }
        }
        return null;
    }

    /**
     * Gets a list of all the exercise entries within the database.
     * Useful for data analysis and future charting/graphing.
     * 
     * @return List<ExerciseEntry> Returns a ArrayList of all exercise entries in the database.
     * @throws SQLException        If an error occurs.
     */
    public List<ExerciseEntry> getAllExerciseEntries() throws SQLException {

        List<ExerciseEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM exercise_entries ORDER BY start_time DESC";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                entries.add(new ExerciseEntry(rs.getInt("log_id"), rs.getInt("exercise_id"), rs.getInt("mood_before_id"), rs.getInt("mood_after_id"),
                            LocalDateTime.parse(rs.getString("start_time"), DB_DATE_FORMAT), LocalDateTime.parse(rs.getString("end_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }

    /**
     * Gets a list of exercise entries within a specific range within the database.
     * Useful for data analysis and future charting/graphing.
     * 
     * @param  start               The range start time.
     * @param  end                 The range end time.
     * @return List<ExerciseEntry> Returns a list of all exercise entries within the specified date range.
     * @throws SQLException        If an error occurs.
     */
    public List<ExerciseEntry> getExerciseEntriesByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {

        List<ExerciseEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM exercise_entries WHERE start_time BETWEEN ? AND ? ORDER BY start_time";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, start.format(DB_DATE_FORMAT));
            pstmt.setString(2, end.format(DB_DATE_FORMAT));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(new ExerciseEntry(rs.getInt("log_id"), rs.getInt("exercise_id"), rs.getInt("mood_before_id"), rs.getInt("mood_after_id"), 
                            LocalDateTime.parse(rs.getString("start_time"), DB_DATE_FORMAT), LocalDateTime.parse(rs.getString("end_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }

    /**
     * Inserts exercise entry objects into the database.
     * Useful for inserting new ExerciseEntry objects into the database.
     * 
     * @param  entry   A ExerciseEntry type variable of the entry using ExerciseEntry.java to add to database.
     * @return boolean Returns T/F based on if the insertion is successful or not.
     * @throws SQLException If an error occurs.
     */
    public boolean insertExerciseEntry(ExerciseEntry entry) throws SQLException {

        String query = "INSERT INTO exercise_entries (exercise_id, mood_before_id, mood_after_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, entry.getExerciseID());

            // If the mood before doesn't equal null, insert into the entry. If it does, then null will be entered into database.
            if (entry.getMoodBeforeID() != null) {

                pstmt.setInt(2, entry.getMoodBeforeID());
            } else {

                pstmt.setNull(2, Types.INTEGER);
            }

            // If the mood after doesn't equal null, insert into the entry. If it does, then null will be entered into database.
            if (entry.getMoodAfterID() != null) {

                pstmt.setInt(3, entry.getMoodAfterID());
            } else {

                pstmt.setNull(3, Types.INTEGER);
            }

            pstmt.setString(4, entry.getStartTime().format(DB_DATE_FORMAT));
            pstmt.setString(5, entry.getEndTime().format(DB_DATE_FORMAT));

            return pstmt.executeUpdate() > 0;   // Will return true upon successful insertion
        }
    }

    /**
     * Updates the mood before the exercise.
     * Useful for post-exercise logging in which the user forgets to add their mood and they want to later.
     * 
     * @param  logID           The logs unique ID for accessing and editing it.
     * @param  newMoodBeforeID The ID of the unique mood we are adding.
     * @return boolean         Returns T/F based on if the update was successful or not.
     * @throws SQLException    If an error occurs.
     */
    public boolean updateMoodBeforeExercise(int logID, int newMoodBeforeID) throws SQLException {

        String query = "UPDATE exercise_entries SET mood_before_id = ? WHERE log_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, newMoodBeforeID);
            pstmt.setInt(2, logID);
            return pstmt.executeUpdate() > 0; // Will return true upon successful update
        }

    }

    /**
     * Updates the mood after the exercise.
     * Useful for post-exercise logging in which the user forgets to add their mood and they want to later.
     * 
     * @param  logID          The logs unique ID for accessing and editing it.
     * @param  newMoodAfterID The ID of the unique mood we are adding.
     * @return boolean        Returns T/F based on if the update was successful or not.
     * @throws SQLException   If an error occurs.
     */
    public boolean updateMoodAfterExercise(int logID, int newMoodAfterID) throws SQLException {

        String query = "UPDATE exercise_entries SET mood_after_id = ? WHERE log_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, newMoodAfterID);
            pstmt.setInt(2, logID);
            return pstmt.executeUpdate() > 0; // Will return true upon successful update
        }
    }

    /**
     * Deletes an exercise entry by ID.
     * Useful for deleting logs the user no longer wants.
     * 
     * @param  logID           The logs unique ID for accessing and deleting it.
     * @return boolean         Returns T/F based on if the deletion was successful or not.
     * @throws SQLException    If an error occurs.
     */
    public boolean deleteExerciseEntry(int logID) throws SQLException {

        String query = "DELETE FROM exercise_entries WHERE log_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, logID);
            return pstmt.executeUpdate() > 0; // Will return true upon successful deletion
        }
    }
}