package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class ResourcesView {

    private VBox root;
    private TextField searchField;
    private Button searchButton;
    private Label resultLabel;

    public ResourcesView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        searchField = new TextField();
        searchField.setPromptText("search for local resources (e.g., therapists, clinics)");
        searchButton = new Button("search");
        resultLabel = new Label("resources will appear here");

        searchButton.setOnAction(e -> {
            String searchTerm = searchField.getText().trim();
            if (!searchTerm.isEmpty()) {
                resultLabel.setText("searching for: " + searchTerm);
                // later: integrate google places api to get results
            } else {
                resultLabel.setText("please enter a search term");
            }
        });

        root.getChildren().addAll(new Label("resources/support"), searchField, searchButton, resultLabel);
    }

    public Node getView() {
        return root;
    }
}
