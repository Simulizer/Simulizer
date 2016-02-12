package simulizer.simulation.cpu.components;

import java.util.Optional;

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
	
	private static byte[] branchTrue = new byte[]{0b1,0b1,0b1,0b1};//if branch returns true
	private static byte[] branchFalse = new byte[]{0b0,0b0,0b0,0b0};//if branch returns false
	
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
	public Word execute(Instruction instruction, Optional<Word> firstWord, Optional<Word> secondWord) throws InstructionException
	{
		byte[] firstValue;
		byte[] secondValue;
		
		if(firstWord.isPresent())//if a value stored
		{
			firstValue = firstWord.get().getWord();
		}
		else
		{
			throw new InstructionException("No operand given for alu operation", instruction);
		}
		
		if(secondWord.isPresent())//if a value stored
		{
			secondValue = secondWord.get().getWord();
		}
		else
		{
			secondValue = new byte[]{0x00,0x00,0x00,0x00};//this is probably the best workaround in case of something silly
			//this will either end up returning the original value, or produce undefined behaviour
		}
		
		switch(instruction) {//checking each possible instruction
			case abs:
				return new Word(serialiseSigned(Math.abs(loadAsSigned(firstValue))));
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
				return new Word(serialiseSigned(loadAsSigned(firstValue) * loadAsSigned(secondValue)));//might have to take more into account with overflow
			case mulou:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) * loadAsUnsigned(secondValue)));//might have to take more into account with overflow
			case div:
				return new Word(serialiseSigned(loadAsSigned(firstValue) / loadAsSigned(secondValue)));
			case divu:
				return new Word(serialiseUnsigned(loadAsUnsigned(firstValue) / loadAsUnsigned(secondValue)));
			case neg:
				return new Word(serialiseSigned(loadAsSigned(firstValue)*-1));
			case negu:
				return new Word(serialiseUnsigned(loadAsSigned(firstValue)*-1));//is this correct?
			case nor:
				byte[] resultNor = new byte[4];
				for(int i = 0; i < resultNor.length; i++) {
					resultNor[i] = (byte) ~(firstValue[i] | secondValue[i]);
				}
				return new Word(resultNor);
			case not:
				byte[] resultNot = new byte[4];
				for(int i = 0; i < resultNot.length; i++) {
					resultNot[i] = (byte) ~(firstValue[i]);
				}
				return new Word(resultNot);
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
				return new Word(branchTrue);
			case beq:
				for(int i = 0; i < firstValue.length; i++) {
					if(firstValue[i] != secondValue[i]) {
						return new Word(branchFalse);
					}
				}
				return new Word(branchTrue);//if all bytes equal
			case bne:
				for(int i = 0; i < firstValue.length; i++) {
					if(firstValue[i] != secondValue[i]) {//if a difference found
						return new Word(branchTrue);
					}
				}
				return new Word(branchFalse);//if all bytes equal then false
			case bgez:
				if(loadAsSigned(firstValue) >= 0)
				{
					return new Word(branchTrue);
				}
				else
				{
					return new Word(branchFalse);
				}
			case bgtz:
				if(loadAsSigned(firstValue) > 0)
				{
					return new Word(branchTrue);
				}
				else
				{
					return new Word(branchFalse);
				}
			case blez:
				if(loadAsSigned(firstValue) <= 0)
				{
					return new Word(branchTrue);
				}
				else
				{
					return new Word(branchFalse);
				}
			case bltz:
				if(loadAsSigned(firstValue) < 0)
				{
					return new Word(branchTrue);
				}
				else
				{
					return new Word(branchFalse);
				}
			case beqz:
				if(loadAsSigned(firstValue) > 0)
				{
					return new Word(branchTrue);
				}
				else
				{
					return new Word(branchFalse);
				}
			default:
				throw new InstructionException("Invalid/Unsupported Instruction.",instruction);
		}
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