package simulizer.highlevel.models;

import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import javafx.util.Pair;

public class HLVisualManager extends Observable {

	private final Set<DataStructureModel> models = new HashSet<DataStructureModel>();

	public DataStructureModel create(String visualiser, boolean showNow) {
		DataStructureModel model = null;
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
		getModels().add(model);
		if (showNow)
			model.show();
		else
			model.hide();
		setChanged();
		notifyObservers(new Pair<Action, DataStructureModel>(Action.CREATED, model));
		return model;
	}

	public void remove(DataStructureModel model) {
		getModels().remove(model);
		setChanged();
		notifyObservers(new Pair<Action, DataStructureModel>(Action.DELETED, model));
	}

	public Set<DataStructureModel> getModels() {
		return models;
	}
}
