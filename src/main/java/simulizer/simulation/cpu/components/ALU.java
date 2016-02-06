package simulizer.simulation.cpu.components;

import java.math.BigInteger;
import java.util.Observable;

import simulizer.simulation.data.representation.Word;

/**this class simulates the Arithemtic and Logic Unit
 * it contains operations for add, shift, mult
 * div, subtract, xor, or and, not
 * it will also include the immediate operations when appropriate
 * @author Charlie Street
 *
 */
public class ALU extends Observable {
	
	private final String TwoThirtyTwo = "4294967296";
	private Word temp;//temporary holding cell for transport etc.
	
	/**returns the temporary holding value
	 * this is a replacement for a bus
	 * @return the temporary holding value
	 */
	public Word getData()
	{
		return this.temp;
	}
	
	/**this method sets the temporary holding value for the ALU
	 * 
	 * @param word the new word to set the temp holding value too
	 */
	public synchronized void setData(Word word)
	{
		this.temp = word;
	}
	
	/**this method carries out exclusive or on two words
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the exclusive or of these two words
	 */
	public Word xor(Word firstWord, Word secondWord)
	{
		BigInteger firstInt = firstWord.getWord();
		BigInteger secondInt = secondWord.getWord();
		
		BigInteger result = firstInt.xor(secondInt);
		
		notifyObservers();
		setChanged();
		return new Word(result);
	}
	
	/**carries out inclusive or on two words, pretty much the same as xor
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the bitwise or of these two words
	 */
	public Word or(Word firstWord, Word secondWord)
	{
		BigInteger firstInt = firstWord.getWord();
		BigInteger secondInt = secondWord.getWord();
		
		BigInteger result = firstInt.or(secondInt);
		
		notifyObservers();
		setChanged();
		return new Word(result);
	}
	
	/**method carries out the and operation on two words
	 * very similar to or, xor with different conditions
	 * @param firstWord
	 * @param secondWord
	 * @return a word as the result of the and operation
	 */
	public Word and(Word firstWord, Word secondWord)
	{
		BigInteger firstInt = firstWord.getWord();
		BigInteger secondInt = secondWord.getWord();
		
		BigInteger result = firstInt.and(secondInt);
		
		notifyObservers();
		setChanged();
		return new Word(result);
	}
	
	/**this method will negate the bits of a word
	 * 
	 * @param toNegate the word to be negated
	 * @return the negated Word
	 */
	public Word not(Word toNegate)
	{
		BigInteger result = toNegate.getWord().not();
		
		notifyObservers();
		setChanged();
		return new Word(result);
	}
	
	/**this function carries out the nand operation on two binary values
	 * 
	 * @param firstWord the first value
	 * @param secondWord the second value
	 * @return the result of the nand operation in a word
	 */
	public Word nand(Word firstWord, Word secondWord)
	{
		Word and = this.and(firstWord, secondWord);
		Word result = this.not(and);
		notifyObservers();
		setChanged();
		return result;
	}
	
	/** this method carries out the nor operation on two binary values
	 * 
	 * @param firstWord the first binary value
	 * @param secondWord the second binary value
	 * @return the result of the nor operation on these two words
	 */
	public Word nor(Word firstWord, Word secondWord)
	{
		Word or = this.or(firstWord, secondWord);
		Word result = this.not(or);
		notifyObservers();
		setChanged();
		return result;
	}
	
	/**generic shift, what direction the shift is in depends on the sign of the shift number
	 * 
	 * @param toShift the word to be shifted
	 * @param shiftNumber the shift amount
	 * @return the word shifted
	 */
	public Word shift(Word toShift, Word shiftNumber)
	{
		if(shiftNumber.getWord().compareTo(new BigInteger("0")) < 0)//if negative shift right
		{
			BigInteger result = toShift.getWord().shiftRight(shiftNumber.getWord().negate().intValue());
			result = result.mod(new BigInteger(this.TwoThirtyTwo));
			return new Word(result);
		}
		else
		{
			BigInteger result = toShift.getWord().shiftLeft(shiftNumber.getWord().intValue());
			result = result.mod(new BigInteger(this.TwoThirtyTwo));
			return new Word(result);
		}
	}
	
	/**this function will add two numbers using twos complement
	 * on BigInteger
	 * @param num1 the first number
	 * @param num2 the second number
	 * @return the sum of the numbers
	 */
	public Word add(Word num1, Word num2)
	{
		notifyObservers();
		setChanged();
		
		BigInteger result = num1.getWord().add(num2.getWord());
		result = result.mod(new BigInteger(this.TwoThirtyTwo));
		
		return new Word(result);
		
	}
	
	/**this method subtracts one twos complement number
	 * from the other.The numbers are bigInts
	 * @param num1 the first number
	 * @param num2 the number to subtract away from num1
	 * @return the subtraction of num1 - num2
	 */
	public Word sub(Word num1, Word num2)
	{
		notifyObservers();
		setChanged();
		
		BigInteger result = num1.getWord().subtract(num2.getWord());
		result = result.mod(new BigInteger(this.TwoThirtyTwo));
		
		return new Word(result);
	}
	
	/**this method multiplies two 2s complement numbers together
	 * these numbers are represented in big ints
	 * @param num1 the first number
	 * @param num2 the second number
	 * @return the multiplication of the two, ignoring overflow
	 */
	public Word mult(Word num1, Word num2)
	{
		notifyObservers();
		setChanged();
		
		BigInteger result = num1.getWord().multiply(num2.getWord());
		result = result.mod(new BigInteger(this.TwoThirtyTwo));
		
		return new Word(result);
	}
	
	/**this method divides two numbers represented as big integers
	 * 
	 * @param num1 the first number
	 * @param num2 the number to divide by
	 * @return the division of the two numbers in binary format
	 */
	public Word div(Word num1, Word num2)
	{
		notifyObservers();
		setChanged();
		
		BigInteger result = num1.getWord().divide(num2.getWord());
		result = result.mod(new BigInteger(this.TwoThirtyTwo));
		
		return new Word(result);
	}
	
	
	
}
