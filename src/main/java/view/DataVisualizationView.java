package view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

import models.Mood;
import models.MoodEntry;
import models.Exercise;
import models.ExerciseEntry;
import controller.DataVisualizationController;

/**
 * DataVisualizationView class represents the GUI framework for the data visualization feature within Mindscape.
 *
 * @author Isabella Castillo
 */
public class DataVisualizationView {

    // Creating necessary objects for use through this view file
    private final DataVisualizationController controller = new DataVisualizationController();
    private final VBox root = new VBox(20);
    private final Label title = new Label("Data Visualization");
    private final HBox chartButtonContainer = new HBox(20);

    private LocalDateTime currStartDate;
    private LocalDateTime currEndDate;
    private String currTimeRange = "1M";
    private Tooltip tooltip = new Tooltip();


    /**
     * Creates the view and initializes the layout.
     */
    public DataVisualizationView() {

        // Setting root and title styling
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e1f22;");
        title.getStyleClass().add("title-label");

        // Creating a card for the chart buttons to be on
        VBox cardContent = new VBox(15);
        cardContent.getChildren().add(chartButtonContainer);
        StackPane card = new StackPane(cardContent);
        card.getStyleClass().add("card");

        // Building the initial layout by loading in the button options
        root.getChildren().addAll(title, card);
        loadChartButtons();

        // Tooltip config (for hovering over data points)
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #333333; -fx-text-fill: white;");
        tooltip.setShowDelay(Duration.millis(100));
        tooltip.setHideDelay(Duration.millis(200));
    }


    /**
     * Loads chart buttons for the different visualization options (user selects 1 of 4).
     */
    private void loadChartButtons() {

        // Creating buttons for different visualization options
        chartButtonContainer.getChildren().clear();
        chartButtonContainer.setSpacing(20);
        chartButtonContainer.setAlignment(Pos.CENTER);
        chartButtonContainer.setPadding(new Insets(30, 0, 30, 0));
        chartButtonContainer.setFillHeight(true);
        HBox.setHgrow(chartButtonContainer, Priority.ALWAYS);

        // Creating subtitle label with styling
        Label subtitle = new Label("Select a visualization to begin");
        subtitle.getStyleClass().add("subtitle-label");
        subtitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        root.getChildren().clear();
        root.getChildren().addAll(title, subtitle);

        // Listing chart name and description options for the buttons
        String[][] chartOptions = {

                {"Mood Over Time", "A line chart displaying your mood ratings over time. See how your mood has changed over the course of days, weeks, or months."},
                {"Exercise Effectiveness", "A bar chart showing the effectiveness of specific exercises by comparing mood ratings before and after each exercise type."},
                {"Mood Distribution", "A pie chart showing the proportion of different moods you've experienced. Discover which moods are most common for you."},
                {"Mood Variation", "A stacked bar chart exploring how your mood shifts between morning and evening. Identify patterns in your daily mood cycles."}
        };

        // Creating chart buttons for each name and description option
        for (String[] chartOption : chartOptions) {

            // Creating chart button for specific option
            String chartName = chartOption[0];
            String chartDescription = chartOption[1];
            VBox buttonContent = new VBox(10);
            buttonContent.setAlignment(Pos.CENTER);
            buttonContent.setMaxWidth(Double.MAX_VALUE);

            // Creating labels for name and description with styling
            Label nameLabel = new Label(chartName);
            nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f0f0f0;");
            Label descLabel = new Label(chartDescription);
            descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d0d0d0;");
            descLabel.setWrapText(true);
            descLabel.setTextAlignment(TextAlignment.CENTER);
            descLabel.setMaxWidth(330);
            buttonContent.getChildren().addAll(nameLabel, descLabel);

            // Creating the button config
            Button button = new Button();
            button.setGraphic(buttonContent);
            button.getStyleClass().add("exercise-button");
            button.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(button, Priority.ALWAYS);

            // Swapping to view screen based on what button was hit
            switch (chartName) {

                case "Mood Over Time":
                    button.setOnAction(event -> showMoodOverTimeChart());
                    break;

                case "Exercise Effectiveness":
                    button.setOnAction(event -> showExerciseEffectivenessChart());
                    break;

                case "Mood Distribution":
                    button.setOnAction(event -> showMoodDistributionChart());
                    break;

                case "Mood Variation":
                    button.setOnAction(event -> showMoodVariationChart());
                    break;
            }
            chartButtonContainer.getChildren().add(button);
        }

        // Displaying
        StackPane card = new StackPane(chartButtonContainer);
        card.getStyleClass().add("card");
        card.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(card, Priority.ALWAYS);
        root.getChildren().add(card);
    }


    /**
     * Creates time range selection buttons in the chart view (1W, 1M, 3M, 6M).
     * 
     * @param  updateAction The action performed when a time range is selected.
     * @return HBox         Returns an HBox with time range selection buttons.
     */
    private HBox createTimeRangeButtons(Runnable updateAction) {

        // Creating buttons for different time ranges available
        ToggleGroup timeRangeGroup = new ToggleGroup();

        ToggleButton oneWeekButton = new ToggleButton("1W");
        oneWeekButton.setToggleGroup(timeRangeGroup);
        oneWeekButton.getStyleClass().add("action-button");

        ToggleButton oneMonthButton = new ToggleButton("1M");
        oneMonthButton.setToggleGroup(timeRangeGroup);
        oneMonthButton.getStyleClass().add("action-button");

        ToggleButton threeMonthsButton = new ToggleButton("3M");
        threeMonthsButton.setToggleGroup(timeRangeGroup);
        threeMonthsButton.getStyleClass().add("action-button");

        ToggleButton sixMonthsButton = new ToggleButton("6M");
        sixMonthsButton.setToggleGroup(timeRangeGroup);
        sixMonthsButton.getStyleClass().add("action-button");
        String selectedStyle = "-fx-border-color: white; -fx-border-width: 1px;";

        // Swapping the selected button based on currentTimeRange chosen
        switch (currTimeRange) {

            case "1W":
                oneWeekButton.setSelected(true);
                oneWeekButton.setStyle(selectedStyle);
                break;

            case "1M":
                oneMonthButton.setSelected(true);
                oneMonthButton.setStyle(selectedStyle);
                break;

            case "3M":
                threeMonthsButton.setSelected(true);
                threeMonthsButton.setStyle(selectedStyle);
                break;

            case "6M":
                sixMonthsButton.setSelected(true);
                sixMonthsButton.setStyle(selectedStyle);
                break;
        }

        // Setting up button actions based on the time range selected by calling updateDateRange and setting as active button
        oneWeekButton.setOnAction(event -> {

            updateDateRange("1W");
            resetButtonStyles(oneWeekButton, oneMonthButton, threeMonthsButton, sixMonthsButton);
            oneWeekButton.setStyle(selectedStyle);
            updateAction.run();
        });

        oneMonthButton.setOnAction(event -> {

            updateDateRange("1M");
            resetButtonStyles(oneWeekButton, oneMonthButton, threeMonthsButton, sixMonthsButton);
            oneMonthButton.setStyle(selectedStyle);
            updateAction.run();
        });

        threeMonthsButton.setOnAction(event -> {

            updateDateRange("3M");
            resetButtonStyles(oneWeekButton, oneMonthButton, threeMonthsButton, sixMonthsButton);
            threeMonthsButton.setStyle(selectedStyle);
            updateAction.run();
        });

        sixMonthsButton.setOnAction(event -> {

            updateDateRange("6M");
            resetButtonStyles(oneWeekButton, oneMonthButton, threeMonthsButton, sixMonthsButton);
            sixMonthsButton.setStyle(selectedStyle);
            updateAction.run();
        });

        // Displaying
        HBox timeRangeButtons = new HBox(10, oneWeekButton, oneMonthButton, threeMonthsButton, sixMonthsButton);
        timeRangeButtons.setAlignment(Pos.CENTER);
        return timeRangeButtons;
    }


    /**
     * Updates the current date range for data based on the selected time range from the user.
     *
     * @param timeRange The time range to set for data retrieval (1W, 1M, 3M, 6M).
     */
    private void updateDateRange(String timeRange) {

        currTimeRange = timeRange;
        LocalDateTime currTime = LocalDateTime.now();

        // Switching based on user selection (default to 1 month of data shown upon initial launch)
        switch (timeRange) {

            case "1W":
                currStartDate = currTime.minusWeeks(1);
                break;

            case "1M":
                currStartDate = currTime.minusMonths(1);
                break;

            case "3M":
                currStartDate = currTime.minusMonths(3);
                break;

            case "6M":
                currStartDate = currTime.minusMonths(6);
                break;

            default:
                currStartDate = currTime.minusMonths(1);
        }
        currEndDate = currTime;
    }


    /**
     * Creates a line chart showing mood over time.
     *
     * @return LineChart Returns the created chart.
     */
    private LineChart<String, Number> createMoodOverTimeChart() {

        // Creating chart axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-font-size: 14px;");
        NumberAxis yAxis = new NumberAxis(0, 16, 1);
        yAxis.setStyle("-fx-font-size: 14px;");

        // Creating the line chart
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(false);
        lineChart.setPrefHeight(700);
        lineChart.setPrefWidth(600);
        lineChart.setPadding(new Insets(0, 30, 10, 30));

        // Attempting to access database via controller for info based on date range
        try {

            // Getting mood entries in range (ensuring sorted by date)
            List<MoodEntry> entries = controller.getMoodEntriesByDateRange(currStartDate, currEndDate);
            if (entries.isEmpty()) { return lineChart; }
            entries.sort(Comparator.comparing(MoodEntry::getDateAndTime));

            // Creating data series
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Mood");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd");

            // Getting all moods for y-axis labels (1-15) and putting in hash map
            List<Mood> allMoods = controller.getMoodList();
            Map<Integer, String> moodNames = new HashMap<>();
            for (Mood mood : allMoods) {

                moodNames.put(mood.getMoodID(), mood.getMoodName());
            }

            // Setting custom y-axis tick labels to show mood names instead of integer values via override of default formatter
            yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {

                @Override
                public String toString(Number object) {

                    int moodId = object.intValue();
                    if (moodId >= 1 && moodId <= 15 && moodNames.containsKey(moodId)) {
                        return moodNames.get(moodId);
                    }
                    return "";
                }
            });

            // Adding all data points
            Map<Integer, Mood> moodCache = new HashMap<>();
            for (MoodEntry entry : entries) {

                // Getting mood name and caching
                Mood mood;
                if (moodCache.containsKey(entry.getMoodID())) {
                    mood = moodCache.get(entry.getMoodID());
                } else {
                    mood = controller.getMoodByID(entry.getMoodID());
                    moodCache.put(entry.getMoodID(), mood);
                }

                // Creating singular data point w hover
                String dateStr = entry.getDateAndTime().format(formatter);
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(dateStr, entry.getMoodID());
                dataPoint.setNode(new HBox());

                dataPoint.getNode().setOnMouseEntered(event -> {

                    dataPoint.getNode().setStyle("-fx-background-color: rgba(255,255,255, 0.7); -fx-padding: 5;");
                    showTooltipMood(event, mood.getMoodName(), entry);
                });

                dataPoint.getNode().setOnMouseExited(event -> { dataPoint.getNode().setStyle(""); tooltip.hide(); });
                series.getData().add(dataPoint);
            }

            // Adding data series and set styling
            lineChart.getData().add(series);
            lineChart.lookupAll(".series0").forEach(node -> node.setStyle("-fx-stroke: #768894; -fx-stroke-width: 2px;"));
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
        return lineChart;
    }


    /**
     * Displays the created line chart via createMoodOverTimeChart().
     */
    private void showMoodOverTimeChart() {

        // Defaulting display to 1M
        if (currStartDate == null) { updateDateRange("1M"); }

        // Creating pane
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> loadChartButtons());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(0));

        // Creating chart content and adding to card
        VBox chartContent = new VBox(15);
        chartContent.setAlignment(Pos.CENTER);
        Label chartTitle = new Label("Mood Over Time");
        chartTitle.getStyleClass().add("subtitle-label");
        LineChart<String, Number> lineChart = createMoodOverTimeChart();

        chartContent.getChildren().addAll(chartTitle, lineChart);
        StackPane chartCard = new StackPane(chartContent);
        chartCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating the time range buttons
        HBox timeRangeButtons = createTimeRangeButtons(() -> {

            LineChart<String, Number> updatedChart = createMoodOverTimeChart();
            chartContent.getChildren().set(1, updatedChart);
        });

        // Displaying
        detailPane.getChildren().addAll(backButtonContainer, chartCard, timeRangeButtons);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, detailCard);

        // Fade in transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), detailCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    /**
     * Creates a bar chart showing mood before and after exercises.
     * 
     * @return BarChart Returns the created chart.
     */
    private BarChart<String, Number> createExerciseEffectivenessChart() {

        // Creating chart axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-font-size: 14px;");
        NumberAxis yAxis = new NumberAxis(1, 16, 1);
        yAxis.setStyle("-fx-font-size: 14px;");

        // Creating bar chart
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setAnimated(false);
        barChart.setLegendVisible(true);
        barChart.setPrefHeight(700);
        barChart.setPrefWidth(600);
        barChart.setPadding(new Insets(0, 30, 10, 30));

        // Attempting to access database via controller for info based on date range
        try {

            // Getting exercise entries in range
            List<ExerciseEntry> entries = controller.getExerciseEntriesByDateRange(currStartDate, currEndDate);
            if (entries.isEmpty()) { return barChart; }

            // Getting all exercises (for names)
            List<Exercise> exercises = controller.getAllExercises();
            Map<Integer, String> exerciseNames = new HashMap<>();
            for (Exercise exercise : exercises) {

                exerciseNames.put(exercise.getExerciseID(), exercise.getExerciseName());
            }

            // Getting all moods for y-axis labels (1-15) and putting in hash map
            List<Mood> allMoods = controller.getMoodList();
            Map<Integer, String> moodNames = new HashMap<>();
            for (Mood mood : allMoods) {
                moodNames.put(mood.getMoodID(), mood.getMoodName());
            }

            // Setting custom y-axis tick labels to show mood names instead of integer values via override of default formatter
            yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {

                @Override
                public String toString(Number object) {
                    int moodId = object.intValue();
                    if (moodId >= 1 && moodId <= 15 && moodNames.containsKey(moodId)) {
                        return moodNames.get(moodId);
                    }
                    return "";
                }
            });

            // Grouping entries by exercise type for distribution
            Map<Integer, List<ExerciseEntry>> entriesByExercise = new HashMap<>();
            for (ExerciseEntry entry : entries) {
                entriesByExercise.computeIfAbsent(entry.getExerciseID(), i -> new ArrayList<>()).add(entry);
            }

            // Creating a data series for before and after exercise moods
            XYChart.Series<String, Number> beforeSeries = new XYChart.Series<>();
            beforeSeries.setName("Before Exercise");
            XYChart.Series<String, Number> afterSeries = new XYChart.Series<>();
            afterSeries.setName("After Exercise");

            // Calculating the avg mood ratings for each exercise using the mood ids
            for (Map.Entry<Integer, List<ExerciseEntry>> exerciseEntry : entriesByExercise.entrySet()) {

                int exerciseId = exerciseEntry.getKey();
                List<ExerciseEntry> exerciseEntries = exerciseEntry.getValue();
                String exerciseName = exerciseNames.getOrDefault(exerciseId, "Unknown Exercise");

                // Calculating the avgs and adding data points to series
                double beforeAvg = exerciseEntries.stream().mapToInt(ExerciseEntry::getMoodBeforeID).average().orElse(0);
                double afterAvg = exerciseEntries.stream().mapToInt(ExerciseEntry::getMoodAfterID).average().orElse(0);
                XYChart.Data<String, Number> beforeData = new XYChart.Data<>(exerciseName, beforeAvg);
                XYChart.Data<String, Number> afterData = new XYChart.Data<>(exerciseName, afterAvg);


                // Mouse hover interactions for the before data series
                beforeData.setNode(new HBox());
                beforeData.getNode().setOnMouseEntered(event -> {

                    beforeData.getNode().setStyle("-fx-background-color: #768894; -fx-opacity: 0.7;  -fx-padding: 5;");
                    showTooltip(event, "Before " + exerciseName, String.format("Average Mood: %.1f\nSessions: %d", beforeAvg, exerciseEntries.size())); });

                beforeData.getNode().setOnMouseExited(event -> {

                    beforeData.getNode().setStyle("");
                    tooltip.hide(); });

                // Mouse hover interactions for the after data series
                afterData.setNode(new HBox());
                afterData.getNode().setOnMouseEntered(event -> {

                    afterData.getNode().setStyle("-fx-background-color: #738265; -fx-opacity: 0.7; -fx-padding: 5;");
                    showTooltip(event, "After " + exerciseName, String.format("Average Mood: %.1f\nSessions: %d", afterAvg, exerciseEntries.size())); });

                afterData.getNode().setOnMouseExited(event -> {

                    afterData.getNode().setStyle("");
                    tooltip.hide(); });

                // Adding data series
                beforeSeries.getData().add(beforeData);
                afterSeries.getData().add(afterData);
            }

            barChart.getData().addAll(beforeSeries, afterSeries);

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
        return barChart;
    }


    /**
     * Displays the created bar chart via createExerciseEffectivenessChart().
     */
    private void showExerciseEffectivenessChart() {

        // Defaulting display to 1M
        if (currStartDate == null) { updateDateRange("1M"); }

        // Creating detail pane
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> loadChartButtons());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(0));

        // Creating chart content and adding to card
        VBox chartContent = new VBox(15);
        chartContent.setAlignment(Pos.CENTER);
        Label chartTitle = new Label("Exercise Effectiveness");
        chartTitle.getStyleClass().add("subtitle-label");
        BarChart<String, Number> barChart = createExerciseEffectivenessChart();

        chartContent.getChildren().addAll(chartTitle, barChart);
        StackPane chartCard = new StackPane(chartContent);
        chartCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating the time range buttons and updating when chart changes (ie newly added data)
        HBox timeRangeButtons = createTimeRangeButtons(() -> {

            BarChart<String, Number> updatedChart = createExerciseEffectivenessChart();
            chartContent.getChildren().set(1, updatedChart);
        });

        // Displaying
        detailPane.getChildren().addAll(backButtonContainer, chartCard, timeRangeButtons);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, detailCard);

        // Fade in transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), detailCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    /**
     * Creates a pie chart showing mood distribution.
     * 
     * @return PieChart Returns the created chart.
     */
    private PieChart createMoodDistributionChart() {

        // Creating the pie chart
        PieChart pieChart = new PieChart();
        pieChart.setTitle("");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(false);
        pieChart.setAnimated(false);

        pieChart.setPrefHeight(700);
        pieChart.setPrefWidth(600);
        pieChart.setPadding(new Insets(0, 30, 10, 30));
        pieChart.setLabelLineLength(30);

        // Attempting to access database via controller for info based on date range
        try {

            // Getting mood entries in range
            List<MoodEntry> entries = controller.getMoodEntriesByDateRange(currStartDate, currEndDate);
            if (entries.isEmpty()) { return pieChart; }

            // Count occurrences of each mood w hash map and get names
            Map<Integer, Integer> moodCounts = new HashMap<>();
            for (MoodEntry entry : entries) {

                moodCounts.put(entry.getMoodID(), moodCounts.getOrDefault(entry.getMoodID(), 0) + 1);
            }

            Map<Integer, Mood> moodMap = new HashMap<>();
            for (Mood mood : controller.getMoodList()) {

                moodMap.put(mood.getMoodID(), mood);
            }

            String[] colors = { "#d6cfc4", "#a8b2a1", "#a3b0b9", "#c1b6aa", "#9aa78d", "#8c9ca9", "#b8a99a", "#859374", "#768894",
                                "#a89f96", "#738265", "#617482", "#9c9186", "#5f6e52", "#4d5f6d" };

            // Creating pie slices
            int colorIndex = 0;
            for (Map.Entry<Integer, Integer> entry : moodCounts.entrySet()) {

                int moodId = entry.getKey();
                int count = entry.getValue();
                Mood mood = moodMap.get(moodId);
                String moodName = mood != null ? mood.getMoodName() : "Unknown";

                // Create pie slice w %
                double percentage = (double) count / entries.size() * 100;
                PieChart.Data slice = new PieChart.Data(String.format("%s (%.1f%%)", moodName, percentage), count);
                final int currentColorIndex = colorIndex;

                // Setting up mouse interactions and coloring for slices
                slice.nodeProperty().addListener((obs, oldNode, newNode) -> {

                    if (newNode != null) {

                        // Setting slice color
                        String color = colors[Math.min(currentColorIndex, colors.length - 1)];
                        newNode.setStyle("-fx-pie-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");

                        // Adding hover effect
                        newNode.setOnMouseEntered(event -> {
                            newNode.setStyle("-fx-pie-color: " + color + "; -fx-opacity: 0.8; -fx-border-color: white; -fx-border-width: 3; -fx-text-fill: white; -fx-font-weight: bold;");
                            showTooltip(event, moodName, String.format("Count: %d\nPercentage: %.1f%%", count, percentage)); });

                        newNode.setOnMouseExited(event -> {
                            newNode.setStyle("-fx-pie-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold;");
                            tooltip.hide(); });
                    }
                });

                // Adding data and going to next color
                pieChart.getData().add(slice);
                colorIndex++;
            }
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
        return pieChart;
    }


    /**
     * Displays a pie chart showing mood distribution.
     */
    private void showMoodDistributionChart() {

        // Defaulting display to 1M
        if (currStartDate == null) { updateDateRange("1M"); }

        // Creating detail pane
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> loadChartButtons());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(0));

        // Creating chart content and adding to card
        VBox chartContent = new VBox(15);
        chartContent.setAlignment(Pos.CENTER);
        Label chartTitle = new Label("Mood Distribution");
        chartTitle.getStyleClass().add("subtitle-label");
        PieChart pieChart = createMoodDistributionChart();

        chartContent.getChildren().addAll(chartTitle, pieChart);
        StackPane chartCard = new StackPane(chartContent);
        chartCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating the time range buttons and updating when chart changes (ie newly added data)
        HBox timeRangeButtons = createTimeRangeButtons(() -> {

            PieChart updatedChart = createMoodDistributionChart();
            chartContent.getChildren().set(1, updatedChart);
        });

        // Displaying
        detailPane.getChildren().addAll(backButtonContainer, chartCard, timeRangeButtons);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, detailCard);

        // Fade in transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), detailCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    /**
     * Creates a stacked bar chart showing mood variation between morning and evening.
     * 
     * @return StackedBarChart Returns the created chart.
     */
    private StackedBarChart<String, Number> createMoodVariationChart() {

        // Creating chart axes
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setStyle("-fx-font-size: 14px;");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setStyle("-fx-font-size: 14px;");

        // Creating stacked bar chart
        StackedBarChart<String, Number> barChart = new StackedBarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setAnimated(false);
        barChart.setLegendVisible(true);
        barChart.setPrefHeight(700);
        barChart.setPrefWidth(600);

        // Attempting to access database via controller for info based on date range
        try {

            // Getting mood entries in range
            List<MoodEntry> entries = controller.getMoodEntriesByDateRange(currStartDate, currEndDate);
            if (entries.isEmpty()) { return barChart; }

            // Getting all moods (for names)
            List<Mood> moods = controller.getMoodList();
            Map<Integer, String> moodNames = new HashMap<>();
            for (Mood mood : moods) {

                moodNames.put(mood.getMoodID(), mood.getMoodName());
            }

            // Grouping moods into categories based on id ordering
            Map<String, List<Integer>> moodCategories = new LinkedHashMap<>();
            moodCategories.put("Hopeless - Anxious", Arrays.asList(1, 2, 3, 4, 5));
            moodCategories.put("Distracted - Tired", Arrays.asList(6, 7, 8, 9, 10));
            moodCategories.put("Hopeful - Elated", Arrays.asList(11, 12, 13, 14, 15));

            // Creating data series for day and night
            XYChart.Series<String, Number> morningSeries = new XYChart.Series<>();
            morningSeries.setName("Day (6AM-6PM)");
            XYChart.Series<String, Number> eveningSeries = new XYChart.Series<>();
            eveningSeries.setName("Night (6PM-6AM)");

            // Counting moods by category and time of day
            for (Map.Entry<String, List<Integer>> category : moodCategories.entrySet()) {

                String categoryName = category.getKey();
                List<Integer> moodIds = category.getValue();
                int morningCount = 0;
                int eveningCount = 0;

                for (MoodEntry entry : entries) {

                    if (moodIds.contains(entry.getMoodID())) {

                        LocalDateTime time = entry.getDateAndTime();
                        int hour = time.getHour();
                        if (hour >= 6 && hour < 18) { morningCount++; } else { eveningCount++; }
                    }
                }

                // Adding data points
                final int finalMorningCount = morningCount;
                final int finalEveningCount = eveningCount;
                final int totalEntries = entries.size();
                XYChart.Data<String, Number> morningData = new XYChart.Data<>(categoryName, finalMorningCount);
                XYChart.Data<String, Number> eveningData = new XYChart.Data<>(categoryName, finalEveningCount);

                // Adding mouse hover interacting for the morning data series
                morningData.setNode(new HBox());
                morningData.getNode().setOnMouseEntered(event -> {

                    morningData.getNode().setStyle("-fx-background-color: #768894; -fx-opacity: 0.7;  -fx-padding: 5;");
                    showTooltip(event, "Day: " + categoryName, String.format("Count: %d\nPercentage: %.1f%%", finalMorningCount, totalEntries == 0 ? 0 : (double) finalMorningCount / totalEntries * 100));});

                morningData.getNode().setOnMouseExited(event -> {
                    morningData.getNode().setStyle("");
                    tooltip.hide(); });

                // Adding mouse hover interacting for the evening data series
                eveningData.setNode(new HBox());
                eveningData.getNode().setOnMouseEntered(event -> {

                    eveningData.getNode().setStyle("-fx-background-color: #738265; -fx-opacity: 0.7; -fx-padding: 5;");
                    showTooltip(event, "Night: " + categoryName, String.format("Count: %d\nPercentage: %.1f%%", finalEveningCount, totalEntries == 0 ? 0 : (double) finalEveningCount / totalEntries * 100)); });

                eveningData.getNode().setOnMouseExited(event -> {

                    eveningData.getNode().setStyle("");
                    tooltip.hide();});

                // Adding data series
                morningSeries.getData().add(morningData);
                eveningSeries.getData().add(eveningData);
            }

            barChart.getData().addAll(morningSeries, eveningSeries);

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
        return barChart;
    }


    /**
     * Displays a stacked bar chart showing mood variation between morning and evening.
     */
    private void showMoodVariationChart() {

        // Defaulting display to 1M
        if (currStartDate == null) { updateDateRange("1M"); }

        // Creating detail pane
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating back button
        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> loadChartButtons());
        HBox backButtonContainer = new HBox(backButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(0));

        // Creating and adding chart content
        VBox chartContent = new VBox(15);
        chartContent.setAlignment(Pos.CENTER);
        Label chartTitle = new Label("Mood Variation");
        chartTitle.getStyleClass().add("subtitle-label");
        StackedBarChart<String, Number> barChart = createMoodVariationChart();

        chartContent.getChildren().addAll(chartTitle, barChart);
        StackPane chartCard = new StackPane(chartContent);
        chartCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating the time range buttons and updating when chart changes (ie newly added data)
        HBox timeRangeButtons = createTimeRangeButtons(() -> {

            StackedBarChart<String, Number> updatedChart = createMoodVariationChart();
            chartContent.getChildren().set(1, updatedChart);
        });

        // Displaying
        detailPane.getChildren().addAll(backButtonContainer, chartCard, timeRangeButtons);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, detailCard);

        // Fade in transition
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), detailCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    /**
     * Shows tooltip with information about a general data point.
     *
     * @param event   The mouse event (hover).
     * @param title   The tooltips title.
     * @param content The tooltips content.
     */
    private void showTooltip(MouseEvent event, String title, String content) {

        tooltip.setText(title + "\n" + content);
        tooltip.show(((Node) event.getSource()).getScene().getWindow(), event.getScreenX() + 10, event.getScreenY() + 10);
    }


    /**
     * Shows tooltip with information about a mood entry (different from other data points).
     * Primarily for line charts.
     *
     * @param event The mouse event (hover).
     * @param title The tooltips title.
     * @param entry The mood entry object.
     */
    private void showTooltipMood(MouseEvent event, String title, MoodEntry entry) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String content = "Date: " + entry.getDateAndTime().format(formatter);
        if (entry.getTag() != null && !entry.getTag().isEmpty()) { content += "\nTag: " + entry.getTag(); }
        showTooltip(event, title, content);
    }


    /**
     * Resets the style of the time range buttons (variable arg amount depending on what we need to reset).
     * Helper method for switching the appearance of the selected button (selected has a outline around it).
     *
     * @param buttons The button(s) to reset.
     */
    private void resetButtonStyles(ToggleButton... buttons) {

        for (ToggleButton button : buttons) {
            button.setStyle("");
        }
    }


    /**
     * Displays an alert with the given message (more for program function errors).
     *
     * @param alert Alert message.
     */
    private void showAlert(String alert) {

        // Pop up since its more of an application error rather than a failing to enter information error where we highlight the missing field in red
        Alert alertDialog = new Alert(Alert.AlertType.INFORMATION, alert, ButtonType.OK);
        DialogPane dialogPane = alertDialog.getDialogPane();
        dialogPane.getStyleClass().add("card");
        alertDialog.showAndWait();
    }


    /**
     * Returns the root node of the view for displaying in app/Main.java.
     *
     * @return Node Returns scene for displaying in app.
     */
    public Node getView() { return root; }
}
