package controller;

import java.sql.*;
import java.time.*;
import java.util.*;

import dao.MoodDAO;
import dao.ExerciseDAO;
import models.Mood;
import models.MoodEntry;
import models.Exercise;
import models.ExerciseEntry;

/**
 * Controller class for main program to handle user events, call DAO methods, and interact with the program.
 * @author Isabella Castillo
 */
public class DataVisualizationController {

    // Creating necessary objects
    private final MoodDAO moodDAO = new MoodDAO();
    private final ExerciseDAO exerciseDAO = new ExerciseDAO();


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
     * @param  id           The unique ID assigned to specific mood.
     * @return Mood         Returns mood object of that unique int ID.
     * @throws SQLException If an error occurs.
     */
    public Mood getMoodByID(int id) throws SQLException { return moodDAO.getMoodByID(id); }


    /**
     * Gets mood entries within a specific date range.
     * 
     * @param  start           Range start time.
     * @param  end             Range end time.
     * @return List<MoodEntry> Returns a list of mood entries in the specified range.
     * @throws SQLException    If an error occurs.
     */
    public List<MoodEntry> getMoodEntriesByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException { return moodDAO.getMoodEntriesByDateRange(start, end); }


    /**
     * Gets all exercises from the database.
     * 
     * @return List<Exercise> Returns a list of all exercises.
     * @throws SQLException   If an error occurs.
     */
    public List<Exercise> getAllExercises() throws SQLException { return exerciseDAO.getExerciseList(); }


    /**
     * Gets exercise entries within a specific date range.
     * 
     * @param  start                Range start time.
     * @param  end                  Range end time.
     * @return List<ExerciseEntry>  Returns a list of exercise entries in the specified range.
     * @throws SQLException         If an error occurs.
     */
    public List<ExerciseEntry> getExerciseEntriesByDateRange(LocalDateTime start, LocalDateTime end) throws SQLException { return exerciseDAO.getExerciseEntriesByDateRange(start, end); }
}
