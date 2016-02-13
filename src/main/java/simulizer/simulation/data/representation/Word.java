package simulizer.simulation.data.representation;

import java.math.BigInteger;

/**
 * this class represents a single 4-byte word, with appropriate operations
 * included to be carried out on them
 * 
 * @author Charlie Street
 */
public class Word {
	private final String modBound = "" + (1L << 32);
	private BigInteger word;

	/**
	 * constructor just initialises the word field
	 * 
	 * @param word
	 *            the word in question
	 * @throws Exception
	 *             if word of wrong size
	 */
	public Word(BigInteger word) {
		this.word = word;
	}

	/** Separate constructor for initialising to zero */
	public Word() {
		this.word = new BigInteger("0");
	}

	/**
	 * this method will return the word encapsulated in this class
	 * 
	 * @return the word
	 */
	public BigInteger getWord() {
		return this.word;
	}

	/**method adds two 32 bit valuestogether
	 * any carry will be ignored
	 * @param word the word to add
	 */
	public Word add(Word word)
	{
		BigInteger num1 = this.getWord();
		BigInteger num2 = word.getWord();
		
		
		return new Word((num1.add(num2)).mod(new BigInteger(modBound)));
	}
}