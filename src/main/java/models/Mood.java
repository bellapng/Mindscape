package models;

/**
 * Object class for moods.
 * @author Isabella Castillo
 */
public class Mood {

    private int moodID;
    private String moodName;

    /**
     * Mood object.
     * 
     * @param moodID   The ID of the mood selected within the entry.
     * @param moodName The name of the mood.
     */
    public Mood(int moodID, String moodName) {

        this.moodID = moodID;
        this.moodName = moodName;
    }

    /*
     * Set of getters for parameters within the Mood object.
     */
    public int getMoodID() { return moodID; }
    public String getMoodName() { return moodName; }

    /*
     * Set of setters for parameters within the Mood object.
     */
    public void setMoodID(int moodID) { this.moodID = moodID; }
    public void setMoodName(String moodName) { this.moodName = moodName; }
}
