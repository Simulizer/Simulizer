package simulizer;

import java.io.File;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;
import simulizer.ui.windows.SplashScreen;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

public class Simulizer extends Application {
	public static final String VERSION = "0.3 (beta)";
	private static Image icon = null;

	public WindowManager wm;
	public Settings settings;

	public static Image getIcon() {
		if(icon == null) {
			icon = new Image(FileUtils.getResourcePath("/img/logo.png"));
		}
		return icon;
	}

	public static void main(String[] args) {
		Thread.setDefaultUncaughtExceptionHandler(UIUtils::showExceptionDialog);
		launch(args);
	}

	@Override
	public void init() throws Exception {
		try {
			settings = Settings.loadSettings(new File("settings.json"));
		} catch (IOException ex) {
			UIUtils.showErrorDialog("Failed To Launch", "Failed to launch: settings.json was missing");
			System.exit(1);
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.getIcons().add(getIcon());

		if ((boolean) settings.get("splash-screen.enabled")) {
			SplashScreen s = new SplashScreen(settings);
			s.show(this, primaryStage);
		} else {
			launchWindowManager(primaryStage);
			wm.show();
		}
	}

	public void launchWindowManager(Stage primaryStage) {
		// Close application
		primaryStage.setOnCloseRequest((t) -> wm.getWorkspace().closeAll());

		// Just show the main window for now
		try {
			wm = new WindowManager(primaryStage, settings);
		} catch (IOException ex) {
			UIUtils.showErrorDialog("Failed To Launch", ex.getMessage());
			System.exit(1);
		}
	}

}
