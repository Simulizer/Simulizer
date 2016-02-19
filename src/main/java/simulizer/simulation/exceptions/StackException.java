package simulizer.simulation.exceptions;

/**class for exceptions raised by the stack
 * 
 * @author Charlie Street
 *
 */
public class StackException extends Exception {

	private int stackAddress;//address relative 0 point of stack
	
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
	
	/**method will return the internal address where there was an error with the stack
	 * 
	 * @return internal stack address of error
	 */
	public int getStackAddress()
	{
		return this.stackAddress;
	}
}
