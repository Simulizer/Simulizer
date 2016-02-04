package main.java.simulizer.simulation.Data_Representation;

/**this class represents a single 4-byte word, with appropriate operations
 * included to be carried out on them
 * @author Charlie Street
 *
 */
public class Word 
{
	private byte[] word;
	
	/**constructor just initialises the word field
	 * @param word the word in question
	 * @throws Exception if word of wrong size
	 */
	public Word(byte[] word)
	{
		assert(word.length==4);//asserting that the word length is 4
		this.word = word;
	}
	
	/**Separate constructor for initialising to all zeros
	 * 
	 */
	public Word()
	{
		this.word = new byte[]{00000000,00000000,00000000,00000000};
	}
	
	/**this method will return the word encapsulated in this class
	 * 
	 * @return the word
	 */
	public byte[] getWord()
	{
		return this.word;
	}
	
}