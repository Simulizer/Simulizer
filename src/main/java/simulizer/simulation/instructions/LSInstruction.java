package simulizer.simulation.instructions;

import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.Word;

/**this class is for instructions involving load and stores
 * this isn't an addressing mode in mips, however
 * we don't have an LSUnit so this seems most appropriate
 * @author Charlie Street
 *
 */
public class LSInstruction extends InstructionFormat {

	private Optional<Word> register;
	private Optional<Register> registerName;//used for load
	private Optional<Address> memAddress;
	private Optional<Word> immediate;//only for li but useful
	
	public LSInstruction(Instruction instruction, Optional<Word> register, Optional<Register> registerName, Optional<Address> memAddress, Optional<Word> immediate)
	{
		super(instruction);
		this.mode = AddressMode.LSTYPE;
		this.register = register;
		this.registerName = registerName;
		this.memAddress = memAddress;
		this.immediate = immediate;
	}
	
	/**this method will return the register used for the instruction
	 * 
	 * @return the register contents used
	 */
	public Optional<Word> getRegister()
	{
		return this.register;
	}
	
	/**this method will return the name of the register, if being used as a destination
	 * 
	 * @return the register name of destination
	 */
	public Optional<Register> getRegisterName()
	{
		return this.registerName;
	}
	/**this method will return the memory address used for the instruction
	 * 
	 * @return the address to retrieve/store at
	 */
	public Optional<Address> getMemAddress()
	{
		return this.memAddress;
	}
	
	/**returns the immediate value (if present)
	 * 
	 * @return the immediate value to load
	 */
	public Optional<Word> getImmediate()
	{
		return this.immediate;
	}
}
