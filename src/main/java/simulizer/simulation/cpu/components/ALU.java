package simulizer.simulation.cpu.components;

import java.util.Optional;

import simulizer.assembler.representation.Instruction;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.InstructionException;


/**this class represents the ALU in the CPU
 * all it is capable of doing is carrying out various operations
 * and then returning the result
 * @author Charlie Street
 *
 */
public class ALU {

    public static final byte[] branchTrue = new byte[]{0b1,0b1,0b1,0b1};//if branch returns true
    public static final byte[] branchFalse = new byte[]{0b0,0b0,0b0,0b0};//if branch returns false
    public static boolean branchFlag = false;//flag to determine branching (true = successful branch has been executed)

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
                return encodeS(Math.abs(decodeS(firstValue)));
            case and:
                byte[] resultAnd = new byte[4];
                for(int i = 0; i < resultAnd.length; i++) {
                    resultAnd[i] = (byte) (firstValue[i] & secondValue[i]);
                }
                return new Word(resultAnd);
            case add:
                return encodeS(decodeS(firstValue) + decodeS(secondValue));
            case addu:
                return encodeU(decodeU(firstValue) + decodeU(secondValue));
            case addi:
                return encodeS(decodeS(firstValue) + decodeS(secondValue));
            case addiu:
                return encodeU(decodeU(firstValue) + decodeU(secondValue));
            case sub:
                return encodeS(decodeS(firstValue) - decodeS(secondValue));
            case subu:
                return encodeU(decodeU(firstValue) - decodeU(secondValue));
            case subi:
                return encodeS(decodeS(firstValue) - decodeS(secondValue));
            case subiu:
                return encodeU(decodeU(firstValue) - decodeU(secondValue));
            case mul:
                return encodeS(decodeS(firstValue) * decodeS(secondValue));
            case mulo:
                return encodeS(decodeS(firstValue) * decodeS(secondValue));//might have to take more into account with overflow
            case mulou:
                return encodeU(decodeU(firstValue) * decodeU(secondValue));//might have to take more into account with overflow
            case div:
                return encodeS(decodeS(firstValue) / decodeS(secondValue));
            case divu:
                return encodeU(decodeU(firstValue) / decodeU(secondValue));
            case neg:
                return encodeS(decodeS(firstValue)*-1);
            case negu:
                return encodeU(decodeS(firstValue)*-1);//is this correct?
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
            	branchFlag = true;
                return new Word(branchTrue);
            case beq:
                for(int i = 0; i < firstValue.length; i++) {
                    if(firstValue[i] != secondValue[i]) {
                    	branchFlag = false;
                        return new Word(branchFalse);
                    }
                }
                branchFlag = true;
                return new Word(branchTrue);//if all bytes equal
            case bne:
                for(int i = 0; i < firstValue.length; i++) {
                    if(firstValue[i] != secondValue[i]) {//if a difference found
                    	branchFlag = true;
                        return new Word(branchTrue);
                    }
                }
                branchFlag = false;
                return new Word(branchFalse);//if all bytes equal then false
            case bgez:
                if(decodeS(firstValue) >= 0) {
                	branchFlag = true;
                    return new Word(branchTrue);
                }
                else {
                	branchFlag = false;
                    return new Word(branchFalse);
                }
            case bgtz:
                if(decodeS(firstValue) > 0) {
                	branchFlag = true;
                    return new Word(branchTrue);
                }
                else {
                	branchFlag = false;
                    return new Word(branchFalse);
                }
            case blez:
                if(decodeS(firstValue) <= 0) {
                	branchFlag = true;
                    return new Word(branchTrue);
                }
                else {
                	branchFlag = false;
                    return new Word(branchFalse);
                }
            case bltz:
                if(decodeS(firstValue) < 0) {
                	branchFlag = true;
                    return new Word(branchTrue);
                }
                else {
                	branchFlag = false;
                    return new Word(branchFalse);
                }
            case beqz:
                if(decodeS(firstValue) == 0) {
                	branchFlag = true;
                    return new Word(branchTrue);
                }
                else {
                	branchFlag = false;
                    return new Word(branchFalse);
                }
            case move:
                return new Word(firstValue);
            case seq:
            	for(int i = 0; i < firstValue.length; i++) {
                    if(firstValue[i] != secondValue[i]) {
                        return encodeS(0);
                    }
                }
            	return encodeS(1);
            case sge:
            	if(decodeS(firstValue) >= decodeS(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sgeu:
            	if(decodeU(firstValue) >= decodeU(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sgt:
            	if(decodeS(firstValue) > decodeS(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sgtu:
            	if(decodeU(firstValue) > decodeU(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sle:
            	if(decodeS(firstValue) <= decodeS(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sleu:
            	if(decodeU(firstValue) >= decodeU(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case slt:
            	if(decodeS(firstValue) < decodeS(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case slti:
            	if(decodeS(firstValue) < decodeS(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sltu:
            	if(decodeU(firstValue) < decodeU(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sltiu:
            	if(decodeU(firstValue) < decodeU(secondValue)) {return encodeS(1);}
            	return encodeS(0);
            case sne:
            	for(int i = 0; i < firstValue.length; i++) {
                    if(firstValue[i] != secondValue[i]) {
                        return encodeS(1);
                    }
                }
            	return encodeS(0);
            default:
                throw new InstructionException("Invalid/Unsupported Instruction.",instruction);
        }
    }

    /**
     * interpret a byte array as a 4 byte signed integer
     *
     * @param word the word to interpret
     * @return the interpreted value
     */
    private static long decodeS(byte[] word) {
        return DataConverter.decodeAsSigned(word);
    }

    /**
     * interpret a byte array as a 4 byte unsigned integer
     *
     * @param word the word to interpret
     * @return the interpreted value
     */
    private static long decodeU(byte[] word) {
        return DataConverter.decodeAsUnsigned(word);
    }

    /**
     * take a value interpreted as having a sign and encode it as a word
     *
     * @param value the signed value to encode
     * @return the encoded value
     */
    private static Word encodeS(long value) {
        return new Word(DataConverter.encodeAsSigned(value));
    }

    /**
     * take a value interpreted as being unsigned and encode it as a word
     *
     * @param value the unsigned value to encode
     * @return the encoded value
     */
    private static Word encodeU(long value) {
        return new Word(DataConverter.encodeAsUnsigned(value));
    }

}