package simulizer.simulation.cpu.components;

import java.math.BigInteger;
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

	public static boolean zeroInit = true;// to toggle between different
											// initialisations

	private Word[] ram;
	@SuppressWarnings("unused")
	private int codeStart;
	private int codeEndDataStart;
	private int dataEndHeapStart;

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
	 */
	public MainMemory(int codeStart, int codeEndDataStart, int dataEndHeapStart) {
		this.MEM_SIZE = 1048576;
		this.ram = new Word[this.MEM_SIZE];

		if (zeroInit) {// enforcing the toggle of initialisations
			this.initialiseRAMZeroed();
		} else {
			this.initialiseRAMDebug();
		}

		this.codeStart = codeStart;
		this.codeEndDataStart = codeEndDataStart;
		this.dataEndHeapStart = dataEndHeapStart;
	}

	/**
	 * this method will set the 'RAM' to all zeros i.e empty words
	 */
	private void initialiseRAMZeroed() {
		for (int i = 0; i < this.ram.length; i++) {
			this.ram[i] = new Word();
			// setting to zeroed word
		}
	}

	/**
	 * this method will initialise all of the memory to bytes consisting of
	 * 0xCC, this should be a lot easier when it comes to debugging code to look
	 * inside memory
	 * 
	 */
	private void initialiseRAMDebug() {
		for (int i = 0; i < this.ram.length; i++) {
			this.ram[i] = new Word(new BigInteger("3435973836"));
			// now obvious to find in memory
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
	 * returns the word at a given address
	 * 
	 * @param index
	 *            address in integer form
	 * @return the word at said address
	 */
	public Word getWord(int index) {
		return this.ram[index];
	}

	/**
	 * sets a word in memory at a given index/address
	 * 
	 * @param index
	 *            the index in memory
	 * @param toSet
	 *            the contents to store at said address
	 */
	public synchronized void setWord(int index, Word toSet) {
		this.ram[index] = toSet;
	}

}
