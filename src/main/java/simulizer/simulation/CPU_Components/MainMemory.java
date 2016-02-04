package simulizer.simulation.CPU_Components;

import java.util.Observable;
import simulizer.simulation.Data_Representation.Word;

/** this class represents the RAM of our simulator it is represented via a large Word array, which will be partitioned this memory
 * has the absence of OS reserved data (which we don't need) the stack is also represented in a different data structure
 * @author Charlie Street */
public class MainMemory extends Observable {
	private final int MEM_SIZE;// the size of the memory we are allowing
								// (standard is 4mb worth of words)
	private Word[] RAM;
	private int codeStart;
	private int codeEndDataStart;
	private int dataEndHeapStart;
	private Bus LSBus;

	/** this constructor just intialises the memory and then initialises all partitions in it
	 * @param codeStart the place where the code partition starts
	 * @param codeEndDataStart the place where the code partition stops and the data partition begins
	 * @param dataEndHeapStart the place where the data partition stops and the heap partition begins
	 * @param LSBus the communication between the LSUnit and main memory */
	public MainMemory(int codeStart, int codeEndDataStart, int dataEndHeapStart, Bus LSBus) {
		this.MEM_SIZE = 1048576;
		this.RAM = new Word[this.MEM_SIZE];
		// initialise RAM here
		this.codeStart = codeStart;
		this.codeEndDataStart = codeEndDataStart;
		this.dataEndHeapStart = dataEndHeapStart;
		this.LSBus = LSBus;
	}
}
