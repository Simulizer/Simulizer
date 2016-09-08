package simulizer.simulation.cpu.components;

import simulizer.assembler.representation.Address;
import simulizer.simulation.exceptions.HeapException;

import java.util.Arrays;

/**this class represents the dynamic heap section of the memory 
 * for our simulated Mips processor
 * @author Charlie Street
 * @author mbway
 *
 */
public class DynamicDataSegment 
{
	private Address heapBaseAddress;
	private int heapBreak; // index of one-past the highest element, relative to the base of the heap
	private int maxLength;
	private byte[] heap;


	public DynamicDataSegment(Address heapBaseAddress, int maxLength)
	{
		this.heapBaseAddress = heapBaseAddress;
		this.heapBreak = 0;
		this.maxLength = maxLength;
        heap = new byte[]{};
	}
	
	/**this method will add bytes new bytes onto the heap
	 * and return the pointer to the start of that block
	 * in the negative argument case, it will return the break
	 * @param additionalBytes the number of bytes (positive or negative) to add to the heap
	 * @return the pointer to the start (lowest address) of the newly allocated block (or the new break when shrinking)
	 */
	public Address sbrk(int additionalBytes) throws HeapException
	{
		if(additionalBytes % 4 != 0) {//spim only allows sbrk to be called with multiples of 4
			throw new HeapException("Sbrk needs to be called with multiples of 4 bytes.", heapBreak, heap.length);

		}else if(additionalBytes < -heap.length) { // shrink below 0 length
			throw new HeapException("sbrk requested shrink below the start of the heap.",heapBreak,heap.length);

		} if(additionalBytes < 0) {// shrink the heap
			heapBreak += additionalBytes; // additional bytes is negative
            return new Address(heapBaseAddress.getValue() + heapBreak);

		} else { // grow the heap

            if(heap.length + additionalBytes > maxLength) {
                throw new HeapException("sbrk requested extends past maximum heap length.",heapBreak,heap.length);
            }

            // at least a growth factor of 1.5. definitely enough to accommodate the additional requested bytes
            // but no longer than the maximum length (if the growth factor extends past it)
            int newLength = Math.min(Math.max((int) (heap.length * 1.5), heap.length+additionalBytes), maxLength);
            byte[] newHeap = new byte[newLength];
            // the old heap is placed at the start of the new heap
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;

			Address result = new Address(heapBaseAddress.getValue() + heapBreak);
			heapBreak += additionalBytes;
			return result;
		}
	}
	
	/**allows to set multiple bytes in one go on the heap
	 *
     * @param relativeAddress address relative to the base of the heap to place the MSB of the data
     * @param toWrite the data to write into the heap
	 */
	public void setBytes(int relativeAddress, byte[] toWrite) throws HeapException
	{
        if(toWrite.length <= 0) {
            throw new HeapException("Invalid write on heap. (non-positive length)", heapBreak, heap.length);
        } else if(relativeAddress + toWrite.length > heapBreak) {
			throw new HeapException("Invalid write on heap. (attempt to write above the break)", heapBreak, heap.length);
		} else if(relativeAddress < 0) {
			throw new HeapException("Invalid write on heap. (attempt to write below the heap)", heapBreak, heap.length);
		}

		// src, srcPos, dest, destPos, length
		System.arraycopy(toWrite, 0, heap, relativeAddress, toWrite.length);
	}
	
	/**method will get n bytes from the heap 
	 *
	 * @param relativeAddress address relative to the base of the heap to place the MSB of the data
	 * @param length the number of bytes to retrieve, starting at the given address
	 * @return the bytes in an array
	 */
	public byte[] getBytes(int relativeAddress, int length) throws HeapException
	{
		if(length <= 0) {
			throw new HeapException("Invalid read on heap. (non-positive length)", heapBreak, heap.length);
		} else if(relativeAddress + length > heapBreak) {
			throw new HeapException("Invalid read on heap. (attempt to read above the break)", heapBreak, heap.length);

		} else if(relativeAddress < 0) {
			throw new HeapException("Invalid read on heap. (attempt to read below the heap)", heapBreak, heap.length);
		}

		return Arrays.copyOfRange(heap, relativeAddress, relativeAddress+length);
	}
}
