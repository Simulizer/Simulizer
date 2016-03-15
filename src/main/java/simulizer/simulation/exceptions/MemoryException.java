package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Address;

/**
 * exception for problems occurring due to access of main memory
 * 
 * @author Charlie Street
 *
 */
public class MemoryException extends Exception {
	private static final long serialVersionUID = -1504303791781141514L;

	private Address addressOfError;
	
	/**
	 * @param message exception message
	 * @param addressOfError address of access where the error was thrown
	 */
	public MemoryException(String message, Address addressOfError)
	{
		super(message);
		this.addressOfError = addressOfError;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {addressOfError = " + addressOfError + "}";
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
