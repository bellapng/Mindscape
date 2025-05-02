package controller;

import java.sql.*;
import java.util.*;

import dao.JournalDAO;
import models.JournalEntry;

/**
 * Controller class for journal feature to handle user events, call DAO methods, and interact with the program.
 * @author Isabella Castillo
 */
public class JournalController {

    // Creating necessary objects
    private final JournalDAO journalDAO = new JournalDAO();


    /**
     * Gets all the journal entries in database sorted from the newest to oldest.
     *
     * @return List<JournalEntry> Returns a list of all journal entries.
     * @throws SQLException       If an error occurs.
     */
    public List<JournalEntry> getAllJournalEntries() throws SQLException { return journalDAO.getAllJournalEntries(); }


    /**
     * Searches the database of entries by keyword.
     *
     * @param  keyword            Word that we are searching for.
     * @return List<JournalEntry> Returns the valid search results in a list.
     * @throws SQLException       If an error occurs.
     */
    public List<JournalEntry> searchJournalEntries(String keyword) throws SQLException { return journalDAO.searchJournalEntries(keyword); }


    /**
     * Inserts a journal entry into the database.
     *
     * @param  entry        The journal entry object a to add to database.
     * @return boolean      Will return true if insert was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean insertJournalEntry(JournalEntry entry) throws SQLException { return journalDAO.insertJournalEntry(entry); }


    /**
     * Updates a known journal entry in the database.
     *
     * @param  entry        The journal entry object to update to the database.
     * @return boolean      Will return true if update was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean updateJournalEntry(JournalEntry entry) throws SQLException { return journalDAO.updateJournalEntry(entry); }


    /**
     * Deletes a journal entry in the database.
     *
     * @param  id           Unique journal entry ID for the journal entry we are deleting.
     * @return boolean      Will return true if deletion was successful, will return false otherwise.
     * @throws SQLException If an error occurs.
     */
    public boolean deleteJournalEntry(int id) throws SQLException { return journalDAO.deleteJournalEntry(id); }
}