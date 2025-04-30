package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import view.*;

/**
 * Main class for the application.
 * @author Isabella Castillo and Pike Dzurny
 */
public class Main extends Application {

    // All the view options
    private BorderPane mainLayout;
    private StackPane contentArea;
    private MoodTrackerView moodTrackerView;
    private JournalView journalView;
    private DataVisualizationView dataVisualizationView;
    private GuidedMeditationView guidedMeditationView;
    private ResourcesView resourcesView;
    private Button activeButton = null;


    /**
     * Main method for the setting up the UI.
     *
     * @param primaryStage Window that Mindscape appears in.
     */
    @Override
    public void start(Stage primaryStage) {

        // Initializing the main layout
        mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1e1f22;");

        // Creating sidebar
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);

        // Creating content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #1e1f22;");

        // Creating scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setContent(contentArea);
        scrollPane.setStyle("-fx-background: #1e1f22; -fx-background-color: transparent; -fx-control-inner-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        StackPane contentWrapper = new StackPane(scrollPane);
        mainLayout.setCenter(contentWrapper);

        // Initialize and show views
        moodTrackerView = new MoodTrackerView();
        journalView = new JournalView();
        dataVisualizationView = new DataVisualizationView();
        guidedMeditationView = new GuidedMeditationView();
        resourcesView = new ResourcesView();
        showView(moodTrackerView.getView());

        // Creating main scene
        Scene scene = new Scene(mainLayout, 1920, 1080);
        scene.getStylesheets().add(getClass().getResource("/styles/mindscape.css").toExternalForm());

        // Setting application icon and title with styling
        primaryStage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/images/icon.png")));
        primaryStage.setTitle("Mindscape");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }


    /**
     * Creates the left sidebar for the application to navigate between views.
     *
     * @return VBox Returns selected view from mood tracking to resources.
     */
    private VBox createSidebar() {

        // Creating left sidepanel for tab navigation (1920/5 = 384px width)
        VBox sidebar = new VBox(20);
        sidebar.setPrefWidth(384);
        sidebar.setStyle("-fx-background-color: #18191c; -fx-padding: 20px 10px;");

        // Adding app logo and title
        HBox titleContainer = new HBox(10);
        titleContainer.setAlignment(Pos.CENTER_LEFT);

        ImageView logoView = new ImageView(new Image(getClass().getResourceAsStream("/images/icon.png")));
        logoView.setFitHeight(96);
        logoView.setFitWidth(96);
        logoView.setPreserveRatio(true);

        Label appTitle = new Label("mindscape");
        appTitle.setFont(Font.font("System", FontWeight.BOLD, 28));
        appTitle.setStyle("-fx-text-fill: #E0E0E0;");

        titleContainer.getChildren().addAll(logoView, appTitle);
        titleContainer.setPadding(new Insets(10, 0, 30, 10));

        // Creating all necessary navigation buttons in sidebar
        Button moodButton = createNavButton("Mood Tracking", true);
        Button journalButton = createNavButton("Journal", false);
        Button visualizationButton = createNavButton("Data Visualization", false);
        Button meditationButton = createNavButton("Guided Meditation", false);
        Button resourcesButton = createNavButton("Resources & Support", false);
        activeButton = moodButton;

        // Setting the button actions
        moodButton.setOnAction(event -> { setActiveButton(moodButton); showView(moodTrackerView.getView()); });
        journalButton.setOnAction(event -> { setActiveButton(journalButton); showView(journalView.getView()); });
        visualizationButton.setOnAction(event -> { setActiveButton(visualizationButton); showView(dataVisualizationView.getView()); });
        meditationButton.setOnAction(event -> { setActiveButton(meditationButton); showView(guidedMeditationView.getView()); });
        resourcesButton.setOnAction(event -> { setActiveButton(resourcesButton); showView(resourcesView.getView()); });
        sidebar.getChildren().addAll(titleContainer, moodButton, journalButton, visualizationButton, meditationButton, resourcesButton);

        // Setting up version info in bottom left (we're real professionals here folks)
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label versionLabel = new Label("v0.1.0");
        versionLabel.setStyle("-fx-text-fill: #6e6e6e; -fx-font-size: 12px;");
        versionLabel.setAlignment(Pos.CENTER);
        versionLabel.setPadding(new Insets(10, 0, 5, 10));

        sidebar.getChildren().addAll(spacer, versionLabel);

        return sidebar;
    }


    /**
     * Creates a button for the sidebar navigation.
     * Helper for createSidebar() class.
     *
     * @param  text     Text within the button.
     * @param  isActive T/F on if the button is active or inactive.
     * @return Button   Returns a navigation button.
     */
    private Button createNavButton(String text, boolean isActive) {

        // Creating navigation buttons with styling
        Button button = new Button(text);
        button.setPrefWidth(364);
        button.setPrefHeight(50);
        button.getStyleClass().add("nav-button");

        if (isActive) { button.getStyleClass().add("active"); }
        return button;
    }


    /**
     * Sets the active button.
     * Helper for createSidebar() class.
     *
     * @param button To set the active button.
     */
    private void setActiveButton(Button button) {

        if (activeButton != null) { activeButton.getStyleClass().remove("active"); }

        button.getStyleClass().add("active");
        activeButton = button;
    }


    /**
     * Displays a view in a content area.
     * Helper for multiple Main.java methods.
     *
     * @param view To set the view.
     */
    private void showView(javafx.scene.Node view) {

        contentArea.getChildren().clear();
        contentArea.getChildren().add(view);
    }


    /**
     * Launch method.
     *
     * @param args Launches application.
     */
    public static void main(String[] args) { launch(args); }
}
