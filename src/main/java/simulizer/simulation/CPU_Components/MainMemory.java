package simulizer.simulation.CPU_Components;

import java.util.Observable;
import simulizer.simulation.Data_Representation.Word;

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
	private Word[] RAM;
	private int codeStart;
	private int codeEndDataStart;
	private int dataEndHeapStart;
	private Bus LSBus;

	/**
	 * this constructor just intialises the memory and then initialises all
	 * partitions in it
	 * 
	 * @param codeStart
	 *            the place where the code partition starts
	 * @param codeEndDataStart
	 *            the place where the code partition stops and the data
	 *            partition begins
	 * @param dataEndHeapStart
	 *            the place where the data partition stops and the heap
	 *            partition begins
	 * @param LSBus
	 *            the communication between the LSUnit and main memory
	 */
	public MainMemory(int codeStart, int codeEndDataStart,
			int dataEndHeapStart, Bus LSBus) {
		this.MEM_SIZE = 1048576;
		this.RAM = new Word[this.MEM_SIZE];
		this.initialiseRAMZeroed();
		this.codeStart = codeStart;
		this.codeEndDataStart = codeEndDataStart;
		this.dataEndHeapStart = dataEndHeapStart;
		this.LSBus = LSBus;
	}

	/**
	 * this method will set the 'RAM' to all zeros i.e empty words
	 */
	private void initialiseRAMZeroed() {
		for (int i = 0; i < this.RAM.length; i++) {
			this.RAM[i] = new Word();// setting to zeroed word
		}
	}

	/**
	 * returns the location of the start of the code partition
	 * 
	 * @return the start of the code partition
	 */
	public int getCodeStart() {
		return this.getCodeStart();
	}

	/**
	 * changing the start of the code partition
	 * 
	 * @param codeStart
	 *            the new beginning of the partition
	 */
	public void setCodeStart(int codeStart) {
		this.codeStart = codeStart;
	}

	/**
	 * get code/data boundary
	 * 
	 * @return the end of the code boundary/start of the data boundary
	 */
	public int getCodeEndDataStart() {
		return this.codeEndDataStart;
	}

	/**
	 * changes the code/data partition boundary
	 * 
	 * @param codeEndDataStart
	 *            the new boundary
	 */
	public void setCodeEndDataStart(int codeEndDataStart) {
		this.codeEndDataStart = codeEndDataStart;
	}

	/**
	 * get data/heap boundary
	 * 
	 * @return the data/heap boundary
	 */
	public int getDataEndHeapStart() {
		return this.dataEndHeapStart;
	}

	/**
	 * changes the data heap boundary
	 * 
	 * @param dataEndHeapStart
	 *            the new boundary
	 */
	public void setDataEndHeapStart(int dataEndHeapStart) {
		this.dataEndHeapStart = dataEndHeapStart;
	}

	/**
	 * this method will take the contents of the LSBus and get the item at that
	 * address and load it back up
	 * 
	 */
	public void readFromMem() {
		Word address = this.LSBus.getData();
		int index = (int) address.getLongValue();
		Word retrieved = this.RAM[index];// retrieving information
		this.LSBus.setData(retrieved);// loading back onto bus
	}

	/**
	 * this method will write to memory, provided it is in a safe area of memory
	 * 
	 */
	public void writeToMem() {
		//TO IMPLEMENT, NEED TO KNOW INSTRUCTION SET UP FIRST
		//possibly second bus required
	}
}
