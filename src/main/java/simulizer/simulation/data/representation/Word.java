package simulizer.simulation.data.representation;

/**
 * this class represents a single 4-byte word, with appropriate operations
 * included to be carried out on them
 * 
 * @author Charlie Street
 */
public class Word {
	private String word;

	/**
	 * constructor just initialises the word field
	 * 
	 * @param word
	 *            the word in question
	 * @throws Exception
	 *             if word of wrong size
	 */
	public Word(String word) {
		assert (word.length() == 32);// asserting that the word length is 4
		this.word = word;
	}

	/** Separate constructor for initialising to all zeros */
	public Word() {
		this.word = "00000000000000000000000000000000";
	}

	/**
	 * this method will return the word encapsulated in this class
	 * 
	 * @return the word
	 */
	public String getWord() {
		return this.word;
	}


}