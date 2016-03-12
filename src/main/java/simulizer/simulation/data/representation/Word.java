package simulizer.simulation.data.representation;


/**
 * this class represents a single 4-byte word
 * the word is immutable
 * 
 * @author Charlie Street
 */
public class Word {
	private final byte[] word;

	public static final Word ZERO = new Word(new byte[] {0, 0, 0, 0});

	/**the constructor just initialises the word
	 * so long as a byte array of length 4 has been passed in
	 * @param word the word to set
	 */
	public Word(byte[] word)
	{
		if(word.length == 4)
		{
			this.word = word;
		}
		else
		{
			//TODO: ERROR THROW LOGGER PROBLEM
			this.word = null;
		}
	}

	/**this method returns the word stored in this object
	 * 
	 * @return the word stored
	 */
	public byte[] getWord()
	{
		return this.word;
	}


}