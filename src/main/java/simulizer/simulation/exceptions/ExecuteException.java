package simulizer.simulation.exceptions;

import simulizer.simulation.instructions.InstructionFormat;

/**for exceptions thrown during execution
 * 
 * @author Charlie Street
 *
 */
public class ExecuteException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private InstructionFormat instruction;
	
	/**constructor calls super constructor and intialises field
	 * 
	 * @param message the exception message
	 * @param instruction the instruction that couldn't execute
	 */
	public ExecuteException(String message, InstructionFormat instruction)
	{
		super(message);
		this.instruction = instruction;
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
