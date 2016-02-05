package simulizer.simulation.cpu.components;

import java.util.Observable;

import simulizer.simulation.data.representation.BinaryConversions;
import simulizer.simulation.data.representation.Word;

/** this class represents the Program Counter, a special register in memory it does almost the same as a normal register, but has
 * different bus connections it also has the ability to increment itself for the next address it should store
 * @author Charlie Street */
public class ProgramCounter extends Observable {
	private final Word INCREMENT;// increment amount for the program counter
	private Word nextAddress;
	private Bus controlBus;
	private Bus IRBus;
	private Bus LSBus;

	/** initialising all fields of this class
	 * @param nextAddress the first address to be put into the program counter, this should be the entry point to start
	 * @param controlBus the bus linking the control unit and the program counter
	 * @param IRBus the bus linking the Instruction Register and the Program Counter
	 * @param LSBus the bus linking the LS Unit and the program counter */
	public ProgramCounter(Word nextAddress, Bus controlBus, Bus IRBus, Bus LSBus) {
		super();
		this.INCREMENT = new Word(BinaryConversions.getBinaryString(4));// 4 in 32 bit binary representation
		this.nextAddress = nextAddress;
		this.controlBus = controlBus;
		this.IRBus = IRBus;
		this.LSBus = LSBus;
	}

	/** this method will increment the value of the program counter by the generic increment */
	public void increment() {
		this.setData(this.getData().add(this.INCREMENT));//adds the increment to the program counter
		notifyObservers();
		setChanged();
	}

	/** this method will add an offset to the program counter
	 * @param offset the offset given to the PC */
	public void addOffset(String offset) {

		String temp = offset;
		while(temp.length() != 32)//forcing length to 32 bits by padding with zeroes to make life easier
		{
			temp = '0' + temp;
		}
		
		Word offsetWord = new Word(temp);
		this.setData(this.getData().add(offsetWord));//adding the offset
		
		this.increment();// even with offset we still have to increment (I think)
	}

	/** this method returns the next address stored in the program counter
	 * @return the nextAddress stored in the program counter */
	public Word getData() {
		return this.nextAddress;
	}

	/** this method sets the value in the PC this method should be used cautiously, i.e only used on the use of jump instructions,
	 * new programs etc.
	 * @param word the new contents of the program counter */
	private synchronized void setData(Word word) {
		this.nextAddress = word;
	}

	/** this method will retrieve whatever data was sent on the control bus */
	public void retrieveControlBus() {
		this.setData(this.controlBus.getData());
		notifyObservers();
		setChanged();
	}

	/** this method 'sends' something onto the control bus */
	public void sendControlBus() {
		this.controlBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** this method will retrieve whatever data was sent on the IR Bus this bus connection might actually be redundant? */
	public void retrieveIRBus() {
		this.setData(this.IRBus.getData());
		notifyObservers();
		setChanged();
	}

	/** method 'sends' something along the IR Bus */
	public void sendIRBus() {
		this.IRBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** method retrieves data from the LS Bus */
	public void retrieveLSBus() {
		this.setData(this.LSBus.getData());
		notifyObservers();
		setChanged();
	}

	/** method 'sends' something on to the LSBus */
	public void sendLSBus() {
		this.LSBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
}