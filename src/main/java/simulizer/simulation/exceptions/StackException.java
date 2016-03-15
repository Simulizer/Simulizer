package simulizer.simulation.exceptions;

/**
 * exception for problems related to the stack
 * 
 * @author Charlie Street
 *
 */
public class StackException extends Exception {
	private static final long serialVersionUID = 3575131873818521606L;

	/**
	 * address relative to the base of the stack (index 0)
	 */
	private int stackAddress;
	
	/**constructor calls super constructor and initialises field
	 * 
	 * @param message exception message
	 * @param stackAddress internal address of error in stack
	 */
	public StackException(String message, int stackAddress)
	{
		super(message);
		this.stackAddress = stackAddress;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {stackAddress = " + stackAddress + "}";
	}

	/**method will return the internal address where there was an error with the stack
	 * 
	 * @return internal stack address of error
	 */
	public int getStackAddress()
	{
		return this.stackAddress;
	}
}
