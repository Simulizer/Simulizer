package simulizer.simulation.exceptions;

import simulizer.assembler.representation.operand.Operand;

/**this class is an exception for problems occuring during decode
 * 
 * @author Charlie Street
 *
 */
public class DecodeException extends Exception {
	
	private Operand operand;
	
	/**constructor calls super constructor of exception
	 * and sets the operand
	 * @param message the exception message
	 * @param operand the operand which caused the error
	 */
	public DecodeException(String message, Operand operand)
	{
		super(message);
		this.operand = operand;
	}
	
	/**returns the operand which threw the exception
	 * 
	 * @return the operand which caused the exception
	 */
	public Operand getOperand()
	{
		return this.operand;
	}
}
