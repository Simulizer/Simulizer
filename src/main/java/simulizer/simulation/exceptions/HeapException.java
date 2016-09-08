package simulizer.simulation.exceptions;

/**
 * exception for problems related to the heap
 * 
 * @author Charlie Street
 *
 */
public class HeapException extends Exception {
	private static final long serialVersionUID = 6007698896140851786L;

	private int heapBreak;
	private int heapSize;
	
	/**
	 * @param message the error message of the exception
	 * @param heapBreak the current location of the break
	 * @param heapSize the current heap size
	 */
	public HeapException(String message, int heapBreak, int heapSize)
	{
		super(message);//calling super constructor
		this.heapBreak = heapBreak;
		this.heapSize = heapSize;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {breakOfHeap = " + heapBreak + ", heapSize=" + heapSize + "}";
	}

	/**return the current heap break point
	 * 
	 * @return the current break point
	 */
	public int getBreak()
	{
		return heapBreak;
	}
	
	/**returns the heap size at time of exception
	 * 
	 * @return the heap size at time of exception
	 */
	public int getHeapSize()
	{
		return heapSize;
	}
}
