package main.java.simulizer.simulation.CPU_Components;

import java.util.Observable;

import main.java.simulizer.simulation.Data_Representation.Word;

/**this class represents the Instruction Register in our simulation
 * 
 * @author Charlie Street
 *
 */
public class InstructionRegister extends Observable
{
	private Word currentInstruction;
	private Bus controlBus;
	private Bus PCBus;
	private Bus LSBus;
	
	/**this constructor will initialise all of the Buses and the stored data
	 * 
	 * @param controlBus the bus connecting the IR to the Control Unit
	 * @param PCBus the bus connecting the IR to the Program Counter
	 * @param LSBus the bus connecting the IR to the Load/Store Unit
	 */
	public InstructionRegister(Bus controlBus, Bus PCBus, Bus LSBus)
	{
		super();
		this.currentInstruction = new Word();//initialising to all zeros (shouldn't really be used before something is placed into it)
		this.controlBus = controlBus;
		this.PCBus = PCBus;
		this.LSBus = LSBus;
	}
	
	/**this method will retrieve the data in the IR
	 * 
	 * @return the stored instruction
	 */
	public Word getData()
	{
		return this.currentInstruction;
	}
	
	/**sets the instruction stored in the IR
	 * 
	 * @param word the new data for the IR
	 */
	private synchronized void setData(Word word)
	{
		this.currentInstruction = word;
	}
	
	/**retrieves whatever was sent on the control bus
	 * 
	 */
	public void retrieveControlBus()
	{
		this.setData(this.controlBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**this method sends something onto the control bus
	 * 
	 */
	public void sendControlBus()
	{
		this.controlBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
	
	/**retrieves whatever was stored on the PCBus
	 * 
	 */
	public void retrievePCBus()
	{
		this.setData(this.PCBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**sends a word onto the PC Bus
	 * 
	 */
	public void sendPCBus()
	{
		this.PCBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
	
	/**retrieves whatever was stored on the LS bus
	 * 
	 */
	public void retrieveLSBus()
	{
		this.setData(this.LSBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**sends a word onto the LSBus
	 * 
	 */
	public void sendLSBus()
	{
		this.LSBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
}
