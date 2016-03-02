package simulizer.ui;

import java.io.IOException;

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

		if ((boolean) settings.get("simulation.pipelined"))
			cpu = new CPUPipeline(io);
		else
			cpu = new CPU(io);

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

	public void assembleAndRun() {
		primaryStage.setTitle("Simulizer - Assembling Program");
		new Thread(() -> {
			StoreProblemLogger log = new StoreProblemLogger();
			Editor editor = (Editor) getWorkspace().openInternalWindow(WindowEnum.EDITOR);

			Program p = Assembler.assemble(editor.getText(), log);

			// if no problems, has the effect of clearing
			Platform.runLater(() -> {
				editor.setProblems(log.getProblems());
				if (p != null) {
					runProgram(p);
				} else {
					int size = log.getProblems().size();
					UIUtils.showErrorDialog("Could Not Run", "The Program Contains " + (size == 1 ? "An Error!" : size + " Errors!"), "You must fix them before you can\nexecute the program.");
				}
				primaryStage.setTitle("Simulizer");
			});
		} , "Assemble").start();
	}

	public void runProgram(Program p) {
		if (p != null) {
			stopCPU();

			cpu.loadProgram(p);
			// TODO: maybe don't re-register the listeners
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
		} else {
			cpu = new CPU(io);
		}
	}

	public void shutdown() {
		workspace.closeAll();
		if (!workspace.hasWindowsOpen())
			primaryStage.close();
	}
}
