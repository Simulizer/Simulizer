package simulizer;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
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

		// Copy folders from resources to jar running directory
		// copyResource("code");
		// copyResource("layouts");
		// copyResource("themes");

		// TODO: Remove this code, simple check for if we are running within the work folder
		String cwd = System.getProperty("user.dir");
		if (!cwd.endsWith("work")) System.out.println("Working from: " + cwd + "\nPLEASE RUN FROM GRADLE");

		// Close application
		primaryStage.setOnCloseRequest((t) -> {
			Platform.exit();
			System.exit(0);
		});

		// Just show the main window for now
		new WindowManager(primaryStage);
	}

	// Thanks to: http://stackoverflow.com/questions/11012819/how-can-i-get-a-resource-folder-from-inside-my-jar-file#answer-20073154
	private void copyResource(String relFile) throws IOException, URISyntaxException {
		Path outsideFolder = Paths.get(System.getProperty("user.dir") + File.separator + relFile);
		if (!Files.exists(outsideFolder)) {
			final File jarFile = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			if (jarFile.isFile()) {
				// Running in a JAR file
				final JarFile jar = new JarFile(jarFile);
				final Enumeration<JarEntry> entries = jar.entries(); // gives ALL entries in jar
				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					// filter according to the path
					if (entry.getName().startsWith(relFile + "/")) {
						// TODO: Copy files from within the JAR
						System.out.println(entry.getName());
					}
				}
				jar.close();
			} else {
				// Running in an IDE
				Path insideFolder = Paths.get(getResource(relFile));
				if (Files.isDirectory(insideFolder)) {
					Files.createDirectories(outsideFolder);
					Files.walkFileTree(insideFolder, new CopyFileVisitor(outsideFolder));
				} else {
					Files.copy(insideFolder, outsideFolder);
				}
			}
		}
	}

	private URI getResource(String filepath) throws URISyntaxException {
		return getClass().getResource("/" + filepath).toURI();
	}
}
