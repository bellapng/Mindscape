package models;

/**
 * Object class for the 3 different exercises.
 * @author Isabella Castillo
 */
public class Exercise {

    private int exerciseID;
    private String exerciseName;
    private String exerciseDescription;

    /**
     * Exercise object.
     * 
     * @param exerciseID The ID of the exercise (1-3) that is assigned.
     * @param exerciseName The name of the specific exercise (3 options).
     * @param exerciseDescription Text description for the specific exercise.
     */
    public Exercise(int exerciseID, String exerciseName, String exerciseDescription) {

        this.exerciseID = exerciseID;
        this.exerciseName = exerciseName;
        this.exerciseDescription = exerciseDescription;
    }

    /*
     * Set of getters for parameters within the Exercise object.
     */
    public int getExerciseID() { return exerciseID; }
    public String getExerciseName() { return exerciseName; }
    public String getExerciseDescription() { return exerciseDescription; }

    /*
     * Set of setters for parameters within the Exercise object.
     */
    public void setExerciseID(int exerciseID) { this.exerciseID = exerciseID; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
    public void setExerciseDescription(String exerciseDescription) { this.exerciseDescription = exerciseDescription; }
    
}