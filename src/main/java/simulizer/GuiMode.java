package simulizer;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;
import simulizer.ui.components.CurrentFile;
import simulizer.ui.windows.SplashScreen;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by matthew on 06/09/16.
 */
public class GuiMode {

	private static Image icon = null;
	private static File settingsFile;
	private static Stage primaryStage;

	private static App app;
	public static WindowManager wm;
	public static Settings settings;
	public static CommandLineArguments.GuiModeArgs args;

	public static Image getIcon() {
		if (icon == null) {
			icon = new Image(FileUtils.getResourcePath("/img/logo.png"));
		}
		return icon;
	}

	public static Stage getPrimaryStage() {
		return primaryStage;
	}

	public static class App extends Application {

		void launchApp(String[] args) {
			launch(args);
		}

		@Override
		public void init() {
			try {
                GuiMode.settings = Settings.loadSettings(settingsFile);
            } catch (IOException ex) {
                UIUtils.showErrorDialog("Failed To Launch", "Failed to launch: settings file: '" +
                        settingsFile.getPath() + "' was missing");
                throw new RuntimeException("missing settings file " + settingsFile.getPath());
            }
		}

		@Override
		public void start(Stage primaryStage) throws Exception {
			GuiMode.primaryStage = primaryStage;
			primaryStage.getIcons().add(GuiMode.getIcon());

			// GUI needs to be active in order to display dialogs on failure
			CurrentFile.loadInitialFile();

			boolean showSplash = (boolean) settings.get("splash-screen.enabled");
			if (args.noSplash) // takes precedence over settings file
				showSplash = false;

			if (showSplash) {
				SplashScreen s = new SplashScreen(settings);
				s.show(primaryStage); // calls launchWindowManager after splash is done
			} else {
				GuiMode.launchWindowManager(primaryStage);
				GuiMode.wm.show();
			}
		}
	}

	public static void start(String[] rawArgs, CommandLineArguments parsedArgs) {
		Thread.setDefaultUncaughtExceptionHandler(UIUtils::showExceptionDialog);

		// improves ugly font rendering
		// see http://mail.openjdk.java.net/pipermail/openjfx-dev/2013-August/009959.html
		System.setProperty("prism.lcdtext", "false");
		System.setProperty("prism.text", "t2k");

		settingsFile = new File(parsedArgs.guiMode.settingsPath);
		args = parsedArgs.guiMode;

		app = new App();
		app.launchApp(rawArgs);
	}

	public static void launchWindowManager(Stage primaryStage) {
		// Just show the main window for now
		try {
			wm = new WindowManager(app, primaryStage, settings);
		} catch (IOException ex) {
			UIUtils.showErrorDialog("Failed To Launch", ex.getMessage());
			throw new RuntimeException("failed to launch: " + ex.getMessage());
		}
	}
}
