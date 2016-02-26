package simulizer.ui;

import javafx.application.Platform;
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
		this.primaryStage = primaryStage;
		this.settings = settings;
		workspace = new Workspace(this);

		// Create the GridPane to hold MainMenuBar and workspace for InternalWindow
		GridPane.setHgrow(workspace.getPane(), Priority.ALWAYS);
		GridPane.setVgrow(workspace.getPane(), Priority.ALWAYS);
		add(workspace.getPane(), 0, 1);

		// Set up the Primary Stage
		primaryStage.setWidth((int) settings.get("window.width"));
		primaryStage.setHeight((int) settings.get("window.height"));
		primaryStage.setTitle("Simulizer");
		primaryStage.setMinWidth(300);
		primaryStage.setMinHeight(300);

		// Set the theme
		themes = new Themes((String) settings.get("workspace.theme"));
		themes.addThemeableElement(workspace);
		themes.setTheme(themes.getTheme()); // TODO: Remove hack

		// @formatter:off Sets the grid
		if((boolean) settings.get("workspace.grid.enabled"))
			grid = new GridBounds((int) settings.get("workspace.grid.horizontal"), 
								  (int) settings.get("workspace.grid.vertical"), 
								  (double) settings.get("workspace.grid.sensitivity"), 
								  (int) settings.get("workspace.grid.delay"));

		// @formatter:on Set the layout
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
		Platform.runLater(() -> workspace.resizeInternalWindows());

		if (grid != null) {
			grid.setWindowSize(workspace.getWidth(), workspace.getHeight());
			grid.setGridSnap((boolean) settings.get("workspace.grid.enabled"));
			widthProperty().addListener((e) -> grid.setWindowSize(workspace.getWidth(), workspace.getHeight()));
			heightProperty().addListener((e) -> grid.setWindowSize(workspace.getWidth(), workspace.getHeight()));
		}
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

		cpu = new CPU(io);
		HighLevelVisualisation hlv = (HighLevelVisualisation) workspace.findInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		if (hlv != null)
			hlv.attachCPU(cpu);
		cpu.loadProgram(p);
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
		}, "CPU-Thread");
		cpuThread.setDaemon(true);
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
