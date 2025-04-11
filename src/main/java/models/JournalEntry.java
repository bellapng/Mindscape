package models;

import java.time.LocalDateTime;

/**
 * Object class for journal entries.
 * @author Isabella Castillo
 */
public class JournalEntry {

    private int journalID;
    private String title;
    private String textEntry;
    private LocalDateTime entryDateTime;

    /**
     * JournalEntry object.
     * 
     * @param journalID     The ID of the specific journal entry.
     * @param title         The title of the journal entry.
     * @param textEntry     The text of the journal entry.
     * @param entryDateTime The date and time of the journal entry.
     */
    public JournalEntry(int journalID, String title, String textEntry, LocalDateTime entryDateTime) {
        this.journalID = journalID;
        this.title = title;
        this.textEntry = textEntry;
        this.entryDateTime = entryDateTime;
    }

    /**
     * Set of getters for parameters within the JournalEntry object.
     */
    public int getJournalID() { return journalID; }
    public String getTitle() { return title; }
    public String getTextEntry() { return textEntry; }
    public LocalDateTime getEntryDateTime() { return entryDateTime; }

    /**
     * Set of setters for parameters within the JournalEntry object.
     */
    public void setJournalID(int journalID) { this.journalID = journalID; }
    public void setTitle(String title) { this.title = title; }
    public void setTextEntry(String textEntry) { this.textEntry = textEntry; }
    public void setEntryDateTime(LocalDateTime entryDateTime) { this.entryDateTime = entryDateTime; }
}
