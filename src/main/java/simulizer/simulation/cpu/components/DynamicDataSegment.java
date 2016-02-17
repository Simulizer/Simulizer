package simulizer.simulation.cpu.components;

import java.util.ArrayList;

import simulizer.assembler.representation.Address;
import simulizer.simulation.exceptions.HeapException;

/**this class represents the dynamic heap section of the memory 
 * for our simulated Mips processor
 * @author Charlie Street
 *
 */
public class DynamicDataSegment 
{
	private final int megaByte = 1048576;//restricting heap size
	private ArrayList<Byte> heap;
	private Address breakOfHeap;
	private Address startOfHeap;
	
	/**this constructor just initialises the heap and it's end pointer
	 * 
	 */
	public DynamicDataSegment(Address startOfHeap)
	{
		this.heap = new ArrayList<Byte>();
		this.breakOfHeap = startOfHeap;
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
	public Address sbrk(int bytes) throws HeapException
	{
		if(bytes % 4 != 0)//spim only allows sbrk to be called with multiples of 4
		{
			throw new HeapException("Sbrk needs to be called with multiples of 4 bytes.", this.breakOfHeap, this.heap.size());
		}
		if(bytes < 0)
		{
			Address newPointer = new Address(this.breakOfHeap.getValue()- bytes);
			if(newPointer.getValue() >= this.startOfHeap.getValue())//error checking
			{
				this.breakOfHeap = newPointer;
			}
			else
			{
				throw new HeapException("Can't call sbrk with negative arguments behind the static data segment.",this.breakOfHeap,this.heap.size());
			}
			return this.breakOfHeap;
		}
		else
		{
			for(int i = 0; i < bytes; i++)
			{
				if(heap.size() < this.megaByte)
				{
					this.heap.add(new Byte((byte) 0x00));//set to a 0 byte (probably fairly accurate to reality)
				}
				else
				{
					throw new HeapException("Heap over 1MB in size.",this.breakOfHeap,this.heap.size());
				}
			}
			
			Address result = this.breakOfHeap;
			this.breakOfHeap = new Address(this.breakOfHeap.getValue() + bytes);//increasing the pointer
			return result;
		}
	}
	
	/**will set a byte at a given position
	 * 
	 * @param toSet the byte to write into the heap
	 * @param position the position in the heap
	 */
	public void setByte(byte toSet, int position) throws HeapException
	{
		if(position < this.heap.size())
		{
			this.heap.set(position,toSet);
		}
		else
		{
			throw new HeapException("Invalid write on heap. Out of Bounds.", this.breakOfHeap, this.heap.size());
		}
	}
	
	/**allows to set multiple bytes in one go on the heap
	 * 
	 * @param toSet the byte[] to set
	 * @param startPos the start position in the heap
	 */
	public void setBytes(byte[] toSet, int startPos) throws HeapException
	{
		for(int i = startPos; i < startPos + toSet.length; i++)
		{
			if(i < this.heap.size())
			{
				this.heap.set(i, toSet[i-startPos]);
			}
			else
			{
				throw new HeapException("Invalid write on heap. Out of Bounds.", this.breakOfHeap, this.heap.size());
			}
		}
	}
	
	/**method will get n bytes from the heap 
	 * 
	 * @param startPosition the start address in the heap
	 * @param length the number of bytes to retrieve
	 * @return the bytes in an array
	 */
	public byte[] getBytes(int startPosition, int length) throws HeapException
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
				throw new HeapException("Invalid read on heap. Out of Bounds.", this.breakOfHeap, this.heap.size());
			}
		}
		
		return result;
	}
}
