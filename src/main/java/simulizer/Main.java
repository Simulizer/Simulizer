package simulizer;

import java.io.File;

import javafx.application.Application;
import javafx.stage.Stage;
import simulizer.ui.WindowManager;

public class Main extends Application {
	// TODO: Find out how to get the resources folder properly for java.io.File;
	public static String RESOURCES = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator;

	public static void main(String[] args) {
		System.out.println("Hello world!");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Show the main window for now
		new WindowManager(primaryStage);
	}
}
