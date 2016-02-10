package simulizer.simulation.cpu.components;

import java.util.ArrayList;

/**this class represents the dynamic heap section of the memory 
 * for our simulated Mips processor
 * @author Charlie Street
 *
 */
public class MipsHeap 
{
	private int endOfHeap;
	private ArrayList<Byte> heap;
	
	/**this constructor just initialises the heap and it's end pointer
	 * 
	 */
	public MipsHeap()
	{
		this.endOfHeap = 0;//initialising pointer in heap to 0
		this.heap = new ArrayList<Byte>();
	}
	
	/**this method will add bytes new bytes onto the heap
	 * and return the pointer to the start of that block
	 * @param bytes the number of bytes to add to the heap
	 * @return the pointer to the start of that block
	 */
	public int sbrk(int bytes)
	{
		for(int i = 0; i < bytes; i++)
		{
			this.heap.add(new Byte(null));//set to a null byte (probably fairly accurate to reality
		}
		
		int returnVal = this.endOfHeap;
		this.endOfHeap += bytes;//incrementing pointer
		return returnVal;
	}
	
	/**will set a byte at a given position
	 * 
	 * @param toSet the byte to write into the heap
	 * @param position the position in the heap
	 */
	public void setByte(byte toSet, int position)
	{
		this.heap.set(position,toSet);
	}
	
	/**allows to set multiple bytes in one go on the heap
	 * 
	 * @param toSet the byte[] to set
	 * @param startPos the start position in the heap
	 */
	public void setBytes(byte[] toSet, int startPos)
	{
		for(int i = startPos; i < startPos + toSet.length; i++)
		{
			this.heap.set(i, toSet[i-startPos]);
		}
	}
}
