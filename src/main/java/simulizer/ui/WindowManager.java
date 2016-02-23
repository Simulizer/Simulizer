package simulizer.ui;

import java.util.HashSet;
import java.util.Set;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import simulizer.assembler.representation.Program;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.components.UISimulationListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.layout.Layouts;
import simulizer.ui.theme.Theme;
import simulizer.ui.theme.Themes;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.ui.windows.Logger;

public class WindowManager extends Pane {
	// Stores a set of all open windows
	private Set<InternalWindow> openWindows = new HashSet<InternalWindow>();

	private Pane pane = new Pane();
	private Stage primaryStage;

	private GridBounds grid;
	private Themes themes;
	private Layouts layouts;

	private CPU cpu = null;
	private Thread cpuThread = null;
	UISimulationListener simListener;

	public WindowManager(Stage primaryStage) {
		init(primaryStage, "default", 1060, 740);
	}

	public WindowManager(Stage primaryStage, String theme) {
		this(primaryStage, theme, 1060, 740);
	}

	public WindowManager(Stage primaryStage, String theme, int x, int y) {
		init(primaryStage, theme, x, y);
	}

	private void init(Stage primaryStage, String theme, int x, int y) {
		GridPane gridPane = new GridPane();
		//getChildren().add(gridPane);
		GridPane.setHgrow(pane, Priority.ALWAYS);
		gridPane.add(pane, 0, 1);

		Scene scene = new Scene(gridPane, x, y);
		simListener = new UISimulationListener(this);

		this.primaryStage = primaryStage;
		primaryStage.setWidth(x);
		primaryStage.setHeight(y);
		primaryStage.setTitle("Simulizer");
		primaryStage.setScene(scene);

		// Set the theme
		themes = new Themes();
		themes.setTheme(theme);
		setTheme(themes.getTheme());

		// Sets the grid
		grid = new GridBounds(4, 2, 30);
		grid.setWindowSize(scene.getWidth(), scene.getHeight());

		// Set the layout
		layouts = new Layouts(this);
		layouts.setDefaultLayout();

		// MainMenuBar
		MainMenuBar bar = new MainMenuBar(this);
		gridPane.add(bar, 0, 0);

	}

	public void show() {
		primaryStage.show();

		// Snap to grid after the stage is shown
		grid.setGridSnap(true);
	}

	public void closeAll() {
		pane.getChildren().removeAll(openWindows);
		openWindows.clear();
	}

	public void addWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			if (!openWindows.contains(window)) {
				window.setOnCloseAction((e) -> removeWindows(window));
				openWindows.add(window);
				window.setTheme(themes.getTheme());
				pane.getChildren().addAll(window);
				window.setGridBounds(grid);
				window.ready();
			} else {
				System.out.println("Window already exists: " + window.getTitle());
			}
		}
	}

	private void removeWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			if (window.isVisible())
				window.close();
			openWindows.remove(window);
		}
	}

	public void setTheme(Theme theme) {
		themes.setTheme(theme);
		getStylesheets().clear();
		getStylesheets().add(theme.getStyleSheet("background.css"));
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

	public InternalWindow openInternalWindow(WindowEnum window) {
		InternalWindow w = findInternalWindow(window);
		if (w != null)
			return w;

		// Not found -> Create a new one
		w = window.createNewWindow();
		assert w != null;
		w.setWindowManager(this);
		layouts.setWindowDimentions(w);
		addWindows(w);
		return w;
	}

	public InternalWindow findInternalWindow(WindowEnum window) {
		// Find existing window
		for (InternalWindow w : openWindows)
			if (window.equals(w))
				return w;
		return null;
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public Themes getThemes() {
		return themes;
	}

	public Layouts getLayouts() {
		return layouts;
	}

	public Set<InternalWindow> getOpenWindows() {
		return openWindows;
	}

	public Pane getPane() {
		return pane;
	}

	public void stopCPU() {
		if (cpuThread != null) {
			System.out.println("Terminating running program");
			cpu.stopRunning();
			try {
				cpuThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			cpuThread = null;
			System.out.println("Running program terminated");
		}
	}

	public void runProgram(Program p) {
		Logger io = (Logger) openInternalWindow(WindowEnum.LOGGER);
		stopCPU();

		cpu = new CPU(p, io);
		((HighLevelVisualisation) openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION)).attachCPU(cpu);
		cpu.registerListener(simListener);

		io.clear();

		cpuThread = new Thread(new Task<Object>() {
			@Override
			protected Object call() throws Exception {
				try {
					cpu.setClockSpeed(250);
					cpu.runProgram();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		cpuThread.start();
	}

	public Word[] getRegisters() {
		return cpu.getRegisters();
	}

	public CPU getCPU() {
		return cpu;
	}

	public void closeAllExcept(InternalWindow... newOpenWindows) {

	}
}
