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
		byte[] firstValue = firstWord.getWord();
		byte[] secondValue = secondWord.getWord();
		
		switch(instruction) {//checking each possible instruction
			case abs:
				break;
			case and:
				byte[] resultAnd = new byte[4];
				for(int i = 0; i < resultAnd.length; i++) {
					resultAnd[i] = (byte) (firstValue[i] & secondValue[i]);
				}
				return new Word(resultAnd);
			case add:
				return new Word(serialiseSigned(loadAsSigned(firstValue) + loadAsSigned(secondValue)));
			case addu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) + loadAsUnsigned(secondValue)));
			case addi:
				return new Word(serialiseSigned(loadAsSigned(firstValue) + loadAsSigned(secondValue)));
			case addiu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) + loadAsUnsigned(secondValue)));
			case sub:
				return new Word(serialiseSigned(loadAsSigned(firstValue) - loadAsSigned(secondValue)));
			case subu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) - loadAsUnsigned(secondValue)));
			case subi:
				return new Word(serialiseSigned(loadAsSigned(firstValue) - loadAsSigned(secondValue)));
			case subiu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) - loadAsUnsigned(secondValue)));
			case mul:
				return new Word(serialiseSigned(loadAsSigned(firstValue) * loadAsSigned(secondValue)));
			case mulo:
				break;
			case mulou:
				break;
			case div:
				return new Word(serialiseSigned(loadAsSigned(firstValue) / loadAsSigned(secondValue)));
			case divu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) / loadAsUnsigned(secondValue)));
			case neg:
				break;
			case negu:
				break;
			case nor:
				byte[] resultNor = new byte[4];
				for(int i = 0; i < resultNor.length; i++) {
					resultNor[i] = (byte) ~(firstValue[i] | secondValue[i]);
				}
				return new Word(resultNor);
			case not:
				break;
			case or:
				byte[] resultOr = new byte[4];
				for(int i = 0; i < resultOr.length; i++) {
					resultOr[i] = (byte) (firstValue[i] | secondValue[i]);
				}
				return new Word(resultOr);
			case ori:
				byte[] resultOri = new byte[4];
				for(int i = 0; i < resultOri.length; i++) {
					resultOri[i] = (byte) (firstValue[i] | secondValue[i]);
				}
				return new Word(resultOri);
			case xor:
				byte[] resultXor = new byte[4];
				for(int i = 0; i < resultXor.length; i++) {
					resultXor[i] = (byte) (firstValue[i] ^ secondValue[i]);
				}
				return new Word(resultXor);
			case xori:
				byte[] resultXori = new byte[4];
				for(int i = 0; i < resultXori.length; i++) {
					resultXori[i] = (byte) (firstValue[i] ^ secondValue[i]);
				}
				return new Word(resultXori);
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
				throw new InstructionException("Invalid/Unsupported Instruction.",instruction);
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