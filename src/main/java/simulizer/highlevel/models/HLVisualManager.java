package simulizer.highlevel.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Set;

import javafx.util.Pair;

public class HLVisualManager extends Observable {

	private final Set<DataStructureModel> models = new HashSet<>();

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
		models.add(model);
		setChanged();
		notifyObservers(new Pair<>(Action.CREATED, model));
		return model;
	}

	public void remove(DataStructureModel model) {
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
}
