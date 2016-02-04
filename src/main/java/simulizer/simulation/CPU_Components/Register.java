package simulizer.simulation.CPU_Components;

import java.util.Observable;

import simulizer.simulation.Data_Representation.Word;

/**this class represents a single general purpose register in the CPU
 * 
 * @author Charlie Street
 *
 */
public class Register extends Observable
{
	private Word word;
	private Bus LSBus;
	private Bus ALUBus;
	private Bus controlBus;
	private String pseudonym;//register name like $sp, $t0 etc
	
	/**initialising all connections/data associated with the register
	 * 
	 * @param LSBus the bus shared between the load store unit and this register
	 * @param ALUBus the bus shared between the ALU and this register
	 * @param ControlBus the bus shared between the control unit and this register
	 * @param pseudonym the pseudonym given to this register
	 */
	public Register(Bus LSBus, Bus ALUBus, Bus controlBus, String pseudonym)
	{
		super();
		this.word = new Word();//initialising register contents
		this.LSBus = LSBus;
		this.ALUBus = ALUBus;
		this.controlBus = controlBus;
		this.pseudonym = pseudonym;
	}
	
	/**method returns the data currently stored within this register
	 * 
	 * @return the word of data in the register
	 */
	public Word getData()
	{
		return this.word;
	}
	
	/**this method sets new data into the register, to be careful
	 * it is synchronised for exclusive access to prevent any possible errors
	 * that may occur due to multi threading and the pipelining
	 * @param word the word to set in the register
	 */
	private synchronized void setData(Word word)
	{
		this.word = word;
	}
	
	/**this method retrieves the contents put in it by the LSBus
	 * 
	 */
	public void retrieveLSBus()
	{
		this.setData(this.LSBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**doesn't really 'send' anything 
	 * but puts something in the LSBus to be retrieved by the LSUnit
	 */
	public void sendLSBus()
	{
		this.LSBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
	
	/**this method retrieves the contents sent to it from the ALU
	 * in the bus connecting the ALU and register
	 */
	public void retrieveALUBus()
	{
		this.setData(this.ALUBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**sets data to be 'sent' in the ALU bus
	 * 
	 */
	public void sendALUBus()
	{
		this.ALUBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
	
	/**method retrieves the contents sent to it from the control unit
	 * using the bus connecting the two
	 */
	public void retrieveControlBus()
	{
		this.setData(this.controlBus.getData());
		notifyObservers();
		setChanged();
	}
	
	/**this method 'sends' something on the control bus 
	 * so that it can be retrieved by the control unit
	 */
	public void sendControlBus()
	{
		this.controlBus.setData(this.getData());
		notifyObservers();
		setChanged();
	}
	
	/**returns the registers pseudonym (might not be useful in the slightest)
	 * 
	 * @return the registers pseudonym
	 */
	public String getPseudonym()
	{
		return this.pseudonym;
	}
}
