package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Instruction;

/**exception to be thrown in the case of a bad instruction being detected
 * 
 * @author Charlie Street
 *
 */
public class InstructionException extends Exception
{
	private static final long serialVersionUID = 1L;
	private Instruction instruction;
	
	/**this method will call the exception super constructor and store the
	 * instruction object
	 * @param message the exception message
	 * @param instruction the instruction which caused the exception
	 */
	public InstructionException(String message, Instruction instruction)
	{
		super(message);
		this.instruction = instruction;
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
