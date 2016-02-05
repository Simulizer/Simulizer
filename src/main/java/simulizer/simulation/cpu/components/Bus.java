package simulizer.simulation.CPU_Components;

import java.util.Observable;
import simulizer.simulation.Data_Representation.Word;

/** this class represents a generic simplified bus in the cpu
 * @author Charlie Street */
public class Bus extends Observable {
	private Word storedData;

	/** this constructor will initialise the stored word, nothing else */
	public Bus() {
		super();
		this.storedData = new Word();// initialising to a zeroed word essentially
	}

	/** returning the data stored in the bus
	 * @return the data stored in the bus */
	public synchronized Word getData() {
		return this.storedData;
	}

	/** this method sets the data to be stored in the bus
	 * @param word the word to be set in the bus */
	public synchronized void setData(Word word) {
		this.storedData = word;
		notifyObservers();// alert interface something has changed
		setChanged();
	}
}
