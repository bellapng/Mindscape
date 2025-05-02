package view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;
import java.sql.*;
import java.util.*;

import models.Exercise;
import models.Mood;
import controller.GuidedMeditationController;

/**
 * GuidedMeditationView class represents the GUI framework for the guided meditation feature within Mindscape.
 * @author Isabella Castillo
 */
public class GuidedMeditationView {

    // Creating necessary objects for use through this view file
    private final GuidedMeditationController controller = new GuidedMeditationController();
    private final VBox root = new VBox(20);
    private final Label title = new Label("Guided Meditation");
    private final HBox exerciseButtonContainer = new HBox(20);
    private List<Mood> moods = Collections.emptyList();
    private Exercise selectedExercise;


    /**
     * Creates the view and initializes the layout.
     */
    public GuidedMeditationView() {

        // Setting root and title styling
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e1f22;");
        title.getStyleClass().add("title-label");

        // Creating a card for the exercises to be on
        VBox cardContent = new VBox(15);
        cardContent.getChildren().add(exerciseButtonContainer);
        StackPane card = new StackPane(cardContent);
        card.getStyleClass().add("card");

        // Building the initial layout
        root.getChildren().addAll(title, card);
        loadExerciseList();

        // Loading in moods
        try {

            moods = controller.getMoodList();

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }


    /**
     * Loads exercise list and dynamically creates corresponding buttons to display them within the GUI.
     */
    private void loadExerciseList() {

        // Setting button specs
        exerciseButtonContainer.getChildren().clear();
        exerciseButtonContainer.setSpacing(20);
        exerciseButtonContainer.setAlignment(Pos.CENTER);
        exerciseButtonContainer.setPadding(new Insets(30, 0, 30, 0));
        exerciseButtonContainer.setFillHeight(true);
        HBox.setHgrow(exerciseButtonContainer, Priority.ALWAYS);

        try {

            // Creating box for titles
            List<Exercise> exercises = controller.getAllExercises();
            VBox titleBox = new VBox(10);
            titleBox.setAlignment(Pos.CENTER);

            // Creating subtitle label with styling
            Label subtitle = new Label("Select an exercise to begin");
            subtitle.getStyleClass().add("subtitle-label");
            subtitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

            root.getChildren().clear();
            root.getChildren().addAll(title, subtitle);

            // Adding the buttons to the container
            for (Exercise exercise : exercises) {
                exerciseButtonContainer.getChildren().add(createExerciseButton(exercise));
            }

            // Adding the button container to a card then to the root
            StackPane card = new StackPane(exerciseButtonContainer);
            card.getStyleClass().add("card");
            card.setMaxWidth(Double.MAX_VALUE);
            VBox.setVgrow(card, Priority.ALWAYS);
            root.getChildren().add(card);

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }


    /**
     * Creates a button representing an exercise with its name and description, along with proper styling.
     *
     * @param  exercise Specific exercise (of the 3 choices) that the button is for.
     * @return Button   Returns created exercise button.
     */
    private Button createExerciseButton(Exercise exercise) {

        // For displaying name and description of exercise
        VBox buttonContent = new VBox(10);
        buttonContent.setAlignment(Pos.CENTER);
        buttonContent.setMaxWidth(Double.MAX_VALUE);

        // Creating labels for name and description with styling
        Label nameLabel = new Label(exercise.getExerciseName());
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f0f0f0;");
        Label descLabel = new Label(exercise.getExerciseDescription());
        descLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #d0d0d0;");

        descLabel.setWrapText(true);
        descLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        descLabel.setMaxWidth(330);
        buttonContent.getChildren().addAll(nameLabel, descLabel);

        // Creating the button with the necessary content
        Button button = new Button();
        button.setGraphic(buttonContent);
        button.getStyleClass().add("exercise-button");
        button.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(button, Priority.ALWAYS);

        button.setOnAction(event -> showExerciseDetail(exercise));
        return button;
    }


    /**
     * Displays exercise details of the chosen exercise.
     *
     * @param exercise Specific exercise (of the 3 choices) that we are displaying the detail for.
     */
    private void showExerciseDetail(Exercise exercise) {

        this.selectedExercise = exercise;
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating back button
        Button back = new Button("← Back");
        back.getStyleClass().add("action-button");
        back.setOnAction(event -> { root.getChildren().clear(); root.getChildren().addAll(title, new StackPane(exerciseButtonContainer)); loadExerciseList(); });

        // Exercise title and description with styling
        VBox exerciseInfo = new VBox(15);
        Label name = new Label(exercise.getExerciseName());
        name.getStyleClass().add("subtitle-label");

        Label description = new Label(exercise.getExerciseDescription());
        description.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 16px;");
        description.setWrapText(true);

        exerciseInfo.getChildren().addAll(name, description);
        StackPane infoCard = new StackPane(exerciseInfo);
        infoCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Mood selectors in a card (initally can only select mood before, then after exercise, user can select mood after)
        VBox moodSelectors = new VBox(15);
        Label moodLabel = new Label("Track Your Mood");
        moodLabel.getStyleClass().add("subtitle-label");

        ComboBox<String> before = new ComboBox<>();
        before.setPromptText("How do you feel before?");
        before.setMaxWidth(Double.MAX_VALUE);

        // Populating moods
        for (Mood mood : moods) {
            before.getItems().add(mood.getMoodName());
        }

        moodSelectors.getChildren().addAll(moodLabel, before);
        StackPane moodCard = new StackPane(moodSelectors);
        moodCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating start and stop buttons
        Button start = new Button("Start Exercise");
        start.getStyleClass().addAll("action-button", "success-button");
        Button stop = new Button("Stop Exercise");
        stop.getStyleClass().addAll("action-button", "danger-button");
        stop.setDisable(true);
        start.setOnAction(event -> handleStart(before, start, stop));
        stop.setOnAction(event -> handleStop(start, stop));
        HBox actionBox = new HBox(15, start, stop);
        actionBox.setAlignment(Pos.CENTER);

        // Displaying
        detailPane.getChildren().addAll(back, infoCard, moodCard, actionBox);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, detailCard);

        // Fading in the detail pane
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1000), detailCard);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();
    }


    /**
     * Helper method for handling starting of exercise.
     *
     * @param  before Users mood before exercise (if empty, prompt with an animated shake to tell the user the field is empty).
     * @param  start  Start button in exercise detail screen.
     * @param  stop   Stop button in exercise detail screen.
     */
    private void handleStart(ComboBox<String> before, Button start, Button stop) {

        String moodName = before.getValue();

        // Grabbing attention to blank field upon attempted submission
        if (moodName == null) {

            before.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

            // Shake animation
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(before.translateXProperty(), 0)),
                                new KeyFrame(Duration.millis(50), new KeyValue(before.translateXProperty(), -5)),
                                new KeyFrame(Duration.millis(100), new KeyValue(before.translateXProperty(), 5)),
                                new KeyFrame(Duration.millis(150), new KeyValue(before.translateXProperty(), -5)),
                                new KeyFrame(Duration.millis(200), new KeyValue(before.translateXProperty(), 0)));
            timeline.setCycleCount(2);
            timeline.play();

            return;
        }

        // Resetting style upon valid selection
        before.setStyle("");
        int moodID = moods.stream().filter(mood -> mood.getMoodName().equals(moodName)).findFirst().map(Mood::getMoodID).orElse(-1);

        // Beginning exercise
        try {

            if (!controller.startExercise(selectedExercise.getExerciseID(), moodID)) {
                showAlert("Error: Could not start exercise.");
                return;
            }

            // Showing the exercise screen based on selection
            if (selectedExercise.getExerciseName().contains("Box Breathing")) {
                showBoxBreathingExercise(stop);
            } else if (selectedExercise.getExerciseName().contains("Deep Breathing")) {
                showDeepBreathingExercise(stop);
            } else if (selectedExercise.getExerciseName().contains("Progressive Muscle Relaxation")) {
                showProgressiveMuscleRelaxationExercise(stop);
            }

            start.setDisable(true);
            stop.setDisable(false);

        } catch (SQLException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }


    /**
     * Helper method for handling stopping of exercise.
     *
     * @param start Start button in exercise detail screen.
     * @param stop  Stop button in exercise detail screen.
     */
    private void handleStop(Button start, Button stop) {

        // Creating a screen to ask how they feel after the exercise
        VBox afterMoodPane = new VBox(20);
        afterMoodPane.setAlignment(Pos.CENTER);
        afterMoodPane.setPadding(new Insets(30));

        // Labels with styling
        Label completionTitle = new Label("Exercise Complete");
        completionTitle.getStyleClass().add("title-label");
        Label exerciseName = new Label(selectedExercise.getExerciseName());
        exerciseName.getStyleClass().add("subtitle-label");

        // Mood selector card with styling
        VBox moodSelectors = new VBox(15);
        Label moodLabel = new Label("How do you feel after the exercise?");
        moodLabel.getStyleClass().add("subtitle-label");
        ComboBox<String> moodCombo = new ComboBox<>();
        moodCombo.setPromptText("Select your mood");
        moodCombo.setMaxWidth(Double.MAX_VALUE);

        // Populating moods
        for (Mood mood : moods) {
            moodCombo.getItems().add(mood.getMoodName());
        }

        moodSelectors.getChildren().addAll(moodLabel, moodCombo);
        StackPane moodCard = new StackPane(moodSelectors);
        moodCard.getStyleClass().add("card");

        // Creating submit button
        Button submit = new Button("Submit");
        submit.getStyleClass().addAll("action-button", "primary-button");

        submit.setOnAction(event -> { String moodName = moodCombo.getValue();

            // Grabbing attention to blank field upon attempted submission
            if (moodName == null) {

                moodCombo.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

                // Shake animation
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(moodCombo.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(50), new KeyValue(moodCombo.translateXProperty(), -5)),
                        new KeyFrame(Duration.millis(100), new KeyValue(moodCombo.translateXProperty(), 5)),
                        new KeyFrame(Duration.millis(150), new KeyValue(moodCombo.translateXProperty(), -5)),
                        new KeyFrame(Duration.millis(200), new KeyValue(moodCombo.translateXProperty(), 0)));
                timeline.setCycleCount(2);
                timeline.play();

                return;
            }

            // Resetting style upon valid selection
            moodCombo.setStyle("");
            int moodID = moods.stream().filter(mood -> mood.getMoodName().equals(moodName)).findFirst().map(Mood::getMoodID).orElse(-1);

            try {

                if (!controller.stopExercise(moodID)) {
                    showAlert("Error: Could not stop exercise.");
                    return;
                }

                // Returning to the main guided meditation tab upon completion
                loadExerciseList();

            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            } });

        // Displaying
        afterMoodPane.getChildren().addAll(completionTitle, exerciseName, moodCard, submit);
        StackPane afterMoodCard = new StackPane(afterMoodPane);
        afterMoodCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, afterMoodCard);

        // Resetting button states
        if (start != null) { start.setDisable(false); }
        if (stop != null) { stop.setDisable(true); }
    }


    /**
     * Displays deep breathing exercise and calls necessary helper method(s).
     *
     * @param stopButton Button to stop exercise.
     */
    private void showDeepBreathingExercise(Button stopButton) {

        // Creating pane
        VBox exercisePane = new VBox(20);
        exercisePane.setAlignment(Pos.CENTER);
        exercisePane.setPadding(new Insets(30));

        // Creating title and instructions with styling
        Label exerciseTitle = new Label("Deep Breathing");
        exerciseTitle.getStyleClass().add("title-label");
        Label instructions = new Label("Inhale (4s) → Hold (7s) → Exhale (8s) - Follow the circle");
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: #E0E0E0; -fx-alignment: center;");
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Creating the container and circle
        StackPane animationContainer = new StackPane();
        animationContainer.setMinSize(300, 300);
        animationContainer.setMaxSize(300, 300);

        Circle breathingCircle = new Circle(50);
        breathingCircle.setFill(Color.TRANSPARENT);
        breathingCircle.setStroke(Color.LIGHTBLUE);
        breathingCircle.setStrokeWidth(3);

        // Creating labels with styling
        Label phaseLabel = new Label("Inhale");
        phaseLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        animationContainer.getChildren().addAll(breathingCircle, phaseLabel);

        Timeline breathingAnimation = createBreathingAnimation(breathingCircle, phaseLabel);

        // Adding timer for exercise
        Label timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");

        // Creating stop button with styling
        Button stop = new Button("Stop Exercise");
        stop.getStyleClass().addAll("action-button", "danger-button");

        // On click, stop animation and timer, then show selection for choosing the "mood after"
        stop.setOnAction(event -> { breathingAnimation.stop(); Timeline timer = (Timeline) timerLabel.getUserData(); if (timer != null) { timer.stop(); } handleStop(null, stopButton); });

        // Displaying
        exercisePane.getChildren().addAll(exerciseTitle, instructions, animationContainer, timerLabel, stop);
        StackPane exerciseCard = new StackPane(exercisePane);
        exerciseCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, exerciseCard);

        // Starting animation
        breathingAnimation.play();
        startTimer(timerLabel);
    }


    /**
     * Creates the breathing animation for the deep breathing exercise.
     * Helper for showDeepBreathingExercise method.
     *
     * @param  circle     Ring that expands and contracts.
     * @param  phaseLabel To indicate whether it is an inhale, hold, or exhale phase.
     * @return Timeline   Returns the animation.
     */
    private Timeline createBreathingAnimation(Circle circle, Label phaseLabel) {

        // Creating timeline for animation and setting dimensions
        Timeline timeline = new Timeline();
        double minRadius = 50;
        double maxRadius = 150;

        // Inhale phase (4s) ring expanding
        KeyValue inhaleRadiusKV = new KeyValue(circle.radiusProperty(), maxRadius, Interpolator.EASE_BOTH);
        KeyValue inhaleColorKV = new KeyValue(circle.strokeProperty(), Color.LIGHTBLUE, Interpolator.DISCRETE);
        KeyValue inhaleLabelKV = new KeyValue(phaseLabel.textProperty(), "Inhale", Interpolator.DISCRETE);

        KeyFrame inhaleStartKF = new KeyFrame(Duration.ZERO, inhaleColorKV, inhaleColorKV, inhaleLabelKV);
        KeyFrame inhaleEndKF = new KeyFrame(Duration.seconds(4), inhaleRadiusKV);

        // Hold phase (7s) ring stays static
        KeyValue holdRadiusKV = new KeyValue(circle.radiusProperty(), maxRadius, Interpolator.DISCRETE);
        KeyValue holdColorKV = new KeyValue(circle.strokeProperty(), Color.LIGHTPINK, Interpolator.DISCRETE);
        KeyValue holdLabelKV = new KeyValue(phaseLabel.textProperty(), "Hold", Interpolator.DISCRETE);

        KeyFrame holdStartKF = new KeyFrame(Duration.seconds(4.01), holdColorKV, holdLabelKV, holdRadiusKV);
        KeyFrame holdEndKF = new KeyFrame(Duration.seconds(11), holdRadiusKV);

        // Exhale phase (8s) ring contracting
        KeyValue exhaleRadiusKV = new KeyValue(circle.radiusProperty(), minRadius, Interpolator.EASE_BOTH);
        KeyValue exhaleColorKV = new KeyValue(circle.strokeProperty(), Color.LIGHTGREEN, Interpolator.DISCRETE);
        KeyValue exhaleLabelKV = new KeyValue(phaseLabel.textProperty(), "Exhale", Interpolator.DISCRETE);

        KeyFrame exhaleStartKF = new KeyFrame(Duration.seconds(11.01), exhaleColorKV, exhaleLabelKV);
        KeyFrame exhaleEndKF = new KeyFrame(Duration.seconds(19), exhaleRadiusKV);

        // Adding frames to timeline and displaying
        timeline.getKeyFrames().addAll(inhaleStartKF, inhaleEndKF, holdStartKF, holdEndKF, exhaleStartKF, exhaleEndKF);
        timeline.setCycleCount(Timeline.INDEFINITE);

        return timeline;
    }


    /**
     * Displays progressive muscle relaxation exercise and calls necessary helper method(s).
     *
     * @param stopButton Button to stop exercise.
     */
    private void showProgressiveMuscleRelaxationExercise(Button stopButton) {

        // Creating pane
        VBox exercisePane = new VBox(20);
        exercisePane.setAlignment(Pos.CENTER);
        exercisePane.setPadding(new Insets(30));

        // Creating title and instructions with styling
        Label exerciseTitle = new Label("Progressive Muscle Relaxation");
        exerciseTitle.getStyleClass().add("title-label");
        Label instructions = new Label("Tense each muscle group for 5 seconds, then relax for 25 seconds");
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: #E0E0E0; -fx-alignment: center;");
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Creating the box container
        VBox animationContainer = new VBox(15);
        animationContainer.setAlignment(Pos.CENTER);
        animationContainer.setPadding(new Insets(20));
        animationContainer.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 10px;");
        animationContainer.setMinHeight(300);

        // Creating labels with styling
        Label muscleGroupLabel = new Label("Starting soon...");
        muscleGroupLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        Label actionLabel = new Label("Get ready");
        actionLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        Label timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");
        animationContainer.getChildren().addAll(muscleGroupLabel, actionLabel, timerLabel);

        // Creating stop button with styling
        Button stop = new Button("Stop Exercise");
        stop.getStyleClass().addAll("action-button", "danger-button");

        // On click, stop animation and show selection for choosing the "mood after"
        stop.setOnAction(event -> { Timeline timeline = (Timeline) animationContainer.getUserData(); if (timeline != null) { timeline.stop(); } handleStop(null, stopButton); });

        // Displaying
        exercisePane.getChildren().addAll(exerciseTitle, instructions, animationContainer, stop);
        StackPane exerciseCard = new StackPane(exercisePane);
        exerciseCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, exerciseCard);

        // Starting the animation
        startProgressiveMuscleRelaxationSequence(animationContainer, muscleGroupLabel, actionLabel, timerLabel);
    }


    /**
     * Creates the PMR sequence for the PMR exercise.
     * Helper for showProgressiveMuscleRelaxationExercise.
     *
     * @param container        VBox where exercise sequence occurs.
     * @param muscleGroupLabel Label for different muscle groups user will be tensing/relaxing.
     * @param actionLabel      Label for the current action the user should take based on the phase/point in the sequence.
     * @param timerLabel       Label for the timer.
     */
    private void startProgressiveMuscleRelaxationSequence(VBox container, Label muscleGroupLabel, Label actionLabel, Label timerLabel) {

        // Different muscle groups for user to tense and relax
        String[] muscleGroups = { "Hands and Forearms", "Biceps", "Shoulders", "Neck", "Face", "Chest and Abdomen", "Thighs", "Calves", "Feet" };

        // Creating timeline
        Timeline timeline = new Timeline();

        // Creating an initial delay to prepare user for exercise
        KeyFrame startDelay = new KeyFrame(Duration.seconds(5), new KeyValue(muscleGroupLabel.textProperty(), "Get ready to begin", Interpolator.DISCRETE),
                new KeyValue(actionLabel.textProperty(), "Starting soon", Interpolator.DISCRETE));
        timeline.getKeyFrames().add(startDelay);

        // Current time tracker to ensure we follow 5s for tensing and 25s for relaxing
        double currTime = 5;

        // Display loop from tensing to relaxing
        for (String muscleGroup : muscleGroups) {

            // Tense phase (5s)
            KeyFrame tensePrepare = new KeyFrame(Duration.seconds(currTime), new KeyValue(muscleGroupLabel.textProperty(), muscleGroup, Interpolator.DISCRETE),
                    new KeyValue(actionLabel.textProperty(), "Get ready to tense", Interpolator.DISCRETE), new KeyValue(actionLabel.textFillProperty(), Color.BLACK, Interpolator.DISCRETE));
            currTime += 3;
            KeyFrame tenseStart = new KeyFrame(Duration.seconds(currTime), new KeyValue(actionLabel.textProperty(), "Tense", Interpolator.DISCRETE),
                    new KeyValue(actionLabel.textFillProperty(), Color.LIGHTPINK, Interpolator.DISCRETE));
            currTime += 5;

            // Relax phase (25s)
            KeyFrame relaxStart = new KeyFrame(Duration.seconds(currTime), new KeyValue(actionLabel.textProperty(), "Relax", Interpolator.DISCRETE),
                    new KeyValue(actionLabel.textFillProperty(), Color.LIGHTGREEN, Interpolator.DISCRETE));
            currTime += 25;

            timeline.getKeyFrames().addAll(tensePrepare, tenseStart, relaxStart);
        }

        // Ending sequence when done
        KeyFrame endSequence = new KeyFrame(Duration.seconds(currTime), new KeyValue(muscleGroupLabel.textProperty(), "Complete", Interpolator.DISCRETE),
                               new KeyValue(actionLabel.textProperty(), "Exercise Finished", Interpolator.DISCRETE), new KeyValue(actionLabel.textFillProperty(), Color.LIGHTBLUE, Interpolator.DISCRETE));
        timeline.getKeyFrames().add(endSequence);

        // Displaying
        container.setUserData(timeline);
        timeline.play();
        startTimer(timerLabel);
        timeline.setOnFinished(event -> { Timeline timer = (Timeline) timerLabel.getUserData(); if (timer != null) { timer.stop(); }});

    }


    /**
     * Displays box breathing exercise and calls necessary helper method(s).
     *
     * @param stopButton Button to stop exercise.
     */
    private void showBoxBreathingExercise(Button stopButton) {

        // Creating exercise pane
        VBox exercisePane = new VBox(20);
        exercisePane.setAlignment(Pos.CENTER);
        exercisePane.setPadding(new Insets(30));

        // Creating title and instructions with styling
        Label exerciseTitle = new Label("Box Breathing");
        exerciseTitle.getStyleClass().add("title-label");
        Label instructions = new Label("Inhale (4s) → Hold (4s) → Exhale (4s) → Hold (4s) - Follow the ball");
        instructions.setStyle("-fx-font-size: 16px; -fx-text-fill: #E0E0E0; -fx-alignment: center;");
        instructions.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Creating the box container and outline that a ball will rotate around every side 4s each
        StackPane boxContainer = new StackPane();
        boxContainer.setMinSize(300, 300);
        boxContainer.setMaxSize(300, 300);

        Rectangle boxOutline = new Rectangle(250, 250);
        boxOutline.setFill(Color.TRANSPARENT);
        boxOutline.setStroke(Color.LIGHTGRAY);
        boxOutline.setStrokeWidth(2);

        // Phase label with styling (to indicate inhale, hold, exhale)
        Label phaseLabel = new Label("Inhale");
        phaseLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");

        // Creating the moving ball
        Circle ball = new Circle(10, Color.LIGHTGREEN);
        boxContainer.getChildren().addAll(boxOutline, ball, phaseLabel);

        // Creating the animation path by calling helper method
        PathTransition pathTransition = createBoxPathAnimation(boxOutline, ball, phaseLabel);

        // Adding timer label
        Label timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #E0E0E0;");

        // Creating stop button with styling
        Button stop = new Button("Stop Exercise");
        stop.getStyleClass().addAll("action-button", "danger-button");

        // On click, stop animation and timer, then show selection for choosing the "mood after"
        stop.setOnAction(event -> { pathTransition.stop(); Timeline timer = (Timeline) timerLabel.getUserData(); if (timer != null) { timer.stop(); } handleStop(null, stopButton); });

        // Displaying
        exercisePane.getChildren().addAll(exerciseTitle, instructions, boxContainer, timerLabel, stop);
        StackPane exerciseCard = new StackPane(exercisePane);
        exerciseCard.getStyleClass().add("card");
        root.getChildren().clear();
        root.getChildren().addAll(title, exerciseCard);

        // Starting the animation and timer
        pathTransition.play();
        startTimer(timerLabel);
    }


    /**
     * Creates the box path animation for the box breathing exercise.
     * Helper for showBoxBreathingExercise.
     *
     * @param  box            Box that ball follows a path around.
     * @param  ball           Ball that travels around the box (4s each side to mimic box breathing)
     * @param  phaseLabel     To indicate whether it is an inhale, hold, or exhale phase.
     * @return PathTransition Returns the box animation.
     */
    private PathTransition createBoxPathAnimation(Rectangle box, Circle ball, Label phaseLabel) {

        // Creating path and finding dimensions/starting location
        Path path = new Path();
        double width = box.getWidth();
        double height = box.getHeight();
        double startX = -width/2;
        double startY = -height/2;

        // Creating pathing sequence
        path.getElements().add(new MoveTo(startX, startY));
        path.getElements().add(new LineTo(startX + width, startY));
        path.getElements().add(new LineTo(startX + width, startY + height));
        path.getElements().add(new LineTo(startX, startY + height));
        path.getElements().add(new LineTo(startX, startY));

        // Creating the path transition, 16s total for 4 per side
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.seconds(16));
        pathTransition.setPath(path);
        pathTransition.setNode(ball);
        pathTransition.setCycleCount(PathTransition.INDEFINITE);
        pathTransition.setInterpolator(Interpolator.LINEAR);

        // Creating timeline for dynamic phase labels (inhale, hold, exhale, hold, repeat)
        Timeline phaseTimeline = new Timeline(new KeyFrame(Duration.ZERO, event -> phaseLabel.setText("Inhale")), new KeyFrame(Duration.seconds(4), event -> phaseLabel.setText("Hold")),
                                 new KeyFrame(Duration.seconds(8), event -> phaseLabel.setText("Exhale")), new KeyFrame(Duration.seconds(12), event -> phaseLabel.setText("Hold")));
        phaseTimeline.setCycleCount(Timeline.INDEFINITE);
        phaseTimeline.play();

        // Updating ball color based on the phase
        Timeline colorTimeline = new Timeline(new KeyFrame(Duration.ZERO, event-> ball.setFill(Color.LIGHTBLUE)), new KeyFrame(Duration.seconds(4), event -> ball.setFill(Color.LIGHTPINK)),
                                 new KeyFrame(Duration.seconds(8), event -> ball.setFill(Color.LIGHTGREEN)), new KeyFrame(Duration.seconds(12), event -> ball.setFill(Color.LIGHTPINK)));
        colorTimeline.setCycleCount(Timeline.INDEFINITE);
        colorTimeline.play();

        pathTransition.setOnFinished(event -> { phaseTimeline.stop(); colorTimeline.stop(); });

        return pathTransition;
    }


    /**
     * Begins timer for exercise screens.
     *
     * @param timerLabel Label for the timer.
     */
    private void startTimer(Label timerLabel) {

        final long startTime = System.currentTimeMillis();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {

            long elapsedTime = System.currentTimeMillis() - startTime;
            long minutes = (elapsedTime / 1000) / 60;
            long seconds = (elapsedTime / 1000) % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, seconds)); }));

        timeline.setCycleCount(Animation.INDEFINITE);

        // Storing the time for later
        timerLabel.setUserData(timeline);
        timeline.play();
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
