package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Address;

/**class used to represent erros occuring due to access of main memory
 * 
 * @author Charlie Street
 *
 */
public class MemoryException extends Exception {

	private Address addressOfError;
	
	/**constructor calls super constructor and initialises error address field
	 * 
	 * @param message exception message
	 * @param addressOfError address of access where the error was thrown
	 */
	public MemoryException(String message, Address addressOfError)
	{
		super(message);
		this.addressOfError = addressOfError;
	}
	
	/**returns the address where the error occured
	 * 
	 * @return the address of access which caused the exception
	 */
	public Address getErrorAddress()
	{
		return this.addressOfError;
	}
}
