package simulizer.simulation.cpu.components;

import java.util.Arrays;
import java.util.Map;
import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Statement;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;

/**
 * this class represents the RAM of our simulator it is represented via a large
 * Word array, which will be partitioned this memory has the absence of OS
 * reserved data (which we don't need) the stack is also represented in a
 * different data structure
 * 
 * @author Charlie Street
 * @author mbway
 */
public class MainMemory {


	private Address startOfTextSegment;
	private Address startOfStaticData;//start of the static data segment
	private Address bottomOfDynamicData; //the end of the static data segment
	private Address topOfStack;
	private final Address endOfMemory; //TODO: why is this not used?
	private static final int mebibyte = 1024*1024;


	private Map<Address,Statement> textSegment;
	private byte[] staticDataSegment;
	private DynamicDataSegment heap;
	private StackSegment stack;


	/**
	 * this constructor just initialises the memory and then initialises all
	 * partitions in it
	 *
	 */
	MainMemory(Map<Address,Statement> textSegment, byte[] staticDataSegment, Address startTextSegment, Address startOfStaticData, Address bottomOfDynamicData, Address stackPointer) {
		this.startOfTextSegment = startTextSegment; //TODO: why is this not used?
		this.startOfStaticData = startOfStaticData;
		this.bottomOfDynamicData = bottomOfDynamicData;
		this.topOfStack = stackPointer;
		this.endOfMemory = new Address(2147483644);

		this.textSegment = textSegment;
		this.staticDataSegment = staticDataSegment;
		this.heap = new DynamicDataSegment(bottomOfDynamicData, mebibyte);
		int topOfHeap = bottomOfDynamicData.getValue() + mebibyte;
		int maxStackSize = topOfStack.getValue() - topOfHeap;
		this.stack = new StackSegment(maxStackSize);
	}

	// definition of being 'in' a segment: if you write 1 byte at that location, that byte would be inside the segment
	// eg the top of the stack is not inside the stack because it points past the highest element

	private boolean inDynamicSegment(int address) {
		return address >= bottomOfDynamicData.getValue()
				  && address < bottomOfDynamicData.getValue() + mebibyte;
	}
	private boolean inDynamicSegment(int address, int length) {
		return inDynamicSegment(address) && inDynamicSegment(address + length - 1);
	}

	private boolean inStaticSegment(int address) {
		return address >= startOfStaticData.getValue()
				&& address < startOfStaticData.getValue() + staticDataSegment.length;
	}
	private boolean inStaticSegment(int address, int length) {
		return inStaticSegment(address) && inStaticSegment(address + length - 1);
	}

	private boolean inStack(int address) {
		return address < topOfStack.getValue() // top of stack is not inside the stack
				&& address >= bottomOfDynamicData.getValue() + mebibyte;
	}
	private boolean inStack(int address, int length) {
		return inStack(address) && inStack(address + length - 1);
	}

	/**allows the use of sbrk outside of this memory class
	 * 
	 * @return the heap/dynamic data segment
	 */
	public DynamicDataSegment getHeap()
	{
		return this.heap;
	}
	/**this method will read from memory, in the places it is allowed to
	 * 
	 * @param address the start address to read from
	 * @param length the number of bytes to read
	 * @return those bytes from memory
	 * @throws StackException if invalid use of stack
	 */
	public byte[] readFromMem(int address, int length) throws MemoryException, HeapException, StackException
	{
        if(inStaticSegment(address, length)) {
            int relativeAddress = address - startOfStaticData.getValue();
			return Arrays.copyOfRange(staticDataSegment, relativeAddress, relativeAddress+length);

		} else if(inDynamicSegment(address, length)) {
			int relativeAddress = address - bottomOfDynamicData.getValue();
			return heap.getBytes(relativeAddress, length);

		} else if(inStack(address, length)) {
			int relativeAddress = address - topOfStack.getValue(); // will be negative
			return stack.getBytes(relativeAddress, length);

		} else {
			throw new MemoryException("Reading from invalid area of memory", new Address(address));
		}
	}

	/**
     * read bytes until a null character is read. Use this to extract strings from memory.
	 * An exception is thrown if the end of a segment is reached while scanning for a null character
	 *
	 * @param address the address to begin scanning at
	 * @return the bytes up to but _not_ including the null character
	 * @throws MemoryException
	 * @throws StackException
	 */
	public byte[] readUntilNull(int address) throws MemoryException, HeapException, StackException {
		if(inStaticSegment(address)) {
			int relativeAddress = address - startOfStaticData.getValue();
            for(int i = relativeAddress; i < staticDataSegment.length; ++i) {
                if(staticDataSegment[i] == '\0') {
					return Arrays.copyOfRange(staticDataSegment, relativeAddress, i); // exclusive so null not included
				}
			}
			throw new MemoryException("Reading from invalid area of memory (scanning for a null character)", new Address(address));

		} else if(inDynamicSegment(address)) {
			int relativeAddress = address - bottomOfDynamicData.getValue();
			return heap.readUntilNull(relativeAddress);

		} else if(inStack(address)) {
			int relativeAddress = address - topOfStack.getValue(); // will be negative
			return stack.readUntilNull(relativeAddress);

		} else {
			throw new MemoryException("Reading from invalid area of memory (scanning for a null character)", new Address(address));
		}
	}

	/**this method will write into memory
	 * it will contain some form of bounds checking but this may be slightly off
	 * @param address the address to start writing to
	 * @param toWrite the bytes to write
	 * @throws MemoryException
	 * @throws HeapException
	 * @throws StackException 
	 */
	public void writeToMem(int address, byte[] toWrite) throws MemoryException, HeapException, StackException
	{
		if(inStaticSegment(address, toWrite.length)) {
		    int relativeAddress = address - startOfStaticData.getValue();
			System.arraycopy(toWrite, 0, staticDataSegment, relativeAddress, toWrite.length);

		} else if(inDynamicSegment(address, toWrite.length)) {
		    int relativeAddress = address - bottomOfDynamicData.getValue();
            heap.setBytes(relativeAddress, toWrite);

		} else if(inStack(address, toWrite.length)) {
		    int relativeAddress = address - topOfStack.getValue(); // will be negative
			stack.setBytes(relativeAddress, toWrite);

		} else {
			throw new MemoryException("Writing to an invalid area of memory", new Address(address));
		}
	}
	
	/**separate method for reading from the text segment of the memory
	 *
	 * @param address the address to retrieve from
	 * @return the statement object at that address
	 */
	public Statement readFromTextSegment(Address address) throws MemoryException
	{
		Statement retrieved = textSegment.get(address);
		if(retrieved != null) {
			return retrieved;
		} else {
			throw new MemoryException("Reading from invalid area of memory",address);
		}
	}

}
