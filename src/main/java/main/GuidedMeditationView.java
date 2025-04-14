package main;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GuidedMeditationView {

    private VBox root;

    public GuidedMeditationView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        Label label = new Label("guided meditation - exercise instructions, timers, and animations will go here");
        Button startButton = new Button("start meditation");
        
        startButton.setOnAction(e -> label.setText("meditation in progress..."));
        root.getChildren().addAll(label, startButton);
    }

    public Node getView() {
        return root;
    }
}
