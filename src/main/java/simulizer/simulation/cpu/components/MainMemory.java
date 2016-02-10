package simulizer.simulation.cpu.components;

import java.math.BigInteger;
import java.util.Map;
import java.util.Observable;



import simulizer.simulation.data.representation.Word;

/**
 * this class represents the RAM of our simulator it is represented via a large
 * Word array, which will be partitioned this memory has the absence of OS
 * reserved data (which we don't need) the stack is also represented in a
 * different data structure
 * 
 * @author Charlie Street
 */
public class MainMemory extends Observable {
	private final int MEM_SIZE;// the size of the memory we are allowing
								// (standard is 4mb worth of words)
	
	private Address startOfStaticData;//start of the static data segment
	private Address startOfDynamicData; //the end of the static data segment
	private Address endOfMemory;

	public static boolean zeroInit = true;// to toggle between different
											// initialisations
	
	private Map<Integer,Statement> textSegment;
	private byte[] staticDataSegment;
	private DynamicDataSegment heap;
	

	/**
	 * this constructor just intialises the memory and then initialises all
	 * partitions in it
	 * 
	 */
	public MainMemory(Map<Integer,Statement> textSegment, byte[] staticDataSegment,Address startOfStaticData, Address startOfDynamicData, Address endOfMemory) {
		this.MEM_SIZE = 1048576;
		this.startOfStaticData = startOfStaticData;
		this.startOfDynamicData = startOfDynamicData;
		this.endOfMemory = endOfMemory;
		
		this.textSegment = textSegment;
		this.staticDataSegment = staticDataSegment;
		this.heap = new DynamicDataSegment();
		
		/*if (zeroInit){// enforcing the toggle of initialisations
			this.initialiseRAMZeroed();
		} else {
			this.initialiseRAMDebug();
		}*/
		
	}

	/**
	 * this method will set the 'RAM' to all zeros i.e empty words
	 */
	/*private void initialiseRAMZeroed() {
		for (int i = 0; i < this.RAM.length; i++) {
			this.RAM[i] = new Word();
			// setting to zeroed word
		}
	}*/

	/** this method will initialise all of the memory to bytes
	 * consisting of 0xCC, this should be a lot easier 
	 * when it comes to debugging code to look inside memory
	 * 
	 */
	/*private void initialiseRAMDebug() {
		for(int i = 0; i < this.RAM.length; i++) {
			this.RAM[i] = new Word(new BigInteger("3435973836"));
			//now obvious to find in memory
		}
	}*/

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
					//DO SOMETHING HERE
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


}
