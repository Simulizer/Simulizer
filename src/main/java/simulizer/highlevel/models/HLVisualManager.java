package simulizer.highlevel.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.util.Pair;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;

public class HLVisualManager extends Observable implements Observer {

	private final Set<DataStructureModel> models = new HashSet<>();
	private boolean autoOpen;
	private Workspace workspace;

	public HLVisualManager(Workspace workspace, boolean autoOpen) {
		this.workspace = workspace;
		this.autoOpen = autoOpen;
	}

	public DataStructureModel create(String visualiser) {
		DataStructureModel model;
		switch (visualiser) {
			case "tower-of-hanoi":
				model = new HanoiModel();
				break;
			case "list":
				model = new ListModel();
				break;
			case "frame":
				model = new FrameModel();
				break;
			default:
				throw new IllegalArgumentException();
		}
		model.addObserver(this);
		models.add(model);
		setChanged();
		notifyObservers(new Pair<>(Action.CREATED, model));
		return model;
	}

	public void remove(DataStructureModel model) {
		model.deleteObserver(this);
		models.remove(model);
		setChanged();
		notifyObservers(new Pair<>(Action.DELETED, model));
	}

	public void removeAll() {
		Iterator<DataStructureModel> iterator = models.iterator();
		while (iterator.hasNext()) {
			DataStructureModel model = iterator.next();
			iterator.remove();
			setChanged();
			notifyObservers(new Pair<>(Action.DELETED, model));
		}
	}

	public Set<DataStructureModel> getModels() {
		return models;
	}

	@Override
	public void update(Observable observable, Object obj) {
		if (obj == null) {
			DataStructureModel model = (DataStructureModel) observable;
			if (autoOpen && model.isVisible())
				workspace.openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		}
	}
}
