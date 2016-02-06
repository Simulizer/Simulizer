package simulizer.simulation.cpu.components;

import simulizer.simulation.data.representation.Word;

/**this class represents the control unit of the 
 * simulated processor
 * @author Charlie Street
 *
 */
public class ControlUnit
{
	private Word temp;//used for temporary data storage and transport  
	
	
	/**this method gets the temporary data being held in the control unit
	 * 
	 * @return the temporary transport holder
	 */
	public Word getData()
	{
		return this.temp;
	}
	
	/**sets the temporary data holder in the control unit,
	 * this will be set by the control unit or other components that can access it
	 * @param word the word to store
	 */
	public synchronized void setData(Word word)
	{
		this.temp = temp;
	}
}
