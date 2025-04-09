package dao;

import models.DatabaseConnection;
import models.Mood;
import models.MoodEntry;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for main program to manage moods and mood entries.
 * @author Isabella Castillo
 */
public class MoodDAO {

    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Retrieves the 15 predefined moods from the database.
     * Useful for populating dropdowns and similar uses.
     * 
     * @return List<Mood>   A list of mood objects.
     * @throws SQLException If an error occurs.
     */
    public List<Mood> getMoodList() throws SQLException {

        List<Mood> moods = new ArrayList<>();
        String query = "SELECT mood_id, mood FROM moods ORDER BY mood_id";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {

                int id = rs.getInt("mood_id");
                String name = rs.getString("mood");
                moods.add(new Mood(id, name));
            }
        }
        return moods;
    }


    /**
     * Fetches a specific mood entry by its unique entry ID.
     * Useful for editing mood entries.
     * 
     * @param  id           The integer ID passed through to find specific entry.
     * @return MoodEntry    Will return a mood entry object based on ID given or will return null if not found.
     * @throws SQLException If an error occurs.
     */
    public MoodEntry getMoodEntryByID(int id) throws SQLException {

        String query = "SELECT * FROM mood_entries WHERE entry_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new MoodEntry(rs.getInt("entry_id"), rs.getInt("mood_id"), rs.getString("tag"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT));
            }
        }
        // When not found
        return null;
    }


    /**
     * Finds a mood ID by its name (case sensitive).
     * Useful for insertion.
     * 
     * @param  moodName     Name of mood we are attempting to find ID for.
     * @return Mood         Mood object of that specific mood name or -1 if not found.
     * @throws SQLException If an error occurs.
     */
    public Mood getMoodByName(String moodName) throws SQLException {

        String query = "SELECT * FROM moods WHERE mood = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, moodName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Mood(rs.getInt("mood_id"), rs.getString("mood"));
            }
        }
        return null;
    }


    /**
     * Finds a mood object by it's assigned ID.
     * Useful for display and search purposes.
     * 
     * @param  id       Unique ID assigned to specific mood.
     * @return Mood         Mood object of that specific ID.
     * @throws SQLException If an error occurs.
     */
    public Mood getMoodByID(int id) throws SQLException {

        String query = "SELECT mood FROM moods WHERE mood_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Mood(rs.getInt("mood_id"), rs.getString("mood"));
            }
        }
        // When not found
        return null;
    }

    
    /**
     * Finds all mood entries and returns them in descending order (most recent first).
     * Useful for data analysis and chart/graph creation later on.
     * 
     * @return List<MoodEntry> Returns a list of entries in descending order.
     * @throws SQLException    If an error occurs.
     */
    public List<MoodEntry> getAllMoodEntries() throws SQLException {

        List<MoodEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM mood_entries ORDER BY entry_date_and_time DESC";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                entries.add(new MoodEntry(rs.getInt("entry_id"), rs.getInt("mood_id"), rs.getString("tag"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }


    /**
     * Finds mood entries with specific date and range.
     * Useful for data analysis and chart/graph creation later on.
     * 
     * @param  start           Range start time.
     * @param  end             Range end time.
     * @return List<MoodEntry> Returns a list of mood entries in the specified range.
     * @throws SQLException    If an error occurs.
     */
    public List<MoodEntry> getMoodEntriesByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {

        List<MoodEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM mood_entries WHERE entry_date_and_time BETWEEN ? AND ? ORDER BY entry_date_and_time";
        
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, start.format(DB_DATE_FORMAT));
            pstmt.setString(2, end.format(DB_DATE_FORMAT));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(new MoodEntry(rs.getInt("entry_id"), rs.getInt("mood_id"), rs.getString("tag"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }


    /**
     * Gives the top N most frequently logged moods in the local database (sorts from most to least frequent).
     * Useful for data analysis and chart/graph creation later on.
     * 
     * @param  limit                The amount (N) that we want to limit our result to (Top N moods).
     * @return Map<String, Integer> Returns a map of most frequent moods with their key (string ie the name of the mood) and their value (integer ie the # of times mood was logged).
     * @throws SQLException         If an error occurs.
     */
    public Map<String, Integer> getMostFrequentMoods(int limit) throws SQLException {

        Map<String, Integer> moodFrequency = new LinkedHashMap<>();
        String query = "SELECT m.mood, COUNT(*) AS count " + "FROM mood_entries me " + "JOIN moods m ON me.mood_id = m.mood_id " + "GROUP BY m.mood " + "ORDER BY count DESC " + "LIMIT ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                moodFrequency.put(rs.getString("mood"), rs.getInt("count"));
            }
        }
        return moodFrequency;
    }


    /**
     * Inserts a users mood entry with the given moodID, optional tag, and timestamp.
     * Useful for inserting a new mood entry into the database.
     * 
     * @param  entry        A MoodEntry type variable of the entry using MoodEntry.java to add to database.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean insertMoodEntry(MoodEntry entry) throws SQLException {

        String query = "INSERT INTO mood_entries (mood_id, tag, entry_date_and_time) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, entry.getMoodID());
            pstmt.setString(2, entry.getTag());
            pstmt.setString(3, entry.getDateAndTime().format(DB_DATE_FORMAT));
            return pstmt.executeUpdate() > 0; // Returns true if at least 1 row in table affected

        }
    }

    /**
     * Used to update mood entries by ID.
     * Useful for updating desired mood entries that can be found via ID (getMoodEntryByID).
     * 
     * @param  entry        A MoodEntry type variable of the entry using MoodEntry.java to add updates to database.
     * @return boolean      Will return true if update was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean updateMoodEntry(MoodEntry entry) throws SQLException {

        String query = "UPDATE mood_entries SET mood_id = ?, tag = ?, WHERE entry_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, entry.getMoodID());
            pstmt.setString(2, entry.getTag());
            pstmt.setInt(3, entry.getEntryID());
            return pstmt.executeUpdate() > 0; // Returns true if at least one row updated

        }
    }


    /**
     * Used to delete mood entries by ID.
     * Useful for deleting desired mood entries that can be found via ID (getMoodEntryByID).
     * 
     * @param  entryID      Unique entry ID for the mood we are deleting.
     * @return boolean      Will return true if deletion was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean deleteMoodEntry(int entryID) throws SQLException {

        String query = "DELETE FROM mood_entries WHERE entry_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, entryID);
            return pstmt.executeUpdate() > 0; // Returns true if at least one row deleted
        }
    }
}