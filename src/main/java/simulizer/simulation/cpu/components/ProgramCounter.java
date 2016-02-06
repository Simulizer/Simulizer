package simulizer.simulation.cpu.components;

import java.math.BigInteger;
import java.util.Observable;

import simulizer.simulation.data.representation.Word;

/** this class represents the Program Counter, a special register in memory it does almost the same as a normal register, but has
 * different 'bus' connections it also has the ability to increment itself for the next address it should store
 * @author Charlie Street */
public class ProgramCounter extends Observable {
	private final Word INCREMENT;// increment amount for the program counter
	private Word nextAddress;
	private ControlUnit controlUnit;
	private InstructionRegister IRUnit;
	private LSUnit LSUnit;

	/** initialising all fields of this class
	 * @param nextAddress the first address to be put into the program counter, this should be the entry point to start
	 * @param controlUnit the 'bus' linking the control unit and the program counter
	 * @param IRUnit the 'bus' linking the Instruction Register and the Program Counter
	 * @param LSUnit the 'bus' linking the LS Unit and the program counter */
	public ProgramCounter(Word nextAddress, ControlUnit controlUnit, InstructionRegister IRUnit, LSUnit LSUnit) {
		super();
		this.INCREMENT = new Word(new BigInteger("4"));// 4 in 32 bit binary representation
		this.nextAddress = nextAddress;
		this.controlUnit = controlUnit;
		this.IRUnit = IRUnit;
		this.LSUnit = LSUnit;
	}

	/** this method will increment the value of the program counter by the generic increment */
	public void increment() {
		this.setData(this.getData().add(this.INCREMENT));//adds the increment to the program counter
		notifyObservers();
		setChanged();
	}

	/** this method will add an offset to the program counter
	 * @param offset the offset given to the PC */
	public void addOffset(Word offset) {
		this.setData(this.getData().add(offset));//adding the offset
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
	public synchronized void setData(Word word) {
		this.nextAddress = word;
	}

	/** this method will retrieve whatever data was sent on the control bus */
	public void retrievecontrolUnit() {
		this.setData(this.controlUnit.getData());
		notifyObservers();
		setChanged();
	}

	/** this method 'sends' something onto the control bus */
	public void sendcontrolUnit() {
		this.controlUnit.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** this method will retrieve whatever data was sent on the IR Bus this bus connection might actually be redundant? */
	public void retrieveIRUnit() {
		this.setData(this.IRUnit.getData());
		notifyObservers();
		setChanged();
	}

	/** method 'sends' something along the IR Bus */
	public void sendIRUnit() {
		this.IRUnit.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** method retrieves data from the LS Bus */
	public void retrieveLSUnit() {
		this.setData(this.LSUnit.getData());
		notifyObservers();
		setChanged();
	}

	/** method 'sends' something on to the LSUnit */
	public void sendLSUnit() {
		this.LSUnit.setData(this.getData());
		notifyObservers();
		setChanged();
	}
}