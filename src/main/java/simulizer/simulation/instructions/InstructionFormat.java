package simulizer.simulation.instructions;

import simulizer.assembler.representation.Instruction;

/**generic class for different instruction types
 * 
 * @author Charlie Street
 *
 */
public class InstructionFormat {

	public AddressMode mode;
	private Instruction instruction;
	
	/**constructor will initialise the instruction
	 * 
	 * @param instruction the instruction
	 */
	public InstructionFormat(Instruction instruction)
	{
		this.instruction = instruction;
		this.mode =  null;//will be set by subclasses 
	}
	
	/**returns the instruction to be executed
	 * 
	 * @return the instruction encapsulated in this object
	 */
	public Instruction getInstruction()
	{
		return this.instruction;
	}
	
	/**carry out cast if we know its an r-type instruction
	 * 
	 * @return the instruction as an r type
	 */
	public RTypeInstruction asRType()
	{
		return (RTypeInstruction)this;
	}
	
	/**carry out cast if we know its an i-type instruction
	 * 
	 * @return the instruction as an i type
	 */
	public ITypeInstruction asIType()
	{
		return (ITypeInstruction)this;
	}
	
}
