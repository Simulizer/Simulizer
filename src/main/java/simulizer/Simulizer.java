package simulizer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.animation.FadeTransition;
import javafx.application.Application;
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

public class Simulizer extends Application {
	// Thanks to: https://gist.github.com/jewelsea/2305098

	public URI SPLASH_IMAGE;
	private int SPLASH_WIDTH, SPLASH_HEIGHT;
	private long splashStartTime;
	private Pane splashLayout;
	private Label progressText;

	private WindowManager wm;
	private Settings settings;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void init() throws Exception {
		ImageView splash = null;

		try {
			settings = Settings.loadSettings(new File("settings.json"));
		} catch (IOException ex) {
			System.err.println("Failed to launch: settings.json was missing");
			System.exit(1);
		}

		SPLASH_WIDTH = (int) settings.get("splash-screen.width");
		SPLASH_HEIGHT = (int) settings.get("splash-screen.height");

		SPLASH_IMAGE = getResource("SimulizerLogo.png");
		splash = new ImageView(new Image(SPLASH_IMAGE.toString(), SPLASH_WIDTH, SPLASH_HEIGHT, true, true));

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
			public Boolean call() throws Exception {
				launchWindowManager(primaryStage);
				updateMessage("Authors: Charlie Street, Kelsey McKenna, Matthew Broadway, Michael Oultram, Theo Styles . . .");
				long offset = (int) settings.get("splash-screen.delay") - (System.currentTimeMillis() - splashStartTime);
				if (offset > 0)
					Thread.sleep(offset);
				return true;
			}
		};

		if ((boolean) settings.get("splash-screen.enabled")) {
			showSplash(startupTask);
			new Thread(startupTask).start();
		} else {
			launchWindowManager(primaryStage);
			wm.show();
		}
	}

	private void launchWindowManager(Stage primaryStage) {
		// Close application
		primaryStage.setOnCloseRequest((t) -> wm.getWorkspace().closeAll());

		// Just show the main window for now
		try {
			wm = new WindowManager(primaryStage, settings);
		} catch (IOException ex) {
			System.err.println("Failed to launch: " + ex.getMessage());
			System.exit(1);
		}
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
			} // TODO: add code to gracefully handle other task states.
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
		stage.setAlwaysOnTop(true);
		stage.show();
		splashStartTime = System.currentTimeMillis();
	}

	private URI getResource(String filepath) throws URISyntaxException {
		return getClass().getResource("/" + filepath).toURI();
	}
}
