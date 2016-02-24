package simulizer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;

public class Main extends Application {
	// Thanks to: https://gist.github.com/jewelsea/2305098

	public URI SPLASH_IMAGE;

	private Pane splashLayout;
	private Label progressText;
	private WindowManager wm;
	private static final int SPLASH_WIDTH = 676;
	private static final int SPLASH_HEIGHT = 235;

	public static void main(String[] args) {
		System.out.println("Hello world!");
		launch(args);
	}

	@Override
	public void init() {
		ImageView splash = null;

		try {
			SPLASH_IMAGE = getResource("SimulizerLogo.png");
			splash = new ImageView(new Image(SPLASH_IMAGE.toString(), SPLASH_WIDTH, SPLASH_HEIGHT, true, true));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		progressText = new Label("Authors: Charlie Street, Kelsey McKenna, Matthew Broadway, Michael Oultram, Theo Styles\n" + "Version: 0.0.1\n" + "https://github.com/ToastNumber/Simulizer");

		splashLayout = new VBox();
		splashLayout.getChildren().addAll(splash, progressText);
		progressText.setPadding(new Insets(5, 5, 5, 5));
		progressText.setAlignment(Pos.BASELINE_CENTER);

		splashLayout.getStyleClass().add("splashscreen");
		splashLayout.setEffect(new DropShadow());
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Set icon as logo.png
		primaryStage.getIcons().add(new Image(getResource("logo.png").toString()));

		Task<Boolean> startupTask = new Task<Boolean>() {
			@Override
			protected Boolean call() throws Exception {
				// TODO: Remove this code, simple check for if we are running within the work folder
				String cwd = System.getProperty("user.dir");
				if (!cwd.endsWith("work"))
					System.out.println("Working from: " + cwd + "\nPLEASE RUN FROM GRADLE");

				// Close application
				primaryStage.setOnCloseRequest((t) -> {
					Platform.exit();
					System.exit(0);
				});

				// Loads the settings from file
				Settings settings = Settings.loadSettings(new File("settings.json"));

				// Just show the main window for now
				wm = new WindowManager(primaryStage, settings, 1024, 705);

				updateMessage("Authors: Charlie Street, Kelsey McKenna, Matthew Broadway, Michael Oultram, Theo Styles . . .");
				Thread.sleep(750); // so that it's at least readable

				return true;
			};
		};

		//showSplash(startupTask);
		//new Thread(startupTask).start();
		
		// TODO: Remove Splash Screen skip
		String cwd = System.getProperty("user.dir");
		if (!cwd.endsWith("work"))
			System.out.println("Working from: " + cwd + "\nPLEASE RUN FROM GRADLE");

		// Close application
		primaryStage.setOnCloseRequest((t) -> {
			Platform.exit();
			System.exit(0);
		});

		// Loads the settings from file
		Settings settings = Settings.loadSettings(new File("settings.json"));

		// Just show the main window for now
		wm = new WindowManager(primaryStage, settings, 1024, 705);
		wm.show();
	}

	private void showSplash(Task<?> task) throws URISyntaxException, MalformedURLException {
		Stage stage = new Stage(StageStyle.DECORATED);
		stage.getIcons().add(new Image(getResource("logo.png").toString()));

		task.stateProperty().addListener((observableValue, oldState, newState) -> {
			if (newState == Worker.State.SUCCEEDED) {
				// stage.toFront();
				FadeTransition fadeSplash = new FadeTransition(Duration.seconds(1.2), splashLayout);
				fadeSplash.setFromValue(1.0);
				fadeSplash.setToValue(0.0);
				fadeSplash.setOnFinished(actionEvent -> stage.hide());

				wm.show();
				fadeSplash.play();
			} // todo add code to gracefully handle other task states.
		});

		Scene splashScene = new Scene(splashLayout);

		String path = getResource("splash.css").toString();
		splashScene.getStylesheets().add(path);

		stage.initStyle(StageStyle.UNDECORATED);
		final Rectangle2D bounds = Screen.getPrimary().getBounds();
		stage.setScene(splashScene);
		stage.setX(bounds.getMinX() + bounds.getWidth() / 2 - SPLASH_WIDTH / 2);
		stage.setY(bounds.getMinY() + bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);
		stage.setWidth(SPLASH_WIDTH);
		stage.setHeight(SPLASH_HEIGHT);
		stage.show();
	}

	private URI getResource(String filepath) throws URISyntaxException {
		return getClass().getResource("/" + filepath).toURI();
	}
}
