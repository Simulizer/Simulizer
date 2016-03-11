package simulizer.ui;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import simulizer.annotations.AnnotationManager;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Program;
import simulizer.settings.Settings;
import simulizer.simulation.cpu.CPUChangedListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.CPUPipeline;
import simulizer.simulation.cpu.user_interaction.LoggerIO;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.components.UISimulationListener;
import simulizer.ui.components.Workspace;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.layout.Layouts;
import simulizer.ui.theme.Themes;
import simulizer.utils.UIUtils;

public class WindowManager extends GridPane {
	private Stage primaryStage;

	private Workspace workspace;
	private GridBounds grid;
	private Themes themes;
	private Layouts layouts;
	private Settings settings;

	private Set<CPUChangedListener> cpuChangedListeners = new HashSet<>();
	private CPU cpu = null;
	private LoggerIO io;
	private Thread cpuThread = null;
	private UISimulationListener simListener = new UISimulationListener(this);
	private AnnotationManager annotationManager;

	public WindowManager(Stage primaryStage, Settings settings) throws IOException {
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
		primaryStage.setOnCloseRequest(e -> {
			e.consume();
			shutdown();
		});

		// Creates CPU Simulation
		io = new LoggerIO(workspace);
		newCPU((boolean) settings.get("simulation.pipelined"));

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

		// Disable ALT Key to prevent menu bar from stealing
		// the editor's focus
		addEventHandler(KeyEvent.KEY_PRESSED, (e) -> {
			if (e.isAltDown())
				e.consume();
		});

		annotationManager = new AnnotationManager(this);
	}

	public void show() {
		Scene scene = new Scene(this);
		primaryStage.setScene(scene);

		// a hack to make the layout load properly
		primaryStage.setOnShown((e) -> {
			Thread layoutFixThread = new Thread(() -> {
				try {
					for (int i = 0; i < 100; i++) {
						Thread.sleep(50);
						Platform.runLater(workspace::resizeInternalWindows);
					}
				} catch (InterruptedException e1) {
					UIUtils.showExceptionDialog(e1);
				}
			}, "Layout-Fix-Thread");
			layoutFixThread.setDaemon(true);
			layoutFixThread.start();
		});
		primaryStage.show();

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

	public void stopSimulation() {
		if (cpuThread != null) {
			cpu.stopRunning();
			try {
				System.out.println("Waiting for the simulation thread to close");
				cpuThread.join();
			} catch (InterruptedException e) {
				UIUtils.showExceptionDialog(e);
			} finally {
				cpuThread = null;
				System.out.println("Simulation thread closed");
			}
		}
	}

	public void assembleAndRun() {
		primaryStage.setTitle("Simulizer - Assembling Program");

		getWorkspace().openEditorWithCallback((editor) -> {
			final String programText = editor.getText();

			// avoid lots of work on the JavaFX thread
			Thread assembleThread = new Thread(() -> {
				StoreProblemLogger log = new StoreProblemLogger();

				try {
					final Program p = Assembler.assemble(programText, log);
					// doing as little as possible in the FX thread
					getWorkspace().openEditorWithCallback((editor2) -> {
						// if no problems, has the effect of clearing
						editor2.setProblems(log.getProblems());
						if (p == null) {
							int size = log.getProblems().size();
							UIUtils.showErrorDialog("Could Not Run", "The Program Contains " + (size == 1 ? "An Error!" : size + " Errors!"), "You must fix them before you can\nexecute the program.");
							UIUtils.closeAssemblingDialog();
						}
					});

					if (p != null) {
						runProgram(p); // spawns another thread
					}
				} finally {
					Platform.runLater(() -> primaryStage.setTitle("Simulizer"));
				}

			} , "Assemble");
			assembleThread.setDaemon(true);
			assembleThread.start();
		});
	}

	public void runProgram(Program p) {
		if (p != null) {
			stopSimulation();

			cpu.loadProgram(p);

			io.clear();

			cpuThread = new Thread(new Task<Object>() {
				@Override
				protected Object call() throws Exception {
					try {
						cpu.runProgram();
					} catch (Exception e) {
						UIUtils.showExceptionDialog(e);
					}
					return null;
				}
			}, "CPU-Thread");
			cpuThread.setDaemon(true);
			cpuThread.start();
		} else {
			throw new NullPointerException();
		}
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

	public LoggerIO getIO() {
		return io;
	}

	public AnnotationManager getAnnotationManager() {
		return annotationManager;
	}

	public void newCPU(boolean pipelined) {
		if (cpu != null) {
			cpu.shutdown();
		}

		if (pipelined) {
			cpu = new CPUPipeline(io);
		} else {
			cpu = new CPU(io);
		}
		cpu.registerListener(simListener);
		cpu.setCycleFreq((Integer) settings.get("simulation.default-CPU-frequency"));

		for (CPUChangedListener listener : cpuChangedListeners) {
			listener.cpuChanged(cpu);
		}
	}

	public void addCPUChangedListener(CPUChangedListener listener) {
		cpuChangedListeners.add(listener);
	}

	public void removeCPUChangedListener(CPUChangedListener listener) {
		cpuChangedListeners.remove(listener);
	}

	public void shutdown() {
		cpu.shutdown();
		workspace.closeAll();
		if (!workspace.hasWindowsOpen())
			primaryStage.close();
	}
}
