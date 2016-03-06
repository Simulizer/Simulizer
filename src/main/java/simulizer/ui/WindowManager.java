package simulizer.ui;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

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
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.CPUPipeline;
import simulizer.simulation.cpu.user_interaction.LoggerIO;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.components.UISimulationListener;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.GridBounds;
import simulizer.ui.layout.Layouts;
import simulizer.ui.theme.Themes;
import simulizer.ui.windows.CPUVisualisation;
import simulizer.ui.windows.Editor;
import simulizer.utils.UIUtils;

public class WindowManager extends GridPane {
	private Stage primaryStage;

	private Workspace workspace;
	private GridBounds grid;
	private Themes themes;
	private Layouts layouts;
	private Settings settings;

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

		if ((boolean) settings.get("simulation.pipelined")) {
			cpu = new CPUPipeline(io);
			cpu.registerListener(simListener);
		} else {
			cpu = new CPU(io);
			cpu.registerListener(simListener);
		}

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
			new Thread(() -> {
				try {
					for (int i = 0; i < 3; i++) {
						Thread.sleep(50);
						Platform.runLater(workspace::resizeInternalWindows);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}).start();
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
				e.printStackTrace();
			} finally {
				cpuThread = null;
				System.out.println("Simulation thread closed");
			}
		}
	}

	public void assembleAndRun() {
		primaryStage.setTitle("Simulizer - Assembling Program");
		new Thread(() -> {
			StoreProblemLogger log = new StoreProblemLogger();
			Editor editor = (Editor) getWorkspace().openInternalWindow(WindowEnum.EDITOR);

			final FutureTask<String> getProgramText = new FutureTask<>(editor::getText);
			
			Platform.runLater(getProgramText);

			try {
				final Program p = Assembler.assemble(getProgramText.get(), log);
				// doing as little as possible in the FX thread
				Platform.runLater(() -> {
					// if no problems, has the effect of clearing
					editor.setProblems(log.getProblems());
					if (p == null) {
						int size = log.getProblems().size();
						UIUtils.showErrorDialog("Could Not Run", "The Program Contains " + (size == 1 ? "An Error!" : size + " Errors!"), "You must fix them before you can\nexecute the program.");
					}
				});

				if(p != null) {
					runProgram(p); // spawns another thread
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} finally {
				Platform.runLater(() -> primaryStage.setTitle("Simulizer"));
			}

		} , "Assemble").start();
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
						cpu.setClockSpeed((Integer) settings.get("simulation.default-clock-speed"));
						cpu.runProgram();
					} catch (Exception e) {
						e.printStackTrace();
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

	public LoggerIO getIO() {
		return io;
	}

	public AnnotationManager getAnnotationManager() {
		return annotationManager;
	}

	public void setPipelined(boolean pipelined) {
		if (pipelined) {
			cpu = new CPUPipeline(io);
			cpu.registerListener(simListener);
		} else {
			cpu = new CPU(io);
			cpu.registerListener(simListener);
		}
	}

	public void shutdown() {
		workspace.closeAll();
		if (!workspace.hasWindowsOpen())
			primaryStage.close();
	}
}
