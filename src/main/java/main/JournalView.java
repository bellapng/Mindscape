package main;

import dao.JournalDAO;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import models.JournalEntry;

import java.time.LocalDateTime;

public class JournalView {

    private VBox root;
    private TextField titleField;
    private HTMLEditor htmlEditor;  // rich text editor for the journal entry
    private Button saveButton;
    private Button updateButton;    // placeholder for update feature
    private JournalDAO journalDAO;

    public JournalView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        
        titleField = new TextField();
        titleField.setPromptText("enter title for your journal entry");
        
        // use HTMLEditor so users get rich text formatting (bold, italic, underline, lists, etc.)
        htmlEditor = new HTMLEditor();
        htmlEditor.setPrefHeight(300);

        saveButton = new Button("save entry");
        updateButton = new Button("update entry");
        updateButton.setDisable(true); // disable until update is fully implemented
        
        journalDAO = new JournalDAO();

        // set up action handlers
        saveButton.setOnAction(e -> handleSave());
        updateButton.setOnAction(e -> handleUpdate());

        root.getChildren().addAll(new Label("journal entry title:"), titleField,
                new Label("write your journal entry:"), htmlEditor, saveButton, updateButton);
    }

    // saving the journal entry using the dao
    private void handleSave() {
        String title = titleField.getText().trim();
        String htmlContent = htmlEditor.getHtmlText().trim();
        
        // a minimal check - note HTMLEditor returns default empty html markup if nothing is typed
        if (title.isEmpty() || htmlContent.isEmpty() || htmlContent.equals("<html><head></head><body contenteditable=\"true\"></body></html>")) {
            showAlert("title and journal entry cannot be empty");
            return;
        }
        
        // construct a new JournalEntry with current datetime (id 0, as db will auto-generate)
        JournalEntry entry = new JournalEntry(0, title, htmlContent, LocalDateTime.now());
        try {
            if (journalDAO.insertJournalEntry(entry)) {
                showAlert("journal entry saved successfully");
                // clear fields after successful save
                titleField.clear();
                htmlEditor.setHtmlText("");
            } else {
                showAlert("failed to save journal entry");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert("error: " + ex.getMessage());
        }
    }
    
    // placeholder for future update functionality
    private void handleUpdate() {
        showAlert("update feature not implemented yet");
    }

    // utility to display simple alerts
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    public Node getView() {
        return root;
    }
}
