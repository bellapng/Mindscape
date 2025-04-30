package view;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class DataVisualizationView {
    
    private VBox root;
    
    public DataVisualizationView() {
        root = new VBox(10);
        root.setPadding(new Insets(15));
        Label label = new Label("data visualization - charts and graphs will appear here");
        root.getChildren().add(label);
    }
    
    public Node getView() {
        return root;
    }
}
