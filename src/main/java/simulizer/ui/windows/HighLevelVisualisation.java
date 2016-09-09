package simulizer.ui.windows;

import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Pair;
import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.FrameModel;
import simulizer.highlevel.models.HLVisualManager;
import simulizer.highlevel.models.HLVisualManager.Action;
import simulizer.highlevel.models.HanoiModel;
import simulizer.highlevel.models.ListModel;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.components.highlevel.FrameVisualiser;
import simulizer.ui.components.highlevel.ListVisualiser;
import simulizer.ui.components.highlevel.TowerOfHanoiVisualiser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

/**
 *
 * Allows several high-level visualisations to be
 * displayed in separate tabs.
 *
 * @author Michael Oultram
 *
 */
public class HighLevelVisualisation extends InternalWindow implements Observer {
	private double width = 400;
	private double height = 300;

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
			for (Tab tab : tabs.getTabs())
				((DataStructureVisualiser) tab.getContent()).repaint();
		});

		getContentPane().heightProperty().addListener((o, old, newValue) -> {
			height = newValue.doubleValue();
			for (Tab tab : tabs.getTabs())
				((DataStructureVisualiser) tab.getContent()).repaint();
		});
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.0, 0.0, 0.3, 0.2);
	}

	@Override
	public void ready() {
		// Listened for visualisation changes
		HLVisualManager hlvisual = getWindowManager().getHLVisualManager();
		hlvisual.addObserver(this);

		// Add already opened visualisations
		hlvisual.getModels().forEach(this::addNewVisualisation);
		super.ready();
	}

	@Override
	public void close() {
		super.close();

		// Close all tabs
		tabs.getTabs().forEach(t -> removeTab((DataStructureVisualiser) t.getContent()));

		// Stop listening to visualisation changes
		getWindowManager().getHLVisualManager().deleteObserver(this);
	}

	/**
	 * Adds a visualisation to the tabs
	 *
	 * @param vis
	 *            the visualisation to add
	 */
	public void addTab(DataStructureVisualiser vis) {
		Tab tab = new Tab(vis.getName());
		tab.setContent(vis);

		// JavaFX doesn't appear to have an event to handle this
		Timer t = new Timer(true);
		t.scheduleAtFixedRate(new TimerTask() {
			private int runs = 0;

			@Override
			public void run() {
				if (runs++ == 10) t.cancel();
				Platform.runLater(vis::repaint);
			}
		}, 0, 10);

		tab.setOnClosed(e -> vis.close());
		Platform.runLater(() -> tabs.getTabs().add(tab));
	}

	/**
	 * Removes a model from the tabs
	 *
	 * @param model
	 *            the model to remove
	 */
	private void removeTab(DataStructureModel model) {
		tabs.getTabs().stream().filter(t -> ((DataStructureVisualiser) t.getContent()).getModel() == model).forEach(t -> removeTab(t));
	}

	/**
	 * Removes a visualisation from the tabs
	 *
	 * @param vis
	 *            the visualisation to remove
	 */
	public void removeTab(DataStructureVisualiser vis) {
		tabs.getTabs().stream().filter(t -> t.getContent() == vis).forEach(this::removeTab);
	}

	/**
	 * Removes a tab from the tabs
	 *
	 * @param tab
	 *            the tab to remove
	 */
	private void removeTab(Tab tab) {
		((DataStructureVisualiser) tab.getContent()).close();
		Platform.runLater(() -> tabs.getTabs().remove(tab));
	}

	/**
	 * @return the window width
	 */
	public double getWindowWidth() {
		return width;
	}

	/**
	 * @return the window height
	 */
	public double getWindowHeight() {
		return height;
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().add(theme.getStyleSheet("highlevel.css"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void update(Observable arg0, Object obj) {
		if (obj == null) {
			System.out.println("HLVisualisation Window upadate was null. If you see this message then tell Michael");
			return;
		}
		Pair<Action, DataStructureModel> change = (Pair<Action, DataStructureModel>) obj;
		if (change.getKey() == Action.CREATED) {
			addNewVisualisation(change.getValue());
		} else if (change.getKey() == Action.DELETED) {
			removeTab(change.getValue());
		}
	}

	/**
	 * Adds a new visualisation
	 *
	 * @param model
	 *            the model to create the visualisation for
	 */
	private void addNewVisualisation(DataStructureModel model) {
		DataStructureVisualiser vis = null;
		switch (model.modelType()) {
			case FRAME:
				vis = new FrameVisualiser((FrameModel) model, this);
				break;
			case HANOI:
				vis = new TowerOfHanoiVisualiser((HanoiModel) model, this);
				break;
			case LIST:
				vis = new ListVisualiser((ListModel) model, this);
				break;
		}
		if (model.isVisible()) vis.show();
	}

}
