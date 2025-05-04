package models;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Class to generate test data for the database.
 *
 * @author Isabella Castillo
 */
public class TestDataGenerator {

    // Creating objects needed
    private static final DateTimeFormatter DB_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Random random = new Random();


    /**
     * Main method to generate test data.
     */
    public static void main(String[] args) {

        try {

            // Mocking a 6 month (180 days) period of test data
            clearExistingData();
            generateMoodEntries(160);
            generateExerciseEntries(75);
            System.out.println("Test data generation complete!");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }


    /**
     * Clears existing data from the database.
     *
     * @throws SQLException If an error occurs.
     */
    private static void clearExistingData() throws SQLException {

        // Attempt database connection and deletion
        try (Connection conn = DatabaseConnection.connect()) {

            try (Statement stmt = conn.createStatement()) {

                stmt.executeUpdate("DELETE FROM mood_entries");
                stmt.executeUpdate("DELETE FROM exercise_entries");
            }
        }
    }


    /**
     * Generates a specified number of mood entries.
     *
     * @param  count        The number of mood entries to generate.
     * @throws SQLException If an error occurs.
     */
    private static void generateMoodEntries(int count) throws SQLException {

        String sql = "INSERT INTO mood_entries (mood_id, tag, entry_date_and_time) VALUES (?, ?, ?)";

        // Try connecting and inserting data into database with randomized dates and times (randomness may not show human trends, but helps with testing)
        try (Connection conn = DatabaseConnection.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();
            String[] tags = {"Work", "Family", null, "Friends", "Health", "Hobby", null, "Exercise", "Sleep", "Food", "Weather", null};

            for (int i = 0; i < count; i++) {

                int moodId = random.nextInt(15) + 1;
                String tag = tags[random.nextInt(tags.length)];
                LocalDateTime date = now.minusDays(random.nextInt(180))
                                        .withHour(random.nextInt(24))
                                        .withMinute(random.nextInt(60))
                                        .withSecond(random.nextInt(60));

                pstmt.setInt(1, moodId);
                pstmt.setString(2, tag);
                pstmt.setString(3, date.format(DB_DATE_FORMAT));
                pstmt.executeUpdate();
            }
        }
    }


    /**
     * Generates a specified number of exercise entries.
     *
     * @param  count        The number of exercise entries to generate.
     * @throws SQLException If an error occurs.
     */
    private static void generateExerciseEntries(int count) throws SQLException {

        String sql = "INSERT INTO exercise_entries (exercise_id, mood_before_id, mood_after_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";

        // Try connecting and inserting data into database with randomized dates and times (randomness may not show human trends, but helps with testing)
        try (Connection conn = DatabaseConnection.connect();  PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            for (int i = 0; i < count; i++) {
                int exerciseId = random.nextInt(3) + 1;  // Assuming 3 exercises with IDs 1-3
                int moodBeforeId = random.nextInt(15) + 1;  // Assuming 15 moods with IDs 1-15

                // Mood after is typically better than mood before for exercises
                int moodAfterId = Math.min(15, moodBeforeId + random.nextInt(5) + 1);

                // Generate a random date within the last 6 months
                LocalDateTime startTime = now.minusDays(random.nextInt(180))
                                             .withHour(random.nextInt(24))
                                             .withMinute(random.nextInt(60))
                                             .withSecond(random.nextInt(60));

                LocalDateTime endTime = startTime.plusMinutes(random.nextInt(20) + 5);  // 5-25 minutes range

                pstmt.setInt(1, exerciseId);
                pstmt.setInt(2, moodBeforeId);
                pstmt.setInt(3, moodAfterId);
                pstmt.setString(4, startTime.format(DB_DATE_FORMAT));
                pstmt.setString(5, endTime.format(DB_DATE_FORMAT));
                pstmt.executeUpdate();
            }
        }
    }
}