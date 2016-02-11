package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Address;

/**this class used for reporting errors related with the heap
 * 
 * @author Charlie Street
 *
 */
public class HeapException extends Exception
{
	private static final long serialVersionUID = 1L;
	private Address breakOfHeap;
	private int heapSize;
	
	/**constructor sets up the exception and sets up the other data associated with the class
	 * 
	 * @param message the error message of the exception
	 * @param breakOfHeap the current location of the break
	 * @param heapSize the current heap size
	 */
	public HeapException(String message, Address breakOfHeap, int heapSize)
	{
		super(message);//calling super constructor
		this.breakOfHeap = breakOfHeap;
		this.heapSize = heapSize;
	}
	
	/**return the current heap break point
	 * 
	 * @return the current break point
	 */
	public Address getBreak()
	{
		return this.breakOfHeap;
	}
	
	/**returns the heap size at time of exception
	 * 
	 * @return the heap size at time of exception
	 */
	public int getHeapSize()
	{
		return this.heapSize;
	}
}
