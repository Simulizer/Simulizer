package simulizer.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.layout.Layouts;
import simulizer.ui.layout.WindowLocation;

public class WindowManager extends Pane {
	// Stores a list of all open windows
	private List<InternalWindow> openWindows = new ArrayList<InternalWindow>();
	private Pane pane = new Pane();
	private String theme = "themes/default"; // Default theme
	private Stage primaryStage;

	public WindowManager(Stage primaryStage) {
		init(primaryStage, 1060, 740);
	}

	public WindowManager(Stage primaryStage, String theme) {
		this.theme = theme;
		init(primaryStage, 1060, 740);
	}

	public WindowManager(Stage primaryStage, String theme, int x, int y) {
		this.theme = theme;
		init(primaryStage, x, y);
	}

	private void init(Stage primaryStage, int x, int y) {
		Scene scene = new Scene(pane, x, y);
		primaryStage.setTitle("Simulizer");
		primaryStage.setScene(scene);
		pane.getStyleClass().add("background");

		MainMenuBar bar = new MainMenuBar(this);
		bar.setMinWidth(1060);
		pane.getChildren().add(bar);

		// Resize menubar to window width
		scene.widthProperty().addListener((a, b, newSceneWidth) -> bar.setMinWidth((double) newSceneWidth));

		setLayout(Layouts.original());

		primaryStage.show();
	}

	public void closeAll() {
		pane.getChildren().removeAll(openWindows);
		openWindows.clear();
	}

	public void addWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			openWindows.add(window);
			window.setTheme(theme);
			pane.getChildren().addAll(window);
		}
	}

	public void setLayout(Layout layout) {
		List<InternalWindow> newOpenWindows = new ArrayList<InternalWindow>();

		// For each new window
		for (WindowLocation location : layout) {
			InternalWindow window = findInternalWindow(location.getWindowEnum());
			window.setBounds(location.getX(), location.getY(), location.getWidth(), location.getHeight());
			newOpenWindows.add(window);
		}

		closeAll();
		pane.getChildren().addAll(newOpenWindows);
		openWindows = newOpenWindows;

		setTheme(theme);
	}

	public void setTheme(String theme) {
		this.theme = theme;
		pane.getStylesheets().add(theme + "/background.css");
		for (InternalWindow window : openWindows)
			window.setTheme(theme);
	}

	public void printWindowLocations() {
		for (InternalWindow w : openWindows) {
			System.out.print(w.getTitle() + ": ");
			for (double s : w.getBounds())
				System.out.print(s + ", ");
			System.out.println();
		}
	}

	public InternalWindow findInternalWindow(WindowEnum window) {
		// Find existing window
		for (InternalWindow w : openWindows)
			if (window.equals(w)) return w;

		// Not found -> Create a new one
		InternalWindow w = window.createNewWindow();
		w.setBounds(5, 35, 1303, 974);
		addWindows(w);
		return w;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

}
