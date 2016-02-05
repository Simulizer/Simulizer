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
		while(word.length() < 32)//enforcing length checks
		{
			word = '0' + word;
		}
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

	/**method adds two 32 bit binary strings together
	 * any carry will be ignored
	 * @param word the word to add
	 */
	public Word add(Word word)
	{
		String original = this.getWord();
		String toAdd = word.getWord();
		String result = "";
		int carry = 0;//carry for adding
		for(int i = original.length()-1; i>= 0; i--)
		{
			int firstChar = Integer.parseInt(original.charAt(i)+"");
			int secondChar = Integer.parseInt(toAdd.charAt(i)+"");
			if(firstChar+secondChar+carry == 0)//different scenarios for addition
			{
				carry = 0;
				result = '0' + result;
			}
			else if(firstChar+secondChar+carry == 1)
			{
				carry = 0;
				result = '1' + result;
			}
			else if(firstChar+secondChar+carry == 2)
			{
				carry = 1;
				result = '0' + result;
			}
			else if(firstChar+secondChar+carry == 3)
			{
				carry = 1;
				result = '1' + result;
			}
		}
		return new Word(result);//returning string encapsulated in a word object
	}
}