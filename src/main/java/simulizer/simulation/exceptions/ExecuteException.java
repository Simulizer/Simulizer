package simulizer.simulation.exceptions;

import simulizer.simulation.instructions.InstructionFormat;

/**
 * exception for problems encountered during the execution stage
 * 
 * @author Charlie Street
 *
 */
public class ExecuteException extends Exception {
	private static final long serialVersionUID = -6358895526749092340L;

	private InstructionFormat instruction;
	
	/**
	 * @param message the exception message
	 * @param instruction the instruction that couldn't execute
	 */
	public ExecuteException(String message, InstructionFormat instruction)
	{
		super(message);
		this.instruction = instruction;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {instruction = " + instruction + "}";
	}

	/**returns the instruction which caused the exception
	 * 
	 * @return the problematic instruction
	 */
	public InstructionFormat getInstruction()
	{
		return this.instruction;
	}
}
