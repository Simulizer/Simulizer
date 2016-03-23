package simulizer.simulation.instructions;

import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.simulation.data.representation.Word;

/**this class is used to encapsulate the information required for a jtype instruction
 * i.e this is for jump instructions mainly
 * @author charlie street
 *
 */
public class JTypeInstruction extends InstructionFormat {

	private Optional<Address> jumpAddress;
	private Optional<Word> currentAddress;//only needed for jal
	
	/**this constructor calls the super constructor and initialises all fields
	 * 
	 * @param instruction the instruction being executed
	 * @param jumpAddress the address to jump to
	 * @param currentAddress the current address (only needed for jal)
	 */
	public JTypeInstruction(Instruction instruction, Optional<Address> jumpAddress, Optional<Word> currentAddress)
	{
		super(instruction);
		this.mode = AddressMode.JTYPE;
		this.jumpAddress = jumpAddress;
		this.currentAddress = currentAddress;
	}
	
	/**returns the address to jump to
	 * 
	 * @return the address to jump to
	 */
	public Optional<Address> getJumpAddress()
	{
		return this.jumpAddress;
	}
	
	/**method returns the current address useful for jal
	 * 
	 * @return the current address
	 */
	public Optional<Word> getCurrentAddress()
	{
		return this.currentAddress;
	}
}
