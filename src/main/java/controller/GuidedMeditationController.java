package controller;

import java.sql.*;
import java.time.*;
import java.util.*;

import dao.ExerciseDAO;
import dao.MoodDAO;
import models.Exercise;
import models.ExerciseEntry;
import models.Mood;

/**
 * Controller class for main program to handle user events, call DAO methods, and interact with the program.
 * @author Isabella Castillo
 */
public class GuidedMeditationController {

    // Creating necessary objects
    private final ExerciseDAO exerciseDAO = new ExerciseDAO();
    private final MoodDAO moodDAO = new MoodDAO();
    private ExerciseEntry currEntry;


    /**
     * Gets all the exercises in database.
     *
     * @return List<Exercise> Returns a list of all exercises.
     * @throws SQLException   If an error occurs.
     */
    public List<Exercise> getAllExercises() throws SQLException { return exerciseDAO.getExerciseList(); }


    /**
     * Gets all the moods in database.
     *
     * @return List<Mood>   Returns a list of all moods.
     * @throws SQLException If an error occurs.
     */
    public List<Mood> getMoodList() throws SQLException { return moodDAO.getMoodList(); }


    /**
     * Starts a new exercise session.
     *
     * @param  exerciseID   The unique ID for the exercise chosen.
     * @param  moodBeforeID The unique ID for the mood before chosen.
     * @return boolean      Returns T/F based on if the entry was added.
     * @throws SQLException If an error occurs.
     */
    public boolean startExercise(int exerciseID, int moodBeforeID) throws SQLException {

        LocalDateTime now = LocalDateTime.now();

        // Inserting temporary entry where we will update 'end time' and 'mood after ID' later
        ExerciseEntry temp = new ExerciseEntry(0, exerciseID, moodBeforeID, moodBeforeID, now, now);
        int newLogID = exerciseDAO.insertExerciseEntry(temp);
        currEntry = exerciseDAO.getExerciseEntryByID(newLogID);

        return currEntry != null;
    }


    /**
     * Stops the current exercise session.
     *
     * @param  moodAfterID  The unique ID for the mood after chosen.
     * @return boolean      Returns T/F based on if the entry was added.
     * @throws SQLException If an error occurs.
     */
    public boolean stopExercise(int moodAfterID) throws SQLException {

        if (currEntry == null) {
            throw new IllegalStateException("Error: No exercise session in progress.");
        }

        // Updating entry mood and end time
        LocalDateTime now = LocalDateTime.now();
        boolean updateCheck1 = exerciseDAO.updateMoodAfterExercise(currEntry.getLogID(), moodAfterID);
        boolean updateCheck2 = exerciseDAO.updateExerciseEndTime(currEntry.getLogID(), now);

        return updateCheck1 && updateCheck2;
    }
}
