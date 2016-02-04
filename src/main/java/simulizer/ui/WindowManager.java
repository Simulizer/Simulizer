package simulizer.ui;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.windows.CPUVisualiser;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.Logger;
import simulizer.ui.windows.Registers;

public class WindowManager extends Pane {

	// Stores a list of all open windows
	private List<InternalWindow> openWindows = new ArrayList<InternalWindow>();
	private Pane pane = new Pane();
	private String theme;

	public WindowManager(String theme, Stage primaryStage) {
		this.theme = theme;

		Scene scene = new Scene(pane, 1060, 740);
		primaryStage.setTitle("Simulizer");
		primaryStage.setScene(scene);
		pane.getStyleClass().add("background");

		MainMenuBar bar = new MainMenuBar(this);
		bar.setMinWidth(1060);
		pane.getChildren().add(bar);

		// Resize menubar to window width
		// TODO: Replaced with something more elegant
		scene.widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
				bar.setMinWidth((double) newSceneWidth);
			}
		});

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
	// existingWindow.setBounds(w.getLayoutX(), w.getLayoutY(), w.getWidth(), w.getHeight());
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
			System.out.print(w.getWindowTitle() + ": ");
			for (double s : w.getBounds())
				System.out.print(s + ", ");
			System.out.println();
		}
	}

	public void newFile() {
		// TODO: New File
		System.out.println("New File");
	}

	public void loadFile() {
		// TODO Load File
		System.out.println("Load File");
	}

	public void saveFile() {
		// TODO Save File
		System.out.println("Save File");
	}

}
