package simulizer.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.interfaces.InternalWindow;
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
	private String theme;
	private Stage primaryStage;

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

	public void newFile() {
		CodeEditor codeEditor = null;

		for (InternalWindow w : openWindows) {
			if (w.getWindowName().equals(WindowManager.CODE_EDITOR)) {
				codeEditor = (CodeEditor) w;
				break;
			}
		}

		if (codeEditor == null) {
			System.err.println("Code Editor window not found");
			return;
		}

		codeEditor.setCurrentFile(null);
		codeEditor.setFileEdited(false);
		codeEditor.setTitle(WindowManager.CODE_EDITOR + " - New File");
		codeEditor.setText("");
	}

	public void loadFile() {
		CodeEditor codeEditor = (CodeEditor) this.getWindow(WindowManager.CODE_EDITOR);

		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Open an assembly file");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));

		File selectedFile = fc.showOpenDialog(primaryStage);

		// If the user actually selected some files
		if (selectedFile != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile));) {
				String codeIn = "";
				String c;
				boolean first = true;
				while ((c = reader.readLine()) != null) {
					if (!first) {
						codeIn += "\n" + c;
					} else {
						codeIn += c;
						first = false;
					}
				}

				// Save the directory the user last opened (for convenience)
				if (selectedFile.getParent() != null)
					System.setProperty("user.dir", selectedFile.getParent());

				// Save the destination of the current file
				codeEditor.setCurrentFile(selectedFile);

				// Show the code in the editor
				codeEditor.setText(codeIn);
				codeEditor.setFileEdited(false);
				codeEditor.setTitle(WindowManager.CODE_EDITOR + " - " + selectedFile.getName());
				codeEditor.updateTitleEditStatus();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void saveFile() {
		CodeEditor codeEditor = (CodeEditor) this.getWindow(WindowManager.CODE_EDITOR);
		// If no file has been opened (or if the user is working on a new
		// file) then ask the user to select a destination
		if (codeEditor.getCurrentFile() == null) {
			final FileChooser fc = new FileChooser();
			fc.setInitialDirectory(new File(System.getProperty("user.dir")));
			fc.setTitle("Save an assembly file");
			fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));

			codeEditor.setCurrentFile(fc.showSaveDialog(primaryStage));
		}

		// If the destination is specified
		File currentFile = codeEditor.getCurrentFile();
		
		if (currentFile != null) {
			try (PrintWriter writer = new PrintWriter(currentFile);) {
				writer.print(codeEditor.getText());

				codeEditor.setTitle(WindowManager.CODE_EDITOR + " - " + currentFile.getName());
				codeEditor.setFileEdited(false);
				codeEditor.updateTitleEditStatus();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private InternalWindow getWindow(String title) {
		for (InternalWindow w : openWindows) {
			if (w.getWindowName().equals(title)) {
				return w;
			}
		}

		return null;
	}

}
