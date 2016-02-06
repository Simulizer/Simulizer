package simulizer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
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

		// Copy folders from resources to jar running directory
		copyResource("code");
		copyResource("layouts");
		copyResource("themes");

		// Just show the main window for now
		new WindowManager(primaryStage);
	}

	private void copyResource(String filepath) throws IOException, URISyntaxException {
		Path file = Paths.get(System.getProperty("user.dir") + File.separator + filepath);
		Path resources = Paths.get(getResource(filepath));
		if (!Files.exists(file)) {
			if (Files.isDirectory(resources)) {
				Files.createDirectories(file);
				Files.walkFileTree(resources, new CopyFileVisitor(file));
			} else {
				Files.copy(resources, file);
			}
		}
	}

	private URI getResource(String filepath) throws URISyntaxException {
		return getClass().getClassLoader().getResource(filepath).toURI();
	}
}
