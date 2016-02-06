package simulizer.simulation.cpu.components;

import java.util.Observable;

import simulizer.simulation.data.representation.Word;

/** this class represents the Instruction Register in our simulation
 * @author Charlie Street */
public class InstructionRegister extends Observable {
	private Word currentInstruction;
	private ControlUnit controlUnit;
	private ProgramCounter programCounter;
	private LSUnit lsUnit;

	/** this constructor will initialise all of the Buses and the stored data
	 * @param controlUnit the bus connecting the IR to the Control Unit
	 * @param programCounter the bus connecting the IR to the Program Counter
	 * @param lsUnit the bus connecting the IR to the Load/Store Unit */
	public InstructionRegister(ControlUnit controlUnit, ProgramCounter programCounter, LSUnit lsUnit) {
		super();
		this.currentInstruction = new Word();// initialising to all zeros (shouldn't really be used before something is placed
												// into it)
		this.controlUnit = controlUnit;
		this.programCounter = programCounter;
		this.lsUnit = lsUnit;
	}

	/** this method will retrieve the data in the IR
	 * @return the stored instruction */
	public Word getData() {
		return this.currentInstruction;
	}

	/** sets the instruction stored in the IR
	 * @param word the new data for the IR */
	public synchronized void setData(Word word) {
		this.currentInstruction = word;
	}

	/** retrieves whatever was sent on the control bus */
	public void retrievecontrolUnit() {
		this.setData(this.controlUnit.getData());
		notifyObservers();
		setChanged();
	}

	/** this method sends something onto the control bus */
	public void sendcontrolUnit() {
		this.controlUnit.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** retrieves whatever was stored on the programCounter */
	public void retrieveprogramCounter() {
		this.setData(this.programCounter.getData());
		notifyObservers();
		setChanged();
	}

	/** sends a word onto the PC Bus */
	public void sendprogramCounter() {
		this.programCounter.setData(this.getData());
		notifyObservers();
		setChanged();
	}

	/** retrieves whatever was stored on the LS bus */
	public void retrievelsUnit() {
		this.setData(this.lsUnit.getData());
		notifyObservers();
		setChanged();
	}

	/** sends a word onto the lsUnit */
	public void sendlsUnit() {
		this.lsUnit.setData(this.getData());
		notifyObservers();
		setChanged();
	}
}
