package simulizer;

import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import simulizer.ui.WindowManager;

public class Main extends Application {

	public static void main(String[] args) {
		System.out.println("Hello world!");
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Set icon as logo.png
		primaryStage.getIcons().add(new Image(getResource("logo.png").toString()));

		// Close application
		primaryStage.setOnCloseRequest((t) -> {
			Platform.exit();
			System.exit(0);
		});

		// Just show the main window for now
		new WindowManager(primaryStage);
	}

	private URI getResource(String filepath) throws URISyntaxException {
		return getClass().getResource("/" + filepath).toURI();
	}
}
