package simulizer.simulation.cpu.components;

import simulizer.simulation.data.representation.Word;

/**this class represents the bus between the LSUnit, 
 * it is different in that it needs a second word for the address in memory
 * NOTE : use address word for any addresses and data for any data
 * @author Charlie Street
 *
 */
public class MemoryBus extends Bus 
{
	private Word addressWord;//address in memory 
	
	/**constructor call super class and initialising the new word
	 * 
	 */
	public MemoryBus()
	{
		super();
		this.addressWord = new Word();
	}
	
	/**method will return the address specified in the bus
	 * 
	 * @return the address word
	 */
	public Word getAddressWord()
	{
		return this.addressWord;
	}
	
	/**sets the new address to be stored and notifies observers
	 * 
	 * @param newAddress the new address to be stored
	 */
	public synchronized void setAddressWord(Word newAddress)
	{
		this.addressWord = newAddress;
		notifyObservers();
		setChanged();
	}
}
