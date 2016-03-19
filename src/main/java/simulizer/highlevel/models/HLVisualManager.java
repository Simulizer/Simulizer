package simulizer.highlevel.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.util.Pair;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;

public class HLVisualManager extends Observable implements Observer {

	private final Set<DataStructureModel> models = new HashSet<>();
	private boolean autoOpen;
	private Workspace workspace;
	private IO io;

	public HLVisualManager(Workspace workspace, IO io, boolean autoOpen) {
		this.workspace = workspace;
		this.io = io;
		this.autoOpen = autoOpen;
	}

	public DataStructureModel create(String visualiser) {
		DataStructureModel model;
		switch (visualiser) {
			case "tower-of-hanoi":
				model = new HanoiModel(io);
				break;
			case "list":
				model = new ListModel(io);
				break;
			case "frame":
				model = new FrameModel(io);
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

	public enum Action {
		CREATED, DELETED;
	}
}
