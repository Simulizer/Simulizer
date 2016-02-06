package simulizer.simulation.cpu.components;

import simulizer.simulation.data.representation.BinaryConversions;
import simulizer.simulation.data.representation.Word;

/**this class simulates the Arithemtic and Logic Unit
 * it contains operations for add, shift, mult
 * div, subtract, xor, or and, not
 * it will also include the immediate operations when appropriate
 * @author Charlie Street
 *
 */
public class ALU {
	
	/**this method carries out exclusive or on two words
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the exclusive or of these two words
	 */
	public Word xor(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 1)//0,1 or 1,0
			{
				result += '1';
			}
			else
			{
				result += '0';
			}
		}
		
		return new Word(result);
	}
	
	/**carries out inclusive or on two words, pretty much the same as xor with a couple of changes
	 * 
	 * @param firstWord the first word being used
	 * @param secondWord the second word being used
	 * @return the bitwise or of these two words
	 */
	public Word or(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 0)//0,0
			{
				result += '0';
			}
			else
			{
				result += '1';
			}
		}
		
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
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 2)//1,1
			{
				result += '1';
			}
			else
			{
				result += '0';
			}
		}
		
		return new Word(result);
	}
	
	/**this method will negate the bit string of a word
	 * 
	 * @param toNegate the word to be negated
	 * @return the negated Word
	 */
	public Word not(Word toNegate)
	{
		String word = toNegate.getWord();
		String result = "";//where to store the negated string
		
		for(int i = 0; i < word.length(); i++)
		{
			if(word.charAt(i)=='1')//if 1 then 0
			{
				result += '0';
			}
			else if(word.charAt(i)=='0')//if 0 then 1
			{
				result += '1';
			}
		}
		
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
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 2)//1,1
			{
				result += '0';
			}
			else
			{
				result += '1';
			}
		}

		return new Word(result);
	}
	
	/** this method carries out the nor operation on two binary values
	 * 
	 * @param firstWord the first binary value
	 * @param secondWord the second binary value
	 * @return the result of the nor operation on these two words
	 */
	public Word nor(Word firstWord, Word secondWord)
	{
		String firstString = firstWord.getWord();
		String secondString = secondWord.getWord();
		String result = "";
		
		for(int i = 0; i < firstString.length(); i++)//looping through the strings
		{
			int firstChar = Integer.parseInt(firstString.charAt(i)+"");
			int secondChar = Integer.parseInt(secondString.charAt(i)+"");
			
			if(firstChar + secondChar == 0)//0,0
			{
				result += '1';
			}
			else
			{
				result += '0';
			}
		}
		
		return new Word(result);
	}
	
	/**this method will shift a word left , i.e get bigger
	 * 
	 * @param toShift the word to be shifted
	 * @param shiftNumber the amount to be shifted by
	 * @return the shifted word
	 */
	private Word shiftLeft(Word toShift, Word shiftNumber)
	{
		String toBeShifted = toShift.getWord();
		int shift = (int)BinaryConversions.getUnsignedLongValue(shiftNumber.getWord());
		
		for(int i = 0; i < shift; i++)//shifting to the left
		{
			toBeShifted += '0';
		}
		
		toBeShifted = toBeShifted.substring(shift);//taking away the now uneccsary characters
		
		return new Word(toBeShifted);
	}
	
	/**this method will shift a word right, i.e get smaller
	 * @param toShift the word to be shifted
	 * @param shiftNumber a NEGATIVE shift number
	 * @return the shifted word
	 */
	private Word shiftRight(Word toShift, Word shiftNumber)
	{
		String toBeShifted = toShift.getWord();
		int shift = (int)BinaryConversions.getSignedLongValue(shiftNumber.getWord()) * -1;//making positive 
		
		toBeShifted = toBeShifted.substring(0,toBeShifted.length()-shift);//carrying out the shift
		
		while(toBeShifted.length() < 32)//adding the padding
		{
			toBeShifted = '0' + toBeShifted;
		}
		
		return new Word(toBeShifted);
	}
	
	/**generic shift, what direction the shift is in depends on the sign of the shift number
	 * 
	 * @param toShift the word to be shifted
	 * @param shiftNumber the shift amount
	 * @return the word shifted
	 */
	public Word shift(Word toShift, Word shiftNumber)
	{
		if(BinaryConversions.getSignedLongValue(shiftNumber.getWord()) < 0)//if negative shift right
		{
			return shiftRight(toShift,shiftNumber);
		}
		else
		{
			return shiftLeft(toShift,shiftNumber);
		}
	}
	
	/**this function will add two numbers using twos complement
	 * 
	 * @param num1 the first number
	 * @param num2 the second number
	 * @return the sum of the numbers
	 */
	public Word add(Word num1, Word num2)
	{
		return num1.add(num2);
	}
	
	/**this method subtracts one twos complement number
	 * from the other.This worls by changing the sign of the 
	 * second number and then adding them
	 * @param num1 the first number
	 * @param num2 the number to subtract away from num1
	 * @return the subtraction of num1 - num2
	 */
	public Word sub(Word num1, Word num2)
	{
		String secondNum = BinaryConversions.switchSigns(num2.getWord());//switching signs of number 2
		return add(num1,new Word(secondNum));
	}
	
	/**this method multiplies two 2s complement numbers together
	 * assuming we ignore any overflow, it seems that
	 * any actual multiplication algorithms are at lead O(n^2)
	 * therefore the easiest way I can think of is to convert the two binary values
	 * to normal integers, multiply them and convert them back
	 * @param num1 the first number
	 * @param num2 the second number
	 * @return the multiplication of the two, ignoring overflow
	 */
	public Word mult(Word num1, Word num2)
	{
		long firstNumber = BinaryConversions.getSignedLongValue(num1.getWord());//getting the long values
		long secondNumber = BinaryConversions.getSignedLongValue(num2.getWord());
		long result = firstNumber * secondNumber;//the result of the multiplication
		
		return new Word(BinaryConversions.getSignedBinaryString(result));//converting back to the binary form
	}
	
	/**this method divides two numbers using the same method as mult
	 * 
	 * @param num1 the first number
	 * @param num2 the number to divide by
	 * @return the division of the two numbers in binary format
	 */
	public Word div(Word num1, Word num2)
	{
		long firstNumber = BinaryConversions.getSignedLongValue(num1.getWord());//converting the numbers
		long secondNumber = BinaryConversions.getSignedLongValue(num2.getWord());
		
		long result = firstNumber / secondNumber;//result of the integer division
		
		return new Word(BinaryConversions.getSignedBinaryString(result));
	}
	
	
	
}
