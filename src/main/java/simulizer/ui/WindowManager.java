package simulizer.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.CPUVisualiser;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.Logger;
import simulizer.ui.windows.Registers;

public class WindowManager extends Pane {
	public static final String CODE_EDITOR = "Code Editor";
	public static final String REGISTERS = "Registers";
	public static final String CPU_VISUALISER = "CPU Visualiser";
	public static final String LOGGER = "Logger";

	// Stores a list of all open windows
	private List<InternalWindow> openWindows = new ArrayList<InternalWindow>();
	private Pane pane = new Pane();
	private String theme = "my-theme"; // Default theme
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

		defaultLayout();

		primaryStage.show();
	}

	// TODO: Replace temporary layout fix
	public void beforeLayout() {
		pane.getChildren().removeAll(openWindows);
		openWindows.clear();
	}

	// TODO: Replace temporary layout fix
	public void afterLayout() {
		setTheme(theme);
		pane.getChildren().addAll(openWindows);
	}

	public void addWindow(InternalWindow window) {
		openWindows.add(window);
		window.setTheme(theme);
		pane.getChildren().addAll(window);
	}

	// Not yet working
	// private void updateLayout(InternalWindow... ws) {
	// pane.getChildren().removeAll(openWindows);
	// List<InternalWindow> newOpenWindows = new ArrayList<InternalWindow>();
	//
	// // For each new window
	// for (InternalWindow w : ws) {
	// // Check if it is already visible
	// boolean found = false;
	// for (InternalWindow existingWindow : openWindows) {
	// if (w.getClass().equals(existingWindow.getClass())) {
	// existingWindow.setBounds(w.getLayoutX(), w.getLayoutY(), w.getWidth(),
	// w.getHeight());
	// newOpenWindows.add(existingWindow);
	// found = true;
	// }
	// }
	// // If not, add it.
	// if (!found) {
	// pane.getChildren().add(w);
	// newOpenWindows.add(w);
	// }
	// }
	//
	// pane.getChildren().addAll(newOpenWindows);
	// openWindows = newOpenWindows;
	//
	// setTheme(theme);
	// }

	public void defaultLayout() {
		beforeLayout();

		// Load Code Editor
		CodeEditor editor = new CodeEditor();
		editor.setTitle(WindowManager.CODE_EDITOR + " - New File");
		editor.setBounds(20, 35, 400, 685);
		openWindows.add(editor);

		// Load the visualisation
		CPUVisualiser cpu = new CPUVisualiser();
		cpu.setBounds(440, 35, 600, 400);
		openWindows.add(cpu);

		// Load Registers
		Registers registers = new Registers();
		registers.setBounds(440, 440, 600, 280);
		openWindows.add(registers);

		afterLayout();
	}

	public void alternativeLayout() {
		beforeLayout();

		// Load Code Editor
		CodeEditor editor = new CodeEditor();
		editor.setBounds(5, 35, 1303, 974);
		openWindows.add(editor);

		// Load Registers
		Registers registers = new Registers();
		registers.setBounds(1315, 428, 600, 185);
		openWindows.add(registers);

		// Load the visualisation
		CPUVisualiser cpu = new CPUVisualiser();
		cpu.setBounds(1315, 35, 600, 380);
		openWindows.add(cpu);

		// Load the logger
		Logger logger = new Logger();
		logger.setBounds(1315, 625, 600, 380);
		openWindows.add(logger);

		afterLayout();
	}

	public void setTheme(String theme) {
		this.theme = theme;
		pane.getStylesheets().add(theme + "/background.css");
		for (InternalWindow window : openWindows)
			window.setTheme(theme);
	}

	public void printWindowLocations() {
		for (InternalWindow w : openWindows) {
			System.out.print(w.getWindowName() + ": ");
			for (double s : w.getBounds())
				System.out.print(s + ", ");
			System.out.println();
		}
	}

	public InternalWindow findInternalWindow(WindowEnum window) {
		// Find existing window
		for (InternalWindow w : openWindows)
			if (window.is(w)) return w;

		// Not found -> Create a new one
		InternalWindow w = window.createNewWindow();
		w.setBounds(5, 35, 1303, 974);
		addWindow(w);
		return w;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

}
