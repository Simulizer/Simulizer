package simulizer.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulizer.assembler.representation.Program;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.data.representation.Word;
import simulizer.ui.components.MainMenuBar;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.layout.Layouts;
import simulizer.ui.layout.WindowLocation;
import simulizer.ui.theme.Theme;
import simulizer.ui.theme.Themes;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.ui.windows.Logger;


public class WindowManager extends Pane {
	// Stores a list of all open windows (may be already done with jfxtras)
	private List<InternalWindow> openWindows = new ArrayList<InternalWindow>();
	private Pane pane = new Pane();
	private Themes themes = new Themes();
	private Layouts layouts = new Layouts(this);
	private Scene scene;
	private Stage primaryStage;
	private CPU cpu;
	private Thread cpuThread;
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
		this.primaryStage = primaryStage;

		cpu = null;
		cpuThread = null;
		simListener = new UISimulationListener(this);

		scene = new Scene(pane, x, y);

		themes.setTheme(theme);
		pane.getStyleClass().add("background");

		MainMenuBar bar = new MainMenuBar(this);
		bar.setMinWidth(1060);
		pane.getChildren().add(bar);

		// Resize menubar to window width
		scene.widthProperty().addListener((a, b, newSceneWidth) -> bar.setMinWidth((double) newSceneWidth));
		setTheme(themes.getTheme());
	}

	public void show() {
		primaryStage.setTitle("Simulizer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public void closeAll() {
		pane.getChildren().removeAll(openWindows);
		openWindows.clear();
	}

	private void addWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			window.setOnCloseAction((e) -> removeWindows(window));
			openWindows.add(window);
			window.setTheme(themes.getTheme());
			pane.getChildren().addAll(window);
			window.ready();
		}
	}

	private void removeWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			if (window.isVisible())
				window.close();
			openWindows.remove(window);
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

		setTheme(themes.getTheme());
	}

	public void setTheme(Theme theme) {
		themes.setTheme(theme);
		pane.getStylesheets().clear();
		pane.getStylesheets().add(theme.getStyleSheet("background.css"));
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
			if (window.equals(w))
				return w;

		// Not found -> Create a new one
		InternalWindow w = window.createNewWindow();
		assert w != null;
		w.setWindowManager(this);
		// TODO: Look for a smarter bounds first (possibly from layout), otherwise maximise the frame
		w.setBounds(10, 35, pane.getWidth() - 20, pane.getHeight() - 45);
		addWindows(w);
		return w;
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

	public List<InternalWindow> getOpenWindows() {
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
		Logger io = (Logger) findInternalWindow(WindowEnum.LOGGER);
		stopCPU();

		cpu = new CPU(p, io);
		((HighLevelVisualisation) findInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION)).attachCPU(cpu);
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
}
