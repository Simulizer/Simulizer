package simulizer.ui.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Pane;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.layout.WindowLocation;
import simulizer.ui.theme.Theme;
import simulizer.ui.theme.Themeable;

public class Workspace extends Observable implements Themeable {
	private Set<InternalWindow> openWindows = new HashSet<InternalWindow>();
	private final Pane pane = new Pane();
	private final WindowManager wm;

	public Workspace(WindowManager wm) {
		this.wm = wm;
		pane.getStyleClass().add("background");
		wm.widthProperty().addListener(resizeEvent);
		wm.heightProperty().addListener(resizeEvent);
	}

	private ChangeListener<Number> resizeEvent = new ChangeListener<Number>() {
		// Thanks to: http://stackoverflow.com/questions/10773000/how-to-listen-for-resize-events-in-javafx#answer-25812859
		final Timer timer = new Timer();
		TimerTask task = null;
		final long delayTime = 50; // Delay before resize UI

		@Override
		public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
			if (task != null)
				task.cancel();
			task = new TimerTask() {
				@Override
				public void run() {
					resizeInternalWindows();
					task.cancel();
				}
			};
			timer.schedule(task, delayTime);
		}

	};

	public void resizeInternalWindows(){
		double width = pane.getWidth(), height = pane.getHeight();
		if (width > 0 && height > 0)
			for (InternalWindow window : openWindows)
				window.setWorkspaceSize(width, height);
	}
	
	public Pane getPane() {
		return pane;
	}

	public double getWidth() {
		return pane.getWidth();
	}

	public double getHeight() {
		return pane.getHeight();
	}

	public void closeAll() {
		for (InternalWindow window : openWindows)
			if (window.isVisible())
				window.close();
		openWindows.clear();
	}

	public InternalWindow findInternalWindow(WindowEnum window) {
		for (InternalWindow w : openWindows)
			if (window.equals(w))
				return w;
		return null;
	}

	public InternalWindow openInternalWindow(WindowEnum window) {
		InternalWindow w = findInternalWindow(window);
		if (w != null)
			return w;

		// Not found -> Create a new one
		w = window.createNewWindow();
		assert w != null;
		w.setWindowManager(wm);
		wm.getLayouts().setWindowDimentions(w);
		addWindows(w);
		return w;
	}

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

	@Override
	public void setTheme(Theme theme) {
		pane.getStylesheets().clear();
		pane.getStylesheets().add(theme.getStyleSheet("background.css"));
		for (InternalWindow window : openWindows)
			window.setTheme(theme);
	}

	public void closeAllExcept(InternalWindow[] newOpenWindows) {
		List<InternalWindow> keepOpen = new ArrayList<InternalWindow>();
		for (int i = 0; i < newOpenWindows.length; i++)
			keepOpen.add(newOpenWindows[i]);

		List<InternalWindow> close = new ArrayList<InternalWindow>();
		for (InternalWindow window : openWindows)
			if (!keepOpen.contains(window))
				close.add(window);

		for (InternalWindow window : close)
			removeWindows(window);
	}

	public Layout generateLayout(String name) {
		int i = 0;
		WindowLocation[] wls = new WindowLocation[openWindows.size()];
		for (InternalWindow window : openWindows) {
			wls[i] = new WindowLocation(WindowEnum.toEnum(window), window.getLayoutX(), window.getLayoutY(), window.getWidth(), window.getHeight());
			i++;
		}

		return new Layout(name, pane.getWidth(), pane.getHeight(), wls);
	}

	public ReadOnlyDoubleProperty widthProperty() {
		return pane.widthProperty();
	}

	public ReadOnlyDoubleProperty heightProperty() {
		return pane.widthProperty();
	}

	public Settings getSettings() {
		return wm.getSettings();
	}

}
