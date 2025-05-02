package view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

import models.Mood;
import models.MoodEntry;
import controller.MoodTrackerController;

/**
 * MoodTrackerView class represents the GUI framework for the mood tracking feature within Mindscape.
 * @author Isabella Castillo
 */
public class MoodTrackerView {

    // Creating necessary objects for use through view file
    private final MoodTrackerController controller = new MoodTrackerController();
    private final VBox root = new VBox(20);
    private VBox moodInputPane;
    private VBox moodLogPane;

    private final Label title = new Label("Mood Tracker");
    private ComboBox<String> moodChoice;
    private TextField tagField;
    private Button submitButton;

    private List<Mood> moods;
    private TableView<MoodEntry> moodLogTable;
    private boolean showingMoodLog = false;
    private MoodEntry selectedEntry;


    /**
     * Creates the view and initializes the layout.
     */
    public MoodTrackerView() {

        // Setting root and title styling
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e1f22;");
        title.getStyleClass().add("title-label");

        // Initializing the mood input pane and mood logging pane
        initializeMoodInputPane();
        initializeMoodLogPane();

        // Building the initial layout
        root.getChildren().addAll(title, moodInputPane);
    }


    /**
     * Initializes the mood input pane.
     */
    private void initializeMoodInputPane() {

        // Setting main pane where action buttons will be
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(50, 0, 0, 0));

        // Creating mood input content (on top of detail pane)
        VBox moodInputContent = new VBox(15);
        moodInputContent.setPadding(new Insets(0, 20, 20, 20));

        // Creating subtitle, mood selection, and tag options with styling
        Label subtitle = new Label("Log Your Mood");
        subtitle.getStyleClass().add("subtitle-label");
        HBox subtitleBox = new HBox(subtitle);
        subtitleBox.setAlignment(Pos.CENTER);

        Label moodLabel = new Label("Select your mood:");
        moodLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");
        moodChoice = new ComboBox<>();
        moodChoice.setPromptText("How are you feeling?");
        moodChoice.setMaxWidth(Double.MAX_VALUE);

        Label tagLabel = new Label("Optional tag:");
        tagLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");
        tagField = new TextField();
        tagField.setPromptText("Add a tag to describe your mood (optional)");

        // Adding elements to card and styling
        moodInputContent.getChildren().addAll(subtitleBox, moodLabel, moodChoice, tagLabel, tagField);
        StackPane moodInputCard = new StackPane(moodInputContent);
        moodInputCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating buttons with styling and putting into actionBox
        submitButton = new Button("Submit Mood");
        submitButton.getStyleClass().addAll("action-button", "success-button");
        Button viewLogButton = new Button("View Mood Log");
        viewLogButton.getStyleClass().addAll("action-button", "primary-button");
        HBox actionBox = new HBox(15, submitButton, viewLogButton);
        actionBox.setAlignment(Pos.CENTER);

        // Displaying
        detailPane.getChildren().addAll(moodInputCard, actionBox);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        detailCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(detailCard, Priority.ALWAYS);
        moodInputPane = new VBox(detailCard);
        moodInputPane.setAlignment(Pos.CENTER);

        // Loading moods
        try {
            moods = controller.getMoodList();

            // Populating dropdown
            for (Mood mood : moods) {
                moodChoice.getItems().add(mood.getMoodName());
            }
        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }

        // Setting up actions for handling when user pressures "submit" and "view logs"
        submitButton.setOnAction(event -> handleSubmit());
        viewLogButton.setOnAction(event -> toggleMoodInputOrLogView());
    }


    /**
     * Initializes the mood log pane (for past logs) with a table view for displaying mood entries.
     */
    private void initializeMoodLogPane() {

        // Setting main pane where action buttons will be
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating mood log content (on top of detail pane)
        VBox moodLogContent = new VBox(15);
        moodLogContent.setAlignment(Pos.CENTER);
        moodLogContent.setPadding(new Insets(0, 20, 20, 20));

        // Creating subtitle and table view with styling
        Label subtitle = new Label("Your Mood Log");
        subtitle.getStyleClass().add("subtitle-label");
        moodLogTable = new TableView<>();
        moodLogTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        moodLogTable.setPlaceholder(new Label("No mood entries found"));
        moodLogTable.getStyleClass().add("mood-log-table");

        // Creating table "date" column and setting up how we should update the item
        TableColumn<MoodEntry, LocalDateTime> dateColumn = new TableColumn<>("Date & Time");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("dateAndTime"));
        dateColumn.setCellFactory(column -> new TableCell<>() {

            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            // Overriding the updateItem method in TableCell (JavaFX) so we can display in correct format and style (readable date time format)
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }

                setStyle("-fx-text-fill: #E0E0E0;");
            }});

        // Creating table "mood" column and setting up how we should update the mood item
        TableColumn<MoodEntry, Integer> moodColumn = new TableColumn<>("Mood");
        moodColumn.setCellValueFactory(new PropertyValueFactory<>("moodID"));
        moodColumn.setCellFactory(column -> new TableCell<>() {

            // Overriding the updateItem method in TableCell (JavaFX) so we can display in correct format and style (mood names not integers corresponding to them)
            @Override
            protected void updateItem(Integer item, boolean empty) {

                super.updateItem(item, empty);

                if (empty || item == null) {

                    setText(null);

                } else {

                    try {
                        Mood mood = controller.getMoodByID(item);
                        setText(mood != null ? mood.getMoodName() : "Unknown");
                    } catch (SQLException e) {
                        setText("Error");
                    }
                }

                setStyle("-fx-text-fill: #E0E0E0;");
            }});

        // Creating table "tag" column and setting up how we should update the tag item ead of "null")
        TableColumn<MoodEntry, String> tagColumn = new TableColumn<>("Tag");
        tagColumn.setCellValueFactory(new PropertyValueFactory<>("tag"));
        tagColumn.setCellFactory(column -> new TableCell<>() {

            // Overriding the updateItem method in TableCell (JavaFX) so we can display in correct format and style (no "null" if tag is empty)
            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                setText(empty || item == null ? "" : item);
                setStyle("-fx-text-fill: #E0E0E0;");
            }});

        // Adding mood log table and content and putting on card
        moodLogTable.getColumns().addAll(dateColumn, moodColumn, tagColumn);
        moodLogContent.getChildren().addAll(subtitle, moodLogTable);
        StackPane moodLogCard = new StackPane(moodLogContent);
        moodLogCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating '← Back' button at the top left
        Button backToMoodTrackerButton = new Button("← Back");
        backToMoodTrackerButton.getStyleClass().add("action-button");
        backToMoodTrackerButton.setOnAction(event -> toggleMoodInputOrLogView());
        HBox backButtonContainer = new HBox(backToMoodTrackerButton);
        backButtonContainer.setAlignment(Pos.TOP_LEFT);
        backButtonContainer.setPadding(new Insets(0));

        // Creating edit and delete buttons
        Button editButton = new Button("Edit");
        editButton.getStyleClass().addAll("action-button", "primary-button");
        editButton.setDisable(true);
        Button deleteButton = new Button("Delete");
        deleteButton.getStyleClass().addAll("action-button", "danger-button");
        deleteButton.setDisable(true);

        // Creating box for the edit and delete buttons
        HBox actionBox = new HBox(15, editButton, deleteButton);
        actionBox.setAlignment(Pos.CENTER);

        // Creating main card
        detailPane.getChildren().addAll(backButtonContainer, moodLogCard, actionBox);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        detailCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(detailCard, Priority.ALWAYS);
        moodLogPane = new VBox(detailCard);
        moodLogPane.setAlignment(Pos.CENTER);

        // Setting up actions for handling when user wants to edit, delete, or go back.
        moodLogTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            selectedEntry = newSelection;
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });

        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
    }


    /**
     * Toggles between the mood input view and the mood log view.
     */
    private void toggleMoodInputOrLogView() {

        if (showingMoodLog) {

            // Switching to mood input view with animation
            root.getChildren().remove(moodLogPane);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), moodInputPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            root.getChildren().add(moodInputPane);
            fadeIn.play();
            showingMoodLog = false;

        } else {

            // Switching to mood log view with animation (and call to update entries for dynamic updating)
            root.getChildren().remove(moodInputPane);
            loadMoodEntries();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), moodLogPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            root.getChildren().add(moodLogPane);
            fadeIn.play();
            showingMoodLog = true;
        }
    }


    /**
     * Handles the submission of a new mood entry.
     */
    private void handleSubmit() {

        String selected = moodChoice.getValue();

        // Grabbing attention to blank field upon attempted submission
        if (selected == null) {

            moodChoice.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

            // Shake animation
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(moodChoice.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(50), new KeyValue(moodChoice.translateXProperty(), -5)),
                    new KeyFrame(Duration.millis(100), new KeyValue(moodChoice.translateXProperty(), 5)),
                    new KeyFrame(Duration.millis(150), new KeyValue(moodChoice.translateXProperty(), -5)),
                    new KeyFrame(Duration.millis(200), new KeyValue(moodChoice.translateXProperty(), 0)));
            timeline.setCycleCount(2);
            timeline.play();

            return;
        }
        moodChoice.setStyle("");

        // Finding the mood ID from the name
        int moodID = -1;
        for (Mood mood : moods) {

            if (mood.getMoodName().equals(selected)) {
                moodID = mood.getMoodID();
                break;
            }
        }

        if (moodID == -1) { showAlert("Invalid mood selection."); return; }

        // Attempting to add new mood entry
        String tagText = tagField.getText().trim();
        MoodEntry entry = new MoodEntry(0, moodID, tagText, LocalDateTime.now());

        try {

            if (controller.insertMoodEntry(entry)) {

                showAlert("Mood logged successfully!");
                moodChoice.getSelectionModel().clearSelection();
                moodChoice.setValue(null);

                // Bug Fix: ComboBox wasnt updating back to its prompt so forcing the ComboBox to refresh its display
                moodChoice.setButtonCell(new ListCell<>() {

                    // Overriding updateItem method
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText("How are you feeling?");
                        } else {
                            setText(item);
                        }
                    }});

                tagField.clear();

            } else {
                showAlert("Failed to log mood.");
            }
        } catch (SQLException ex) {
            showAlert("Error submitting mood entry: " + ex.getMessage());
        }
    }

    /**
     * Handles the editing of a selected mood entry.
     */
    private void handleEdit() {

        if (selectedEntry == null) { return; }

        // Swapping back to input view and try to update
        toggleMoodInputOrLogView();

        try {

            Mood mood = controller.getMoodByID(selectedEntry.getMoodID());
            if (mood != null) { moodChoice.setValue(mood.getMoodName()); }
            tagField.setText(selectedEntry.getTag());

            // Changing submit button to update when editing an entry and handling the update
            submitButton.setText("Update Mood");
            submitButton.setOnAction(event -> {

                if (selectedEntry == null) { return; }

                // Grabbing attention to blank field upon attempted submission
                String selected = moodChoice.getValue();
                if (selected == null) {

                    moodChoice.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

                    // Shake animation
                    Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(moodChoice.translateXProperty(), 0)),
                            new KeyFrame(Duration.millis(50), new KeyValue(moodChoice.translateXProperty(), -5)),
                            new KeyFrame(Duration.millis(100), new KeyValue(moodChoice.translateXProperty(), 5)),
                            new KeyFrame(Duration.millis(150), new KeyValue(moodChoice.translateXProperty(), -5)),
                            new KeyFrame(Duration.millis(200), new KeyValue(moodChoice.translateXProperty(), 0)));
                    timeline.setCycleCount(2);
                    timeline.play();

                    return;
                }
                moodChoice.setStyle("");

                // Finding the mood ID from the name
                int moodID = -1;
                for (Mood moodCheck : moods) {
                    if (moodCheck.getMoodName().equals(selected)) {
                        moodID = moodCheck.getMoodID();
                        break;
                    }
                }

                if (moodID == -1) { showAlert("Invalid mood selection."); return; }

                // Attempting to update (display confirmation or failure)
                String tagText = tagField.getText().trim();
                MoodEntry updatedEntry = new MoodEntry(selectedEntry.getEntryID(), moodID, tagText, selectedEntry.getDateAndTime());

                try {

                    if (controller.updateMoodEntry(updatedEntry)) {

                        showAlert("Mood updated successfully!");
                        moodChoice.setValue(null);
                        tagField.clear();

                        // Resetting submit button back to how it was
                        submitButton.setText("Submit Mood");
                        submitButton.setOnAction(event2 -> handleSubmit());
                        selectedEntry = null;

                    } else {
                        showAlert("Failed to update mood.");
                    }
                } catch (SQLException ex) {
                    showAlert("Error: " + ex.getMessage());
                }});

        } catch (SQLException e) {
            showAlert("Error loading mood data: " + e.getMessage());
        }
    }


    /**
     * Handles the deletion of a selected mood entry.
     */
    private void handleDelete() {

        if (selectedEntry == null) { return; }

        // Creating a custom confirmation dialog box
        VBox confirmationPane = new VBox(15);
        confirmationPane.setAlignment(Pos.CENTER);
        confirmationPane.setPadding(new Insets(20));
        confirmationPane.setMinWidth(400);
        confirmationPane.setMaxWidth(400);

        // Creating title and confirmation message with styling
        Label confirmTitle = new Label("Delete Mood Entry");
        confirmTitle.getStyleClass().add("subtitle-label");
        Label confirmMessage = new Label("Are you sure you want to delete this mood entry?");
        confirmMessage.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 14px;");
        confirmMessage.setWrapText(true);

        // Creating buttons with styling
        Button confirmButton = new Button("Delete");
        confirmButton.getStyleClass().addAll("action-button", "danger-button");
        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().add("action-button");

        // Creating button box
        HBox buttonBox = new HBox(15, cancelButton, confirmButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Displaying
        confirmationPane.getChildren().addAll(confirmTitle, confirmMessage, buttonBox);
        StackPane confirmCard = new StackPane(confirmationPane);
        confirmCard.getStyleClass().add("card");
        confirmCard.setMaxWidth(450);
        confirmCard.setMaxHeight(300);
        confirmCard.setPrefWidth(450);

        // Creating the semi-transparent black overlay container to dim background when the confirmation of deletion message pops up
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7);");
        overlay.getChildren().add(confirmCard);
        StackPane.setAlignment(confirmCard, Pos.CENTER);
        overlay.setAlignment(Pos.CENTER);

        // Bug Fix: Accessing scene to position the dialog (original way I had would keep aligning in the top left, so needed to create a whole different scene to prevent clipping elements)
        Scene scene = root.getScene();
        if (scene != null) {

            // Getting the stage from the scene and make new scene for overlay
            Stage stage = (Stage) scene.getWindow();
            Scene overlayScene = new Scene(overlay, scene.getWidth(), scene.getHeight());
            overlayScene.setFill(Color.TRANSPARENT);
            overlayScene.getStylesheets().addAll(scene.getStylesheets());

            // Creating transparent stage for the overlay while blocking input to other windows (simulates pop up)
            Stage dialogStage = new Stage();
            dialogStage.initOwner(stage);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setScene(overlayScene);

            // Positioning over stage and binding it to its size (was having issues with it)
            dialogStage.setX(stage.getX());
            dialogStage.setY(stage.getY());
            overlay.prefWidthProperty().bind(overlayScene.widthProperty());
            overlay.prefHeightProperty().bind(overlayScene.heightProperty());
            overlay.setMinWidth(scene.getWidth());
            overlay.setMinHeight(scene.getHeight());

            // Setting up button actions
            cancelButton.setOnAction(event -> dialogStage.close());
            confirmButton.setOnAction(event -> {

                try {

                    if (controller.deleteMoodEntry(selectedEntry.getEntryID())) {

                        dialogStage.close();
                        loadMoodEntries();
                        showAlert("Mood entry deleted successfully");

                    } else {
                        showAlert("Failed to delete mood entry");
                    }
                } catch (SQLException e) {
                    showAlert("Error deleting mood entry: " + e.getMessage());
                }
            });

            // Prevent outside interaction and display
            overlay.setOnMouseClicked(event -> event.consume());
            dialogStage.show();
            dialogStage.setWidth(stage.getWidth());
            dialogStage.setHeight(stage.getHeight());
        }
    }


    /**
     * Loads mood entries from the database -> controller -> view into the mood log table (used mainly for dynamic updating).
     */
    private void loadMoodEntries() {

        // Accessing controller which accesses the DAO to retrieve mood entries in database
        try {

            List<MoodEntry> entries = controller.getAllMoodEntries();
            moodLogTable.getItems().clear();
            moodLogTable.getItems().addAll(entries);

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }


    /**
     * Displays an alert with the given message.
     * For success/failure notifications, it shows a fade-out message on screen (pop ups were annoying).
     * For more critical errors, it displays a popup.
     *
     * @param alert Alert message.
     */
    private void showAlert(String alert) {

        // Only showing popup for critical errors
        if (alert.contains("Error:")) {

            Alert alertDialog = new Alert(Alert.AlertType.ERROR, alert, ButtonType.OK);
            DialogPane dialogPane = alertDialog.getDialogPane();
            dialogPane.getStyleClass().add("card");
            alertDialog.showAndWait();

        } else {

            // Displaying success/failure message
            displayFadeOutNotification(alert);
        }
    }


    /**
     * Displays the success/failure notification that fades out after a few seconds.
     *
     * @param message The message to display.
     */
    private void displayFadeOutNotification(String message) {

        // Creating notification with styling
        Label notification = new Label(message);
        notification.getStyleClass().add("notification");

        // Creating container and positioning
        StackPane notificationContainer = new StackPane(notification);
        notificationContainer.setAlignment(Pos.BOTTOM_CENTER);
        notificationContainer.setPadding(new Insets(0, 0, 20, 0));
        notificationContainer.setMouseTransparent(true);
        root.getChildren().add(notificationContainer);

        // Creating fade in animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        // Creating fade out animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2));
        fadeOut.setOnFinished(event -> root.getChildren().remove(notificationContainer));

        // Playing the animation
        fadeIn.play();
        fadeIn.setOnFinished(event -> fadeOut.play());
    }

    /**
     * Returns the root node of the view for displaying in app/Main.java.
     *
     * @return Node Returns scene for displaying in app.
     */
    public Node getView() { return root; }
}
