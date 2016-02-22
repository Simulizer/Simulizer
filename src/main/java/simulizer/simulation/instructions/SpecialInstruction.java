package simulizer.simulation.instructions;

import simulizer.assembler.representation.Instruction;

/**this class is used for 'special' no argument instructions
 * such as syscall, nop and break
 * @author Charlie Street
 *
 */
public class SpecialInstruction extends InstructionFormat {

	/**constructor sets mode and calls super constructor
	 * 
	 * @param instruction the instruction being executed
	 */
	public SpecialInstruction(Instruction instruction)
	{
		super(instruction);
		this.mode = AddressMode.SPECIAL;
	}
	
}
