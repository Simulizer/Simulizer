package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Instruction;

/**
 * exception for problems involving bad instruction decodes
 * 
 * @author Charlie Street
 *
 */
public class InstructionException extends Exception {
	private static final long serialVersionUID = -5766056399227203336L;

	private Instruction instruction;
	
	/**
	 * @param message the exception message
	 * @param instruction the instruction which caused the exception
	 */
	public InstructionException(String message, Instruction instruction)
	{
		super(message);
		this.instruction = instruction;
	}

	@Override
	public String toString() {
		return getMessage() + ". Details: {instruction = " + instruction + "}";
	}

	/**method returns the instruction which caused the exception
	 * 
	 * @return the instruction that caused the exception
	 */
	public Instruction getInstruction()
	{
		return this.instruction;
	}
}
