package simulizer.simulation.instructions;

import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.simulation.data.representation.Word;

/**class for IType Instructions, instructions with an offset
 * and two registers
 * @author Charlie Street
 *
 */
public class ITypeInstruction extends InstructionFormat {

	private Optional<Word> cmp1;
	private Optional<Word> cmp2;
	private Optional<Address> branchAddr;

	/**constructor calls super constructor and initialises fields
	 * 
	 * @param instruction the instruction to carry out
	 * @param cmp1 the first comparison register
	 * @param cmp2 the second comparison register (may be empty)
	 * @param branchAddr the address to branch to
	 */
	public ITypeInstruction(Instruction instruction, Optional<Word> cmp1, Optional<Word> cmp2, Optional<Address> branchAddr)
	{
		super(instruction);
		this.cmp1 = cmp1;
		this.cmp2 = cmp2;
		this.branchAddr = branchAddr;
	}
	
	/**this method returns the first comparison register contents
	 * 
	 * @return contents of cmp1
	 */
	public Optional<Word> getCmp1()
	{
		return this.cmp1;
	}
	
	/**this method returns the second comparison register contents
	 * 
	 * @return the contents of cmp2
	 */
	public Optional<Word> getCmp2()
	{
		return this.cmp2;
	}
	
	/**method gets the branch address
	 * 
	 * @return the branch address if comparison true
	 */
	public Optional<Address> getBranchAddress()
	{
		return this.branchAddr;
	}
}
