package dao;

import models.DatabaseConnection;
import models.JournalEntry;
import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Data Access Object for main program to manage journal entries.
 * @author Isabella Castillo
 */
public class JournalDAO {

    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Gets all the journal entries in database, sorted from the newest to oldest.
     * Useful for mass retrieval. 
     * 
     * @return List<JournalEntry> Returns a list of all journal entries.
     * @throws SQLException       If an error occurs.
     */
    public List<JournalEntry> getAllJournalEntries() throws SQLException {

        List<JournalEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM journal ORDER BY entry_date_and_time DESC";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                entries.add(new JournalEntry(rs.getInt("journal_id"), rs.getString("title"), rs.getString("text_entry"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }


    /**
     * Gets a journal entry by it's specific journal ID.
     * Useful for targetting specific entries for editing/deletion.
     * 
     * @param  id                 Integer representing the unique journal entry ID.
     * @return List<JournalEntry> Returns the journal entry if found, if not then it returns null.
     * @throws SQLException       If an error occurs.
     */
    public JournalEntry getJournalEntryByID(int id) throws SQLException {

        String query = "SELECT * FROM journal WHERE journal_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new JournalEntry(rs.getInt("journal_id"), rs.getString("title"), rs.getString("text_entry"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT));
            } 
        }
        // When not found
        return null;
    }


    /**
     * Searches the database of entries (title and text) by keyword.
     * Useful for user searches.
     * 
     * @param  keyword            Word that represents our search value for query.
     * @return List<JournalEntry> Returns the valid search results in a list.
     * @throws SQLException       If an error occurs.
     */
    public List<JournalEntry> searchJournalEntries(String keyword) throws SQLException {

        List<JournalEntry> results = new ArrayList<>();
        String query = "SELECT * FROM journal WHERE title LIKE ? OR text_entry LIKE ? ORDER BY entry_date_and_time DESC";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            String pattern = "%" + keyword + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(new JournalEntry(rs.getInt("journal_id"), rs.getString("title"), rs.getString("text_entry"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT)));
            }
        }
        return results;
    }


    /**
     * Gets journal entries by date range.
     * Useful for data analysis and chart/graph creation later on.
     * 
     * @param  start              Range start time.
     * @param  end                Range end time.
     * @return List<JournalEntry> Returns the valid results in a list.
     * @throws SQLException       If an error occcurs.
     */
    public List<JournalEntry> getMoodEntriesByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException {

        List<JournalEntry> entries = new ArrayList<>();
        String query = "SELECT * FROM journal WHERE entry_date_and_time BETWEEN ? AND ? ORDER BY entry_date_and_time";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, start.format(DB_DATE_FORMAT));
            pstmt.setString(2, end.format(DB_DATE_FORMAT));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                entries.add(new JournalEntry(rs.getInt("journal_id"), rs.getString("title"), rs.getString("text_entry"), LocalDateTime.parse(rs.getString("entry_date_and_time"), DB_DATE_FORMAT)));
            }
        }
        return entries;
    }


    /**
     * Inserts a journal entry into the database.
     * Useful for creation of new entries, a common feature in Mindscape.
     * 
     * @param  entry        A JournalEntry type variable of the entry using JournalEntry.java to add to database.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean insertJournalEntry(JournalEntry entry) throws SQLException {

        String query = "INSERT INTO journal (title, text_entry, entry_date_and_time) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getTextEntry());
            pstmt.setString(3, entry.getEntryDateTime().format(DB_DATE_FORMAT));
            return pstmt.executeUpdate() > 0; // Returns true if inserted successfully
        }
    }

    /**
     * Updates a known journal entry in the database.
     * Useful for using together with search functions to find and update entries by user request.
     * 
     * @param  entry        A JournalEntry type variable of the entry using JournalEntry.java to update to the database.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean updateJournalEntry(JournalEntry entry) throws SQLException {

        String query = "UPDATE journal SET title = ?, text_entry = ? WHERE journal_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, entry.getTitle());
            pstmt.setString(2, entry.getTextEntry());
            pstmt.setInt(3, entry.getJournalID());
            return pstmt.executeUpdate() > 0; // Returns true if updated successfully
        }
    }

    /**
     * Deletes a journal entry in the database.
     * Useful for using together with search functions to find and delete entries by user request.
     * 
     * @param  id           The unique journal entry ID we are updating.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean deleteJournalEntry(int id) throws SQLException {

        String query = "DELETE FROM journal WHERE journal_id = ?";

        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0; // Returns true if deleted successfully
        }
    }
}