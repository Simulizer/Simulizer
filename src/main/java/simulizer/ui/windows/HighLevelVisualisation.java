package simulizer.ui.windows;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.util.Pair;
import simulizer.highlevel.models.Action;
import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.FrameModel;
import simulizer.highlevel.models.HLVisualManager;
import simulizer.highlevel.models.HanoiModel;
import simulizer.highlevel.models.ListModel;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.components.highlevel.FrameVisualiser;
import simulizer.ui.components.highlevel.ListVisualiser;
import simulizer.ui.components.highlevel.TowerOfHanoiVisualiser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

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
			Iterator<Tab> i = tabs.getTabs().iterator();
			while (i.hasNext())
				((DataStructureVisualiser) i.next().getContent()).resize();
		});

		getContentPane().heightProperty().addListener((o, old, newValue) -> {
			height = newValue.doubleValue();
			Iterator<Tab> i = tabs.getTabs().iterator();
			while (i.hasNext())
				((DataStructureVisualiser) i.next().getContent()).resize();
		});
	}

	@Override
	public void ready() {
		// Listened for visualisation changes
		HLVisualManager hlvisual = getWindowManager().getHLVisualManager();
		hlvisual.addObserver(this);

		// Add already opened visualisations
		Iterator<DataStructureModel> models = hlvisual.getModels().iterator();
		while (models.hasNext()) {
			addNewVisualisation(models.next());
		}

		super.ready();
	}

	@Override
	public void close() {
		super.close();

		// Stop listening to visualisation changes
		getWindowManager().getHLVisualManager().deleteObserver(this);
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
			if (t.getContent() == vis)
				Platform.runLater(() -> tabs.getTabs().remove(t));
		}
	}

	public double getWindowWidth() {
		return width;
	}

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
		Pair<Action, DataStructureModel> change = (Pair<Action, DataStructureModel>) obj;
		if (change.getKey() == Action.CREATED) {
			addNewVisualisation(change.getValue());
		} else if (change.getKey() == Action.DELETED) {
			Iterator<Tab> iterator = tabs.getTabs().iterator();
			while (iterator.hasNext()) {
				Tab t = iterator.next();
				if (((DataStructureVisualiser) t.getContent()).getModel() == change.getValue())
					Platform.runLater(() -> tabs.getTabs().remove(t));
			}
		}
	}

	private void addNewVisualisation(DataStructureModel model) {
		switch (model.modelType()) {
			case FRAME:
				new FrameVisualiser((FrameModel) model, this);
				break;
			case HANOI:
				new TowerOfHanoiVisualiser((HanoiModel) model, this);
				break;
			case LIST:
				new ListVisualiser((ListModel) model, this);
				break;
		}
	}

}
