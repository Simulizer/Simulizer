package simulizer.highlevel.models;

import java.util.Observable;

public abstract class DataStructureModel extends Observable {
	private boolean visible = false;

	public void show() {
		visible = true;
		setChanged();
		notifyObservers();
	}

	public void hide() {
		visible = false;
		setChanged();
		notifyObservers();
	}

	public boolean isVisible() {
		return visible;
	}

	public abstract ModelType modelType();

	

}
