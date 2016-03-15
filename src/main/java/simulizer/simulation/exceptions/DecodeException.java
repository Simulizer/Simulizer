package simulizer.simulation.exceptions;

import simulizer.assembler.representation.operand.Operand;

/**
 * exception for problems encountered during the decode stage
 * 
 * @author Charlie Street
 *
 */
public class DecodeException extends Exception {
	private static final long serialVersionUID = -8469924611641506049L;

	private Operand operand;
	
	/**
	 * @param message the exception message
	 * @param operand the operand which caused the error
	 */
	public DecodeException(String message, Operand operand)
	{
		super(message);
		this.operand = operand;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {operand = " + operand + "}";
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
