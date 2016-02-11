package simulizer.simulation.data.representation;


/**
 * this class represents a single 4-byte word
 * the word is immutable 
 * 
 * @author Charlie Street
 */
public class Word {
	private byte[] word;
	
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
			//ERROR THROW LOGGER PROBLEM
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