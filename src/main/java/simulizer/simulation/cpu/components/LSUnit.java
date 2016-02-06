package simulizer.simulation.cpu.components;

import simulizer.simulation.data.representation.Word;


/**this class represents the Load Store Unit of the simulated CPU
 * 
 * @author Charlie Street
 *
 */
public class LSUnit 
{
	private Word temp;//used for temporary data storage and transport (alternative to buses)
	
	/**this method will return the temporary holding value in the LSunit
	 * 
	 * @return the temporary transport holding value
	 */
	public Word getData()
	{
		return this.temp;
	}
	
	/** this method sets the temporary holding value in the LSUnit
	 * 
	 * @param word the word to set temp to
	 */
	public synchronized void setData(Word word)
	{
		this.temp = word;
	}
}
