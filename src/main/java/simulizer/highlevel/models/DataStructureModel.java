package simulizer.highlevel.models;

import java.util.Observable;

import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;

public abstract class DataStructureModel extends Observable {
	private IO io;
	private boolean visible = false;

	public DataStructureModel(IO io) {
		this.io = io;
	}

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

	protected void printError(String error) {
		// TODO: Throw an actual exception so the annotation number is printed.
		io.printString(IOStream.DEBUG, "From " + modelType() + ": " + error);
	}

}
