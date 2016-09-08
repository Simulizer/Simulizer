package simulizer.simulation.exceptions;

/**
 * exception for problems related to the stack
 * 
 * @author Charlie Street
 *
 */
public class StackException extends Exception {
	private static final long serialVersionUID = 3575131873818521606L;

	/** start of the problematic range (inclusive)
	 * address relative to the base of the stack (highest address) (index 0)
	 */
	private int startAddress;

	/** end of the problematic range (inclusive)
	 * address relative to the base of the stack (highest address) (index 0)
	 */
	private int endAddress;

	
	/**constructor calls super constructor and initialises field
	 * 
	 * @param message exception message
     * @param startAddress the start of the range where the error occurred
	 * @param endAddress the end of the range where the error occurred
	 */
	public StackException(String message, int startAddress, int endAddress)
	{
		super(message);
        this.startAddress = startAddress;
		this.endAddress = endAddress;
	}

	@Override
	public String toString() {
	    if(startAddress == endAddress) // single item, not a range
            return getMessage() + ". Details: {stackAddress = " + startAddress + "}";
        else
			return getMessage() + ". Details: {startAddress = " + startAddress + ", endAddress=" + endAddress + "}";
	}

}
