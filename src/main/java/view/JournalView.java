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
import javafx.scene.web.HTMLEditor;
import javafx.stage.*;
import javafx.util.Duration;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

import models.JournalEntry;
import controller.JournalController;


/**
 * JournalView class represents the GUI framework for the journaling feature within Mindscape.
 * @author Isabella Castillo
 */
public class JournalView {

    // Creating necessary objects for use through the view file
    private final JournalController controller = new JournalController();
    private final VBox root = new VBox(20);
    private VBox journalInputPane;
    private VBox journalSearchPane;

    private final Label title = new Label("Journal");
    private TextField titleField;
    private TextField searchField;
    private HTMLEditor htmlEditor;
    private Button saveButton;

    private TableView<JournalEntry> journalTable;
    private boolean showingSearchResults = false;
    private JournalEntry selectedEntry;


    /**
     * Creates the view and initializes the layout.
     */
    public JournalView() {

        // Setting the root and title styling
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #1e1f22;");
        title.getStyleClass().add("title-label");

        // Initializing the journal input and search pane
        initializeJournalInputPane();
        initializeJournalSearchPane();

        // Building the initial layout
        root.getChildren().addAll(title, journalInputPane);
    }


    /**
     * Initializes the journal input pane.
     */
    private void initializeJournalInputPane() {

        // Setting main pane where action buttons will be
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(50, 0, 0, 0));

        // Creating journal input content (on top of detail pane)
        VBox journalInputContent = new VBox(15);
        journalInputContent.setPadding(new Insets(0, 20, 20, 20));

        // Creating subtitle and label prompting for title input with styling
        Label subtitle = new Label("Create Journal Entry");
        subtitle.getStyleClass().add("subtitle-label");
        HBox subtitleBox = new HBox(subtitle);
        subtitleBox.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Enter your title:");
        titleLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");
        titleField = new TextField();
        titleField.setPromptText("Enter a title for your journal entry");
        titleField.setMaxWidth(Double.MAX_VALUE);

        Label contentLabel = new Label("Write your entry here:");
        contentLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");

        // Creating rich text editor with styling
        htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(400);
        htmlEditor.getStyleClass().add("html-editor");

        // Adding elements to card and styling
        journalInputContent.getChildren().addAll(subtitleBox, titleLabel, titleField, contentLabel, htmlEditor);
        StackPane journalInputCard = new StackPane(journalInputContent);
        journalInputCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating buttons with styling and putting into actionBox
        saveButton = new Button("Save Entry");
        saveButton.getStyleClass().addAll("action-button", "success-button");
        Button searchButton = new Button("Search Entries");
        searchButton.getStyleClass().add("action-button");
        HBox actionBox = new HBox(15, saveButton, searchButton);
        actionBox.setAlignment(Pos.CENTER);

        // Displaying
        detailPane.getChildren().addAll(journalInputCard, actionBox);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        detailCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(detailCard, Priority.ALWAYS);
        journalInputPane = new VBox(detailCard);
        journalInputPane.setAlignment(Pos.CENTER);

        // Setting up actions for handling when user presses "save entry " or "search entries"
        saveButton.setOnAction(event -> handleSave());
        searchButton.setOnAction(event -> toggleJournalInputOrSearchView());
    }


    /**
     * Initializes the journal search pane with a table view for displaying journal entries.
     */
    private void initializeJournalSearchPane() {

        // Setting main pane where action buttons will be
        VBox detailPane = new VBox(20);
        detailPane.setPadding(new Insets(0));

        // Creating journal search content (on top of detail pane)
        VBox journalSearchContent = new VBox(15);
        journalSearchContent.setAlignment(Pos.CENTER);
        journalSearchContent.setPadding(new Insets(0, 20, 20, 20));

        // Creating subtitle, search label, and search text fields with styling
        Label subtitle = new Label("Your Journal Entries");
        subtitle.getStyleClass().add("subtitle-label");
        HBox subtitleBox = new HBox(subtitle);
        subtitleBox.setAlignment(Pos.CENTER);

        Label searchLabel = new Label("Search by keyword:");
        searchLabel.setStyle("-fx-text-fill: #E0E0E0; -fx-font-size: 15px;");
        HBox searchLabelBox = new HBox(searchLabel);
        searchLabelBox.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Enter a keyword to search");
        searchField.setMaxWidth(Double.MAX_VALUE);

        // Creating search button next to search box
        Button performSearchButton = new Button("Search");
        performSearchButton.getStyleClass().addAll("action-button", "primary-button");
        performSearchButton.setStyle("-fx-border-color: white; -fx-border-width: 1px;");
        HBox searchBox = new HBox(10, searchField, performSearchButton);
        searchBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(searchField, Priority.ALWAYS);

        // Creating table view with styling
        journalTable = new TableView<>();
        journalTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        journalTable.setPlaceholder(new Label("No journal entries found"));
        journalTable.getStyleClass().add("mood-log-table");

        // Creating table "date" column and setting up how we should update the item
        TableColumn<JournalEntry, LocalDateTime> dateColumn = new TableColumn<>("Date & Time");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("entryDateTime"));
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

        // Creating table "title" column and setting up how we should update the item
        TableColumn<JournalEntry, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        titleColumn.setCellFactory(column -> new TableCell<>() {

            // Overriding the updateItem method in TableCell (JavaFX) so we can display in correct format and style
            @Override
            protected void updateItem(String item, boolean empty) {

                super.updateItem(item, empty);

                setText(empty || item == null ? "" : item);
                setStyle("-fx-text-fill: #E0E0E0;");
            }});

        // Adding table and content to card
        journalTable.getColumns().addAll(dateColumn, titleColumn);
        journalSearchContent.getChildren().addAll(subtitleBox, searchLabelBox, searchBox, journalTable);
        StackPane journalSearchCard = new StackPane(journalSearchContent);
        journalSearchCard.getStyleClass().addAll("card", "exercise-detail-card");

        // Creating '← Back' button at the top left
        Button backButton = new Button("← Back");
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> toggleJournalInputOrSearchView());
        HBox backButtonContainer = new HBox(backButton);
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
        detailPane.getChildren().addAll(backButtonContainer, journalSearchCard, actionBox);
        StackPane detailCard = new StackPane(detailPane);
        detailCard.getStyleClass().add("card");
        detailCard.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(detailCard, Priority.ALWAYS);
        journalSearchPane = new VBox(detailCard);
        journalSearchPane.setAlignment(Pos.CENTER);

        // Setting up actions for handling when user wants to search, edit, or delete
        journalTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

            selectedEntry = newSelection;
            editButton.setDisable(newSelection == null);
            deleteButton.setDisable(newSelection == null);
        });

        performSearchButton.setOnAction(event -> searchEntries());
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
    }


    /**
     * Toggles between the journal input view and the search results view.
     */
    private void toggleJournalInputOrSearchView() {

        if (showingSearchResults) {

            // Switching to journal input view with animation
            root.getChildren().remove(journalSearchPane);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), journalInputPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            root.getChildren().add(journalInputPane);
            fadeIn.play();
            showingSearchResults = false;

        } else {

            // Switching to search results view with animation (and call to update entries for dynamic updating)
            root.getChildren().remove(journalInputPane);
            loadAllJournalEntries();
            FadeTransition fadeIn = new FadeTransition(Duration.millis(1000), journalSearchPane);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);

            root.getChildren().add(journalSearchPane);
            fadeIn.play();
            showingSearchResults = true;
        }
    }


    /**
     * Performs a search for journal entries based on a keyword.
     */
    private void searchEntries() {

        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) { loadAllJournalEntries(); return; }

        // Searching entries via keyword in database, display popup error if results are empty
        try {

            List<JournalEntry> results = controller.searchJournalEntries(keyword);
            journalTable.getItems().clear();
            journalTable.getItems().addAll(results);
            if (results.isEmpty()) { showAlert("No journal entries found matching '" + keyword + "', please try another keyword."); }

        } catch (SQLException e) {
            showAlert("Error: " + e.getMessage());
        }
    }


    /**
     * Handles the saving of a new journal entry.
     */
    private void handleSave() {

        String title = titleField.getText().trim();
        String htmlContent = htmlEditor.getHtmlText().trim();

        // Grabbing attention to blank field upon attempted submission
        if (title.isEmpty()) {

            titleField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

            // Shake animation
            Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(titleField.translateXProperty(), 0)),
                    new KeyFrame(Duration.millis(50), new KeyValue(titleField.translateXProperty(), -5)),
                    new KeyFrame(Duration.millis(100), new KeyValue(titleField.translateXProperty(), 5)),
                    new KeyFrame(Duration.millis(150), new KeyValue(titleField.translateXProperty(), -5)),
                    new KeyFrame(Duration.millis(200), new KeyValue(titleField.translateXProperty(), 0)));
            timeline.setCycleCount(2);
            timeline.play();

            return;
        }
        titleField.setStyle("");

        // Attempting to add new journal entry
        JournalEntry entry = new JournalEntry(0, title, htmlContent, LocalDateTime.now());

        try {

            // Displaying proper alert/error
            if (controller.insertJournalEntry(entry)) {

                showAlert("Journal entry saved successfully!");
                titleField.clear();
                htmlEditor.setHtmlText("");

            } else {
                showAlert("Failed to save journal entry.");
            }
        } catch (SQLException ex) {
            showAlert("Error: " + ex.getMessage());
        }
    }


    /**
     * Handles the editing of a journal entry.
     */
    private void handleEdit() {

        if (selectedEntry == null) { return; }

        // Switching to journal input view
        toggleJournalInputOrSearchView();
        titleField.setText(selectedEntry.getTitle());
        htmlEditor.setHtmlText(selectedEntry.getTextEntry());

        // Changing save button to update when editing an entry and handling the update
        saveButton.setText("Update Entry");
        saveButton.setOnAction(event -> {

            if (selectedEntry == null) { return; }

            String title = titleField.getText().trim();
            String htmlContent = htmlEditor.getHtmlText().trim();

            // Grabbing attention to blank field upon attempted submission
            if (title.isEmpty()) {

                titleField.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-background-color: rgba(255, 0, 0, 0.1);");

                // Shake animation
                Timeline timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(titleField.translateXProperty(), 0)),
                        new KeyFrame(Duration.millis(50), new KeyValue(titleField.translateXProperty(), -5)),
                        new KeyFrame(Duration.millis(100), new KeyValue(titleField.translateXProperty(), 5)),
                        new KeyFrame(Duration.millis(150), new KeyValue(titleField.translateXProperty(), -5)),
                        new KeyFrame(Duration.millis(200), new KeyValue(titleField.translateXProperty(), 0)));
                timeline.setCycleCount(2);
                timeline.play();

                return;
            }
            titleField.setStyle("");

            // Attempting to edit journal entry
            JournalEntry updatedEntry = new JournalEntry(selectedEntry.getJournalID(), title, htmlContent, selectedEntry.getEntryDateTime());

            try {

                // Displaying proper alert and resseting button
                if (controller.updateJournalEntry(updatedEntry)) {

                    showAlert("Journal entry updated successfully!");
                    titleField.clear();
                    htmlEditor.setHtmlText("");

                    saveButton.setText("Save Entry");
                    saveButton.setOnAction(event2 -> handleSave());
                    selectedEntry = null;

                } else {
                    showAlert("Failed to update journal entry.");
                }
            } catch (SQLException ex) {
                showAlert("Error: " + ex.getMessage());
            }
        });
    }


    /**
     * Handles the deletion of a journal entry.
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
        Label confirmTitle = new Label("Delete Journal Entry");
        confirmTitle.getStyleClass().add("subtitle-label");
        Label confirmMessage = new Label("Are you sure you want to delete this journal entry?");
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

            // Positioning over stage and binding it to its size
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

                    // Displaying proper alerts and refreshing table (dynamic updating)
                    if (controller.deleteJournalEntry(selectedEntry.getJournalID())) {

                        dialogStage.close();
                        if (searchField.getText().trim().isEmpty()) {
                            loadAllJournalEntries();
                        } else {
                            searchEntries();
                        }

                        showAlert("Journal entry deleted successfully!");

                    } else {
                        showAlert("Failed to delete journal entry.");
                    }
                } catch (SQLException e) {
                    showAlert("Error: " + e.getMessage());
                }
            });

            // Preventing outside interaction and display
            overlay.setOnMouseClicked(event -> event.consume());
            dialogStage.show();
            dialogStage.setWidth(stage.getWidth());
            dialogStage.setHeight(stage.getHeight());
        }
    }


    /**
     * Loads all journal entries from the database -> controller -> view into the journal table (mainly used for dynamic updating).
     */
    private void loadAllJournalEntries() {

        // Accessing controller which accesses the DAO to retrieve journal entries in database
        try {

            List<JournalEntry> entries = controller.getAllJournalEntries();
            journalTable.getItems().clear();
            journalTable.getItems().addAll(entries);

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
