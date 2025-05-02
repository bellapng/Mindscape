package controller;

import java.sql.*;
import java.util.*;

import dao.MoodDAO;
import models.Mood;
import models.MoodEntry;

/**
 * Controller class for mood tracking feature to handle user events, call DAO methods, and interact with the program.
 * @author Isabella Castillo
 */
public class MoodTrackerController {

    // Creating necessary objects
    private final MoodDAO moodDAO = new MoodDAO();


    /**
     * Gets the 15 predefined moods from the database.
     * 
     * @return List<Mood>   Returns list of moods.
     * @throws SQLException If an error occurs.
     */
    public List<Mood> getMoodList() throws SQLException { return moodDAO.getMoodList(); }


    /**
     * Finds a mood object by its assigned ID.
     * 
     * @param  id           Unique ID assigned to specific mood.
     * @return Mood         Returns mood object of that specific int ID.
     * @throws SQLException If an error occurs.
     */
    public Mood getMoodByID(int id) throws SQLException { return moodDAO.getMoodByID(id); }


    /**
     * Finds all mood entries and returns them in descending order (most recent first).
     * 
     * @return List<MoodEntry> Returns a list of entries in descending order.
     * @throws SQLException    If an error occurs.
     */
    public List<MoodEntry> getAllMoodEntries() throws SQLException { return moodDAO.getAllMoodEntries(); }


    /**
     * Inserts a users mood entry with the given moodID, optional tag, and timestamp.
     * 
     * @param  entry        The entry object to add to database.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean insertMoodEntry(MoodEntry entry) throws SQLException { return moodDAO.insertMoodEntry(entry); }


    /**
     * Updates mood entries by ID.
     * 
     * @param  entry        The entry object to add updates to database.
     * @return boolean      Will return true if update was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean updateMoodEntry(MoodEntry entry) throws SQLException { return moodDAO.updateMoodEntry(entry); }


    /**
     * Deletes mood entries by ID.
     * 
     * @param  entryID      Unique entry ID for the mood we are deleting.
     * @return boolean      Will return true if deletion was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean deleteMoodEntry(int entryID) throws SQLException { return moodDAO.deleteMoodEntry(entryID); }
}