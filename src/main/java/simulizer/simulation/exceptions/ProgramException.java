package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Program;

/**this class is in use for reporting errors related to
 * the program objects being used
 * @author Charlie Street
 *
 */
public class ProgramException extends Exception {

	private static final long serialVersionUID = 1L;
	private Program programData;
	
	/**constructor calls super constructor and initialises the program object
	 * 
	 * @param message the exception message
	 * @param programData the program which caused the exception
	 */
	public ProgramException(String message, Program programData)
	{
		super(message);
		this.programData = programData;
	}
	
	/**this method returns the errornous program
	 * 
	 */
	public Program getProgram()
	{
		return this.programData;
	}
}
