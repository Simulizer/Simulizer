package simulizer.simulation.cpu.components;

import simulizer.assembler.representation.Instruction;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.InstructionException;



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
	 * @throws InstructionException if unsupported instruction attempted
	 */
	public Word execute(Instruction instruction, Word firstWord, Word secondWord) throws InstructionException
	{
		switch(instruction) {//checking each possible instruction
			case abs:
				break;
			case and:
				break;
			case add:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) + loadAsSigned(secondWord.getWord())));
			case addu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstWord.getWord()) + loadAsUnsigned(secondWord.getWord())));
			case addi:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) + loadAsSigned(secondWord.getWord())));
			case addiu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstWord.getWord()) + loadAsUnsigned(secondWord.getWord())));
			case sub:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) - loadAsSigned(secondWord.getWord())));
			case subu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstWord.getWord()) - loadAsUnsigned(secondWord.getWord())));
			case subi:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) - loadAsSigned(secondWord.getWord())));
			case subiu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstWord.getWord()) - loadAsUnsigned(secondWord.getWord())));
			case mul:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) * loadAsSigned(secondWord.getWord())));
			case mulo:
				break;
			case mulou:
				break;
			case div:
				return new Word(serialiseSigned(loadAsSigned(firstWord.getWord()) / loadAsSigned(secondWord.getWord())));
			case divu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstWord.getWord()) / loadAsUnsigned(secondWord.getWord())));
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
				throw new InstructionException("Invalid/Unsupoorted Instruction.",instruction);
		}
		return new Word(new byte[]{0x00,0x00,0x00,0x00});
	}

	/**method takes a byte array and returns it's signed value as a long
	 * 
	 * @param word the word to convert
	 * @return the byte[] converted to a signed long
	 */
	private long loadAsSigned(byte[] word) {
		return 1L;
	}
	
	/**method takes a byte array and returns it's unsigned value as a long
	 * 
	 * @param word the word to convert
	 * @return the byte[] converted to an unsigned long
	 */
	private long loadAsUnsigned(byte[] word) {
		return 1L;
	}
	
	
	/**this method takes a signed long and converts it to a byte array
	 * 
	 * @param value the long to convert
	 * @return the value as a byte array
	 */
	private byte[] serialiseSigned(long value) {
		return new byte[4];
	}
	
	/**this method takes an unsigned long and converts it to a byte array
	 * 
	 * @param value the long to convert
	 * @return the value as a byte array
	 */
	private byte[] serialiseUnsigned(long value) {
		return new byte[4];
	}
	
}