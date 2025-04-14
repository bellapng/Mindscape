package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TabPane tabPane = new TabPane();

        // mood tracking tab
        Tab moodTab = new Tab("mood tracking");
        moodTab.setContent(new MoodTrackerView().getView());
        moodTab.setClosable(false);
        tabPane.getTabs().add(moodTab);

        // journaling tab
        Tab journalTab = new Tab("journal");
        journalTab.setContent(new JournalView().getView());
        journalTab.setClosable(false);
        tabPane.getTabs().add(journalTab);

        // data visualization tab
        Tab visualizationTab = new Tab("data visualization");
        visualizationTab.setContent(new DataVisualizationView().getView());
        visualizationTab.setClosable(false);
        tabPane.getTabs().add(visualizationTab);

        // guided meditation/exercises tab
        Tab meditationTab = new Tab("guided meditation");
        meditationTab.setContent(new GuidedMeditationView().getView());
        meditationTab.setClosable(false);
        tabPane.getTabs().add(meditationTab);

        // resources/support tab
        Tab resourcesTab = new Tab("resources/support");
        resourcesTab.setContent(new ResourcesView().getView());
        resourcesTab.setClosable(false);
        tabPane.getTabs().add(resourcesTab);

        BorderPane root = new BorderPane(tabPane);
        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setTitle("mindscape app");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
