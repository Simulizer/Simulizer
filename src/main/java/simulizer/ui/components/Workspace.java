package simulizer.ui.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.layout.WindowLocation;
import simulizer.ui.theme.Theme;
import simulizer.ui.theme.Themeable;
import simulizer.ui.windows.Editor;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

/**
 * The workspace is the container where all InternalWindows are stored and handles opening/closing InternalWindows.
 * 
 * @author Michael
 *
 */
public class Workspace extends Observable implements Themeable {
	private Set<InternalWindow> openWindows = new HashSet<InternalWindow>();
	private final Pane pane = new Pane();
	private WindowManager wm = null;

	private static class ResizeListener implements ChangeListener<Object> {
		private final Workspace w;
		private ScheduledExecutorService executor;
		private ScheduledFuture<?> shortTask;
		private ScheduledFuture<?> longTask;
		private int delay;

		public ResizeListener(Workspace w, int delay) {
			this.w = w;
			executor = Executors.newSingleThreadScheduledExecutor(
					new ThreadUtils.NamedThreadFactory("Window-Resizing"));
			shortTask = null;
			longTask = null;
			this.delay = delay;
		}

		@Override
		public synchronized void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
			if(shortTask != null)
				shortTask.cancel(true);
			if(longTask != null)
				longTask.cancel(true);

			shortTask = executor.schedule(() -> Platform.runLater(w::resizeInternalWindows),
					delay, TimeUnit.MILLISECONDS);
			longTask = executor.schedule(() -> Platform.runLater(w::resizeInternalWindows),
					1, TimeUnit.SECONDS);
		}
	}

	/**
	 * A workspace holds all the Internal Windows
	 *
	 * @param wm
	 *            The stage to listen for resize events
	 */
	public Workspace(WindowManager wm) {
		this.wm = wm;
		pane.getStyleClass().add("background");
		if ((boolean) wm.getSettings().get("workspace.scale-ui.enabled")) {
			int delay = (int) wm.getSettings().get("workspace.scale-ui.delay");
			ResizeListener r = new ResizeListener(this, delay);

			// Register event listeners
			Stage window = wm.getPrimaryStage();

			window.widthProperty().addListener(r);
			window.heightProperty().addListener(r);
			window.fullScreenProperty().addListener(r);
			window.maximizedProperty().addListener(r);
		}
	}

	/**
	 * Notifies all open Internal Windows to recalculate their size and positioning
	 */
	public void resizeInternalWindows() {
		double width = pane.getWidth(), height = pane.getHeight();
		if (width > 0 && height > 0)
			for (InternalWindow window : openWindows)
				window.setWorkspaceSize(width, height);
	}

	/**
	 * @return the content pane
	 */
	public Pane getPane() {
		return pane;
	}

	/**
	 * @return the workspace width
	 */
	public double getWidth() {
		return pane.getWidth();
	}

	/**
	 * @return the workspace height
	 */
	public double getHeight() {
		return pane.getHeight();
	}

	/**
	 * Closes all open Internal Windows
	 */
	public void closeAll() {
		Iterator<InternalWindow> windows = openWindows.iterator();
		while (windows.hasNext()) {
			InternalWindow window = windows.next();
			window.close();
			if (window.isClosed())
				windows.remove();
		}
	}

	/**
	 * Finds an Internal Window if it is already open. Returns null if window is not open
	 *
	 * @param window
	 *            The Internal Window to find
	 * @return The internal window if already open
	 */
	public InternalWindow findInternalWindow(WindowEnum window) {
		// TODO: I (Matt) have (very occasionally) gotten a
		// ConcurrentModificationException here, stemming from getting the editor in
		// UISimulationListener just after the simulation starts (it might be the
		// HLVis window opening at the same time which might be the problem)
		Iterator<InternalWindow> windows = openWindows.iterator();
		while (windows.hasNext()) {
			InternalWindow w = windows.next();
			if (window.equals(w))
				return w;
		}
		return null;
	}

	/**
	 * Opens an Internal Window if it is not already open. Returns the open Internal Window if it is already open
	 *
	 * @param window
	 *            The Internal Window to find
	 * @return The internal window
	 */
	public InternalWindow openInternalWindow(WindowEnum window) {
		InternalWindow w = findInternalWindow(window);
		if (w != null)
			return w;

		// Not found -> Create a new one
		InternalWindow w2 = window.createNewWindow();
		assert w2 != null;
		w2.setWindowManager(wm);
		wm.getLayouts().setWindowDimentions(w2);
		Platform.runLater(() -> addWindows(w2));
		return w2;
	}

	/**
	 * Do something with the editor, opening a new window if one isn't already open
	 *
	 * Explanation:
	 * The editor must load a HTML page and setup its members after the page has
	 * loaded. JavaFX requires that the loading be done asynchronously, which
	 * makes it impossible to cleanly wait for the initialisation to finish
	 * while on the JavaFX thread (because the act of waiting while on the
	 * JavaFX thread will block the loading its-self from starting). Using a
	 * callback mechanism is the only guaranteed correct solution as far as I
	 * know.
	 *
	 * @param callback
	 *            the operation to perform with the editor (will be executed on the JavaFX thread)
	 */
	public void openEditorWithCallback(Consumer<Editor> callback) {
		Editor e = (Editor) findInternalWindow(WindowEnum.EDITOR);

		// most of the contents of this method have to be done on the FX thread anyway
		// and this method should also be resilient to being run on any thread
		if(!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> openEditorWithCallback(callback));
			return;
		}

		if (e != null && e.hasLoaded()) {
			callback.accept(e);
		} else {
			// perform the job of openInternalWindow
			e = (Editor) WindowEnum.EDITOR.createNewWindow();
			final Editor finalE = e;
			assert e != null;
			e.setWindowManager(wm);
			wm.getLayouts().setWindowDimentions(e);
			// starts the page loading
			addWindows(finalE);

			// wait for the page to load (crucially: on a non JavaFX thread)
			Thread waiting = new Thread(() -> {
				try {
					while (!finalE.hasLoaded()) {
						Thread.sleep(20);
					}

					Platform.runLater(() -> callback.accept(finalE));
				} catch (InterruptedException ex) {
					UIUtils.showExceptionDialog(ex);
				}
			} , "Editor-Waiting-For-Load");
			waiting.setDaemon(true);
			waiting.start();
		}
	}

	/**
	 * Adds Internal Windows to the workspace (use openInternalWindow instead)
	 *
	 * @param windows
	 *            List of windows to add to the workspace
	 */
	public void addWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			if (!openWindows.contains(window)) {
				window.setOnCloseAction((e) -> removeWindows(window));
				openWindows.add(window);
				window.setTheme(wm.getThemes().getTheme());
				pane.getChildren().addAll(window);
				window.setGridBounds(wm.getGridBounds());
				window.ready();
			} else {
				UIUtils.showErrorDialog("Problem Opening Window", "Tried to add a window which already exists: " + window.getTitle());
			}
		}
	}

	/**
	 * Removes Internal Windows from the workspace
	 *
	 * @param windows
	 *            List of Internal Windows to close
	 */
	private void removeWindows(InternalWindow... windows) {
		for (InternalWindow window : windows) {
			window.close();
			if (window.isClosed())
				openWindows.remove(window);
		}
	}

	@Override
	public void setTheme(Theme theme) {
		pane.getStylesheets().clear();
		pane.getStylesheets().add(theme.getStyleSheet("background.css"));
		for (InternalWindow window : openWindows)
			window.setTheme(theme);
	}

	/**
	 * Closes all open Internal Windows except theseWindows
	 *
	 * @param theseWindows
	 *            The Internal Windows to keep open
	 */
	public void closeAllExcept(InternalWindow[] theseWindows) {
		List<InternalWindow> keepOpen = new ArrayList<InternalWindow>();
		for (int i = 0; i < theseWindows.length; i++)
			keepOpen.add(theseWindows[i]);

		List<InternalWindow> close = new ArrayList<InternalWindow>();
		for (InternalWindow window : openWindows)
			if (!keepOpen.contains(window))
				close.add(window);

		for (InternalWindow window : close)
			removeWindows(window);
	}

	/**
	 * Will generate a Layout of the current workspace
	 *
	 * @param name
	 *            The name of the layout
	 * @return The layout of the current workspace
	 */
	public Layout generateLayout(String name) {
		int i = 0;
		WindowLocation[] wls = new WindowLocation[openWindows.size()];
		for (InternalWindow window : openWindows) {
			// @formatter:off
			wls[i] = new WindowLocation(WindowEnum.toEnum(window),
										window.getLayoutX() / getWidth(),
										window.getLayoutY() / getHeight(),
										window.getWidth() / getWidth(),
										window.getHeight() / getHeight());
			// @formatter:on
			i++;
		}

		return new Layout(name, wls);
	}

	/**
	 * @return the pane's width property
	 */
	public ReadOnlyDoubleProperty widthProperty() {
		return pane.widthProperty();
	}

	/**
	 * @return the pane's height property
	 */
	public ReadOnlyDoubleProperty heightProperty() {
		return pane.widthProperty();
	}

	/**
	 * @return the settings object
	 */
	public Settings getSettings() {
		return wm.getSettings();
	}

	/**
	 * @return true if any InternalWindow is open
	 */
	public boolean hasWindowsOpen() {
		return !openWindows.isEmpty();
	}

}
