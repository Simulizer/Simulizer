package simulizer.simulation.instructions;

import java.util.Optional;

import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.Word;



public class RTypeInstruction extends InstructionFormat {

	private Optional<Word> dest;//data to store
	private Register destReg;
	private Optional<Word> src1;
	private Optional<Word> src2;
	
	/**constructor will set up all the data for execution
	 * 
	 * @param instruction the istruction to carry out
	 * @param dest the destination word
	 * @param destReg the destination register
	 * @param src1 the first src register contents
	 * @param src2 the second src register contents
	 */
	public RTypeInstruction(Instruction instruction, Optional<Word> dest, Register destReg, Optional<Word> src1, Optional<Word> src2)
	{
		super(instruction);//calling super constructor
		this.mode = AddressMode.RTYPE;
		this.dest = dest;
		this.destReg = destReg;
		this.src1 = src1;
		this.src2 = src2;
	}
	
	/**method returns the destination register word
	 * 
	 * @return the destination word
	 */
	public Optional<Word> getDest()
	{
		return this.dest;
		
	}
	
	/**returns the destination register name
	 * 
	 * @return name of destination register
	 */
	public Register getDestReg()
	{
		return this.destReg;
	}
	
	/**returns the first src register contents
	 * 
	 * @return contents of first register
	 */
	public Optional<Word> getSrc1()
	{
		return this.src1;
	}
	
	/**returns the second src register contents
	 * 
	 * @return contents of the second src register
	 */
	public Optional<Word> getSrc2()
	{
		return this.src2;
	}
}
