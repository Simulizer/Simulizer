package simulizer.simulation.cpu.components;

import java.util.ArrayList;

import simulizer.assembler.representation.Address;

/**this class represents the dynamic heap section of the memory 
 * for our simulated Mips processor
 * @author Charlie Street
 *
 */
public class DynamicDataSegment 
{
	private ArrayList<Byte> heap;
	private Address pointer;
	private Address startOfHeap;
	
	/**this constructor just initialises the heap and it's end pointer
	 * 
	 */
	public DynamicDataSegment(Address startOfHeap)
	{
		this.heap = new ArrayList<Byte>();
		this.pointer = startOfHeap;
		this.startOfHeap = startOfHeap;
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
	public Address sbrk(int bytes)
	{
		if(bytes < 0)
		{
			Address newPointer = new Address(this.pointer.getValue()- bytes);
			if(newPointer.getValue() >= this.startOfHeap.getValue())//error checking
			{
				this.pointer = newPointer;
			}
			else
			{
				//REPORT ERROR INVALID SBRK BEHIND HEAP START
			}
			return this.pointer;
		}
		else
		{
			for(int i = 0; i < bytes; i++)
			{
				this.heap.add(new Byte(null));//set to a null byte (probably fairly accurate to reality)
			}
			
			Address result = this.pointer;
			this.pointer = new Address(this.pointer.getValue() + bytes);//increasing the pointer
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
		else
		{
			//INVALID WRITE OUT OF BOUNDS
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
			else
			{
				//REPORT ERROR INVALID WRITE
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
			else
			{
				//REPORT ERROR INVALID READ
			}
		}
		
		return result;
	}
}
