package models;

import java.time.LocalDateTime;

/**
 * Object class for mood entries.
 * @author Isabella Castillo
 */
public class MoodEntry {

    private int entryID;
    private int moodID;
    private String tag;
    private LocalDateTime dateAndTime;

    /**
     * Mood entry object.
     * 
     * @param entryID     The ID of the specific entry.
     * @param moodID      The ID of the mood selected within the entry.
     * @param tag         Tag to describe mood context (optional).
     * @param dateAndTime Timestamp in a string format for database storage.
     */
    public MoodEntry(int entryID, int moodID, String tag, LocalDateTime dateAndTime) {

        this.entryID = entryID;
        this.moodID = moodID;
        this.tag = tag;
        this.dateAndTime = dateAndTime;
    }

    /*
     * Set of getters for parameters within the MoodEntry object.
     */
    public int getEntryID() { return entryID; }
    public int getMoodID() { return moodID; }
    public String getTag() { return tag; }
    public LocalDateTime getDateAndTime() { return dateAndTime; }

    /*
     * Set of setters for parameters within the MoodEntry object.
     */
    public void setEntryID(int entryID) { this.entryID = entryID; }
    public void setMoodID(int moodID) { this.moodID = moodID; }
    public void setTag(String tag) { this.tag = tag; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }
}
