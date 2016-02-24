package simulizer.ui;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import simulizer.assembler.representation.Program;
import simulizer.settings.Settings;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.components.UISimulationListener;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.layout.Layouts;
import simulizer.ui.theme.Themes;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.ui.windows.Logger;

public class WindowManager extends GridPane {
	private Workspace workspace;
	private Stage primaryStage;

	private GridBounds grid;
	private Themes themes;
	private Layouts layouts;
	private Settings settings;

	private CPU cpu = null;
	private Thread cpuThread = null;
	private UISimulationListener simListener = new UISimulationListener(this);

	public WindowManager(Stage primaryStage, Settings settings) {
		this(primaryStage, settings, 1060, 740);
	}

	public WindowManager(Stage primaryStage, Settings settings, int x, int y) {
		this.primaryStage = primaryStage;
		this.settings = settings;
		workspace = new Workspace(this);

		// Create the GridPane to hold MainMenuBar and workspace for InternalWindow
		GridPane.setHgrow(workspace.getPane(), Priority.ALWAYS);
		GridPane.setVgrow(workspace.getPane(), Priority.ALWAYS);
		add(workspace.getPane(), 0, 1);

		// Set up the Primary Stage
		primaryStage.setWidth(x);
		primaryStage.setHeight(y);
		primaryStage.setTitle("Simulizer");

		// Set the theme
		themes = new Themes((String) settings.get("workspace.theme"));
		themes.addThemeableElement(workspace);
		themes.setTheme(themes.getTheme()); // TODO: Remove hack

		// Sets the grid
		grid = new GridBounds((int) settings.get("workspace.grid.horizontal"), (int) settings.get("workspace.grid.vertical"), (double) settings.get("workspace.grid.sensitivity"));
		grid.setWindowSize(workspace.getWidth(), workspace.getHeight());

		// Set the layout
		layouts = new Layouts(workspace);
		layouts.setDefaultLayout();

		// MainMenuBar
		MainMenuBar bar = new MainMenuBar(this);
		GridPane.setHgrow(bar, Priority.ALWAYS);
		add(bar, 0, 0);
	}

	public void show() {
		Scene scene = new Scene(this);
		primaryStage.setScene(scene);
		primaryStage.show();
		workspace.resizeInternalWindows();
		grid.setGridSnap((boolean) settings.get("workspace.grid.enabled"));
		widthProperty().addListener((e) -> grid.setWindowSize(workspace.getWidth(), workspace.getHeight()));
		heightProperty().addListener((e) -> grid.setWindowSize(workspace.getWidth(), workspace.getHeight()));
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

	public GridBounds getGridBounds() {
		return grid;
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
		Logger io = (Logger) workspace.openInternalWindow(WindowEnum.LOGGER);
		stopCPU();

		cpu = new CPU(p, io);
		((HighLevelVisualisation) workspace.openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION)).attachCPU(cpu);
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

	public Workspace getWorkspace() {
		return workspace;
	}

	public Settings getSettings() {
		return settings;

	}
}
