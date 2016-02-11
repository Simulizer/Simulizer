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
		switch(instruction) {//checking each possible instruction
			case abs:
				break;
			case and:
				break;
			case add:
				break;
			case addu:
				break;
			case addi:
				break;
			case addiu:
				break;
			case sub:
				break;
			case subu:
				break;
			case subi:
				break;
			case subiu:
				break;
			case mul:
				break;
			case mulo:
				break;
			case mulou:
				break;
			case div:
				break;
			case divu:
				break;
			case neg:
				break;
			case negu:
				break;
			case nor:
				break;
			case not:
				break;
			case or:
				break;
			case ori:
				break;
			case xor:
				break;
			case xori:
				break;
			case b:
				break;
			case beq:
				break;
			case bne:
				break;
			case bgez:
				break;
			case bgtz:
				break;
			case blez:
				break;
			case bltz:
				break;
			case beqz:
				break;
			default:
				//THROW ERROR INSTRUCTION NOT RECOGNISED/SUPPORTED
				break;
		}
		return new Word(new byte[]{0x00,0x00,0x00,0x00});
	}
	
}