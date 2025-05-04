package models;

import java.time.LocalDateTime;

/**
 * Object class for exercise entries.
 * @author Isabella Castillo
 */
public class ExerciseEntry {

    private int logID;
    private int exerciseID;
    private Integer moodBeforeID;   // Optional (can be null)
    private Integer moodAfterID;    // Optional (can be null)
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    /**
     * Exercise entry object.
     * 
     * @param logID        The log number of the exercise.
     * @param exerciseID   The exercise ID of the performed exercise.
     * @param moodBeforeID The mood ID of the users chosen mood before the exercise.
     * @param moodAfterID  The mood ID of the users chosen mood after the exercise.
     * @param startTime    Exercise start time.
     * @param endTime      Exercise end time.
     */
    public ExerciseEntry(int logID, int exerciseID, Integer moodBeforeID, Integer moodAfterID, LocalDateTime startTime, LocalDateTime endTime) {

        this.logID = logID;
        this.exerciseID = exerciseID;
        this.moodBeforeID = moodBeforeID;
        this.moodAfterID = moodAfterID;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Set of getters for parameters within the ExerciseEntry object.
     */
    public int getLogID() { return logID; }
    public int getExerciseID() { return exerciseID; }
    public Integer getMoodBeforeID() { return moodBeforeID; }
    public Integer getMoodAfterID() { return moodAfterID; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
}
