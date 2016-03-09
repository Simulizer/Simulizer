package simulizer.ui.windows;

import java.util.Iterator;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.components.highlevel.FrameVisualiser;
import simulizer.ui.components.highlevel.ListVisualiser;
import simulizer.ui.components.highlevel.TowerOfHanoiVisualiser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private double width = 400;
	private double height = 300;

	private DataStructureVisualiser visualiser;
	private TabPane tabs;

	public HighLevelVisualisation() {
		// TODO remove this
		setCache(false);

		// TODO check if all this `setXWidth/Height()` stuff is needed
		tabs = new TabPane();
		tabs.setSide(Side.BOTTOM);
		tabs.setCursor(Cursor.DEFAULT);
		getContentPane().getChildren().add(tabs);

		setMinWidth(width);
		setMinHeight(200);

		getContentPane().widthProperty().addListener((o, old, newValue) -> {
			width = newValue.doubleValue();
			if (visualiser != null) {
				visualiser.resize();
			}
		});

		getContentPane().heightProperty().addListener((o, old, newValue) -> {
			height = newValue.doubleValue();
			if (visualiser != null) {
				visualiser.resize();
			}
		});
	}

	public DataStructureVisualiser openVisualisation(String visualiser, boolean show) {
		DataStructureVisualiser vis = null;
		switch (visualiser) {
			case "tower-of-hanoi":
				vis = new TowerOfHanoiVisualiser(this, 0);
				break;
			case "list":
				vis = new ListVisualiser(this);
				break;
			case "frame":
				vis = new FrameVisualiser(this);
				break;
			default:
				throw new IllegalArgumentException();
		}
		if (show) 
			vis.show();
		return vis;
	}

	public void addTab(DataStructureVisualiser vis) {
		Tab tab = new Tab(vis.getName());
		tab.setContent(vis);
		Platform.runLater(() -> tabs.getTabs().add(tab));
	}

	public void removeTab(DataStructureVisualiser vis) {
		Iterator<Tab> iterator = tabs.getTabs().iterator();
		while (iterator.hasNext()) {
			Tab t = iterator.next();
			if (t.getContent() == visualiser)
				Platform.runLater(() -> tabs.getTabs().remove(t));
		}
	}

	public void closeVisualisation(DataStructureVisualiser visualiser) {
		removeTab(visualiser);
	}

	public double getWindowWidth() {
		return width;
	}

	public double getWindowHeight() {
		return height;
	}

	public DataStructureVisualiser getVisualiser() {
		return this.visualiser;
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().add(theme.getStyleSheet("highlevel.css"));
	}

}
