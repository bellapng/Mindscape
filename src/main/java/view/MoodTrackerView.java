package view;

import dao.MoodDAO;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.MoodEntry;

import java.time.LocalDateTime;

public class MoodTrackerView {

    private VBox root;
    private ChoiceBox<String> moodChoice;
    private TextField tagField;
    private Button submitButton;
    private MoodDAO moodDAO;

    public MoodTrackerView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        moodChoice = new ChoiceBox<>();
        tagField = new TextField();
        submitButton = new Button("submit mood");
        moodDAO = new MoodDAO();

        // load moods into the choicebox
        try {
            var moodList = moodDAO.getMoodList();
            for (var m : moodList) {
                // display as "happy (id=1)" etc.
                moodChoice.getItems().add(m.getMoodName() + " (id=" + m.getMoodID() + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        submitButton.setOnAction(e -> handleSubmit());

        root.getChildren().addAll(new Label("select your mood:"), moodChoice,
                new Label("optional tag:"), tagField, submitButton);
    }

    private void handleSubmit() {
        var selected = moodChoice.getValue();
        if (selected == null) {
            showAlert("please select a mood");
            return;
        }

        int moodID = parseMoodID(selected);
        String tagText = tagField.getText().trim();
        MoodEntry entry = new MoodEntry(0, moodID, tagText, LocalDateTime.now());
        try {
            if (moodDAO.insertMoodEntry(entry)) {
                showAlert("mood logged successfully");
                moodChoice.setValue(null);
                tagField.clear();
            } else {
                showAlert("failed to log mood");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("error: " + ex.getMessage());
        }
    }

    private int parseMoodID(String s) {
        int idx = s.indexOf("(id=");
        if (idx != -1) {
            int start = idx + 4;
            int end = s.indexOf(")", start);
            return Integer.parseInt(s.substring(start, end));
        }
        return -1;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.showAndWait();
    }

    public Node getView() {
        return root;
    }
}
