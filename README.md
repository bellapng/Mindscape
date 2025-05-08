Developed using IntelliJ IDEA Ultimate 2024.3.5 with Maven build and JUnit5 for unit tests.

Program Files: Main.java, mindscape.sqlite
               DataVisualizationView.java, GuidedMeditationView.java, JournalView.java, MoodTrackerView.java, ResourcesView.java
               DataVisualizationController.java, GuidedMeditationController.java, JournalController.java, MoodTrackerController.java
               ExerciseDAO.java, FavoriteResourcesDAO.java, JournalDAO.java, MoodDAO.java
               DatabaseConnection.java, Exercise.java, ExerciseEntry.java, FavoriteResource.java, JournalEntry.java, Mood.java, MoodEntry.java, TestDataGenerator.java
               DatabaseConnectionTest.java, ExerciseDAOTest.java, FavoriteResourcesDAOTest.java, JournalDAOTest.java, MoodDAOTest.java

In order to run Mindscape locally, ensure you are using IntelliJ IDEA or another similar Java IDE, and ideally you have JDK 17+ installed, but we created it using JDK 23.

Project Overview:
Mindscape is a mental wellness desktop application developed in Java using JavaFX. The current version includes data management functionality for mood tracking,
journaling, exercise logging, and user-favorited resources. It uses SQLite for persistent data storage and a DAO (Data Access Object) structure for modularity.

How to Run:
1. Open the 'Mindscape' project folder within your Java IDE.
2. Mark '\Mindscape\src\main\java' as the sources root.
3. Mark '\Mindscape\src\test\java' as the test sources root.
4. Install JavaFX SDK 17 (linked below) and copy down the path to the 'lib' folder. Ex: 'C:\Users\Name\Desktop\javafx-sdk-17.0.14\lib'.
5. Add the mindscape.sqlite database as the data source within your IDE's database tab. The easiest way to do this is to select 'Data source from path' and selecting the mindscape.sqlite
   database file within this project file.
6. Next, you must set up the proper run configuration. Please follow the following instructions carefully in order to set run the program properly:

   	1. Open the Run/Debug Configurations menu within your IDE.

   	2. Create a new Application Configuration titled 'Main' with the main class field running 'app.Main'.

   	3. Under the "modify options" dropdown next to the "Build and run", check the 'Add VM Options' tab. Populate this field with the following...

   		   	--module-path
   			"C:\Users\Name\Desktop\javafx-sdk-17.0.14\lib" 	// Be sure this is set to YOUR JavaFX SDK path with the \lib at the end
   			--add-modules
   			javafx.controls,javafx.fxml,javafx.web
   			--add-exports=javafx.controls/com.sun.javafx.scene.control=ALL-UNNAMED   
   	
   	4. Under the "modify options" dropdown next to "Build and run", check the 'Environment Variables' tab. Populate this field with the following...
   			
   			GOOGLE_PLACES_API_KEY=(your api key)

7. Navigate to /src/main/java/app/Main.java and run the main method in order to launch the program.
8. If you would like to generate test data to test all of the features, please navigate to '\Mindscape\src\main\java\models\TestDataGenerator.java' and run the code with however
   many entries you would like to add. It is configured for about a 6 month test period, but you are welcome to adjust values as necessary.
9. If you would like to run the unit tests, navigate to '\Mindscape\src\test\java' and you may run any class and it will run the written unit tests and confirm pass or fail.

Dependencies:
1. JDK 17+
2. SQLite JDBC 3.49.1.0 for Apache Maven (found via https://central.sonatype.com/artifact/org.xerial/sqlite-jdbc/3.49.1.0)
3. JavaFX SDK 17 (install via https://gluonhq.com/products/javafx/)
4. Google Places API

Notes:
We added the full project file so there is not any trouble with missing files during the grading process. All code can be found in '\Mindscape\src\main'. All assets and required files are
included within the project file as well. All methods are documented well with jdocs and each class has a description as well as the authors name.
