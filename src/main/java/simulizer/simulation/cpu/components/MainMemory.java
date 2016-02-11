package simulizer.simulation.cpu.components;

import java.util.Map;






import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Statement;

/**
 * this class represents the RAM of our simulator it is represented via a large
 * Word array, which will be partitioned this memory has the absence of OS
 * reserved data (which we don't need) the stack is also represented in a
 * different data structure
 * 
 * @author Charlie Street
 */
public class MainMemory {

	
	private Address startOfTextSegment;
	private Address startOfStaticData;//start of the static data segment
	private Address startOfDynamicData; //the end of the static data segment
	private final Address endOfMemory;

	
	private Map<Address,Statement> textSegment;
	private byte[] staticDataSegment;
	private DynamicDataSegment heap;
	

	/**
	 * this constructor just intialises the memory and then initialises all
	 * partitions in it
	 * 
	 */
	public MainMemory(Map<Address,Statement> textSegment, byte[] staticDataSegment, Address startTextSegment, Address startOfStaticData, Address startOfDynamicData) {
		this.startOfTextSegment = startTextSegment;
		this.startOfStaticData = startOfStaticData;
		this.startOfDynamicData = startOfDynamicData;
		this.endOfMemory = new Address(2147483644);
		
		this.textSegment = textSegment;
		this.staticDataSegment = staticDataSegment;
		this.heap = new DynamicDataSegment(this.startOfDynamicData);
		
	
	}

	/**this method will read from memory, in the places it is allowed to
	 * 
	 * @param address the start address to read from
	 * @param length the number of bytes to read
	 * @return those bytes from memory
	 */
	public byte[] readFromMem(int address, int length)
	{
		if((address >=  this.startOfStaticData.getValue() && address < this.startOfDynamicData.getValue()))//if in the static data part of memory
		{
			byte[] result = new byte[length];
			for(int i = 0; i < length; i++)
			{
				if(address-this.startOfStaticData.getValue()+i < this.startOfDynamicData.getValue())
				{
					result[i] = this.staticDataSegment[address-this.startOfStaticData.getValue()+i];//reading from the static data segment
				}
				else
				{
					//DO SOMETHING HERE ERROR!!!
				}
			}
			return result;
		}
		else if(address >= this.startOfDynamicData.getValue() && address <= this.endOfMemory.getValue())//if in the dynamic data segment
		{
			int heapVal = address-this.startOfDynamicData.getValue();
			return this.heap.getBytes(heapVal, length);
		}
		else
		{
			//DO SOMETHING, CANT READ FROM HERE
			return null;
		}
	}
	
	/**this method will write into memory
	 * it will contain some form of bounds checking but this may be slightly off
	 * @param address the address to start writing to
	 * @param toWrite the bytes to write
	 */
	public void writeToMem(int address, byte[] toWrite)
	{
		if(address >= this.startOfStaticData.getValue() && address < this.startOfDynamicData.getValue())//if in static data segment
		{
			for(int i = 0; i < toWrite.length; i++)
			{
				if(address + i < this.startOfDynamicData.getValue())
				{
					this.staticDataSegment[address-this.startOfStaticData.getValue() + i] = toWrite[i];
				}
				else
				{
					//REPORT PROBLEM
				}
			}
		}
		else if(address >= this.startOfDynamicData.getValue() && address <= this.endOfMemory.getValue())//write to heap
		{
			if(!(address-this.startOfDynamicData.getValue() + toWrite.length > this.heap.size()))
			{
				this.heap.setBytes(toWrite,address-this.startOfDynamicData.getValue());//writing to the heap
			}
			else//this will 
			{
				//LOG PROBLEM
			}
		}
	}
	
	/**separate method for reading from the text segment of the memory
	 *  
	 * @param address the address to retrieve from
	 * @return the statement object at that address
	 */
	public Statement readFromTextSegment(Address address)
	{
		Statement retrieved = this.textSegment.get(address);
		if(retrieved != null)
		{
			return retrieved;
		}
		else
		{
			return null;
			//LOG PROBLEM INVALID ADDRESS FOR TEXT SEGEMENT
		}
	}
	


}
