package simulizer.highlevel.models;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import javafx.util.Pair;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.ui.components.Workspace;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.interfaces.WindowEnum;

/**
 * Creates and deletes High Level Visualisation models
 * 
 * @author Michael
 *
 */
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

	/**
	 * Creates a new DataStructureModel
	 * 
	 * @param visualiser
	 *            the visualisation to create
	 * @return the new DataStructureModel
	 */
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

	/**
	 * Deletes a High Level Visualisations
	 * 
	 * @param model
	 *            the model to remove
	 */
	public void remove(DataStructureModel model) {
		model.deleteObserver(this);
		models.remove(model);
		setChanged();
		notifyObservers(new Pair<>(Action.DELETED, model));
	}

	/**
	 * Closes all models
	 */
	public void removeAll() {
		Iterator<DataStructureModel> iterator = models.iterator();
		while (iterator.hasNext()) {
			DataStructureModel model = iterator.next();
			iterator.remove();
			setChanged();
			notifyObservers(new Pair<>(Action.DELETED, model));
		}
	}

	/**
	 * @return a set of all loaded visualisation models
	 */
	public Set<DataStructureModel> getModels() {
		return models;
	}

	@Override
	public void update(Observable observable, Object obj) {
		if (obj == null && observable instanceof DataStructureModel) {
			DataStructureModel model = (DataStructureModel) observable;
			if (autoOpen && model.isVisible())
				workspace.openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		}
	}

	/**
	 * Determines whether the model has been created or deleted
	 * 
	 * @author Michael
	 *
	 */
	public enum Action {
		CREATED, DELETED;
	}
}
