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
	
	/**carry out cast if we know it's a special instruction
	 * 
	 * @return the instruction as a special instruction
	 */
	public SpecialInstruction asSpecial()
	{
		return (SpecialInstruction)this;
	}
	
	/**carrys out cast if we know it's a j-type instruction
	 * 
	 * @return the instruction as a j-type
	 */
	public JTypeInstruction asJType()
	{
		return (JTypeInstruction)this;
	}
	
	/**carrys out cast if we know it's an ls instruction
	 * 
	 * @return the instruction as ls type
	 */
	public LSInstruction asLSType()
	{
		return (LSInstruction)this;
	}
}
