package simulizer.simulation.exceptions;

import simulizer.assembler.representation.Program;

/**
 * exception for problems relating to the program object
 *
 * @author Charlie Street
 *
 */
public class ProgramException extends Exception {
	private static final long serialVersionUID = 8126064369945353195L;

	private Program programData;
	
	/**
	 * @param message the exception message
	 * @param programData the program which caused the exception
	 */
	public ProgramException(String message, Program programData)
	{
		super(message);
		this.programData = programData;
	}

	@Override
	public String toString() {
		return getMessage(); // can't display whole program object
	}

	/**this method returns the erroneous program
	 * 
	 */
	public Program getProgram()
	{
		return this.programData;
	}
}
