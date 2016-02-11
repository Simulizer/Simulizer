package simulizer.simulation.cpu.components;

import simulizer.assembler.representation.Instruction;
import simulizer.simulation.data.representation.Word;



/**this class represents the ALU in the CPU
 * all it is capable of doing is carrying out various operations
 * and then returning the result
 * @author Charlie
 *
 */
public class ALU {
	
	/**empty constructor
	 * 
	 */
	public ALU() {
		
	}
	
	/**this method uses a switch statement to execute some operation on two words
	 * 
	 * @param instruction the precise instruction to execute
	 * @param firstWord the first word to work on
	 * @param secondWord the second word to work on
	 * @return the result of the operation on the two words
	 */
	public Word execute(Instruction instruction, Word firstWord, Word secondWord)
	{
		//insert massive switch statement here
		return new Word(new byte[]{0x00,0x00,0x00,0x00});
	}
	
}