package simulizer.simulation.cpu.components;

import java.util.ArrayList;

/**this class represents the dynamic heap section of the memory 
 * for our simulated Mips processor
 * @author Charlie Street
 *
 */
public class DynamicDataSegment 
{
	private ArrayList<Byte> heap;
	private int pointer;
	
	/**this constructor just initialises the heap and it's end pointer
	 * 
	 */
	public DynamicDataSegment()
	{
		this.heap = new ArrayList<Byte>();
		this.pointer = 0;
	}
	
	/**returns the current size of the heap
	 * 
	 * @return the size of the heap
	 */
	public int size()
	{
		return this.heap.size();
	}
	
	/**this method will add bytes new bytes onto the heap
	 * and return the pointer to the start of that block
	 * in the negative argument case, it will return the break
	 * @param bytes the number of bytes to add to the heap
	 * @return the pointer to the start of that block
	 */
	public int sbrk(int bytes)
	{
		if(bytes < 0)
		{
			this.pointer -= bytes;
			return this.pointer;
		}
		else
		{
			for(int i = 0; i < bytes; i++)
			{
				this.heap.add(new Byte(null));//set to a null byte (probably fairly accurate to reality)
			}
			
			int result = this.pointer;
			this.pointer += bytes;//increasing the pointer
			return result;
		}
	}
	
	/**will set a byte at a given position
	 * 
	 * @param toSet the byte to write into the heap
	 * @param position the position in the heap
	 */
	public void setByte(byte toSet, int position)
	{
		if(position < this.heap.size())
		{
			this.heap.set(position,toSet);
		}
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
			if(i < this.heap.size())
			{
				this.heap.set(i, toSet[i-startPos]);
			}
		}
	}
	
	/**method will get n bytes from the heap 
	 * 
	 * @param startPosition the start address in the heap
	 * @param length the number of bytes to retrieve
	 * @return the bytes in an array
	 */
	public byte[] getBytes(int startPosition, int length)
	{
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++)
		{
			if(startPosition+i < this.heap.size())
			{
				result[i] = this.heap.get(startPosition+i);
			}
		}
		
		return result;
	}
}
