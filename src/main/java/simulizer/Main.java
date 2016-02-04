package simulizer;

import javafx.application.Application;
import javafx.stage.Stage;
import simulizer.ui.WindowManager;

public class Main extends Application {

	public static void main(String[] args) {
		System.out.println("Hello world!");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Show the main window for now
		new WindowManager("my-theme", primaryStage);
	}
}
