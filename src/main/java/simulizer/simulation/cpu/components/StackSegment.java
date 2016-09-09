package simulizer.simulation.cpu.components;

import java.util.Arrays;

import simulizer.simulation.exceptions.StackException;


/** Stack memory segment for the CPU
 *
 * //TODO: potential optimisation: replace getBytes with getBytes(int address, byte[] array) so the buffer being written to can be re-used rather than reallocated each time bytes are read
 *
 * @author mbway
 *
 * memory access is performed 'upwards' towards the top of the stack, with the address passed to the accessing methods
 * being the lowest address (MSB) to access and length referring to a length 'above' and including the first address.
 *
 * since the stack grows downwards and values are stored in big Endian format (MSB in lowest address),
 * it is most efficient to store the bytes in an array with the top of the past the end of the array
 * and $sp be element 0 of the array
 *
 * topOfStack     is out of bounds
 * topOfStack - 1 is the last element of the array
 * $sp + 1        is the second element of the array (stack[1])
 * $sp            is the first element of the array (stack[0])
 *
 * example:
 *
 * index  0     1     2     3     4
 *       $sp                      topOfStack
 *      [ 0xAA  0xBB  0xCC  0xDD ]
 *              MSB         LSB
 *
 *   addresses are passed relative to topOfStack. ie highest element of the stack at relative address -1
 */
public class StackSegment {

	private int maxLength;
    private byte[] stack;

    private class ArrayRange {
        int MSBIndex;
        int LSBIndex;

        /**
         * construct an array range given an address relative to the top of the stack and a length
         * @param MSBAddress address of the MSB of the range, relative to the top of the stack (should be negative)
         * @param length the length of the array range
         */
        ArrayRange(int MSBAddress, int length) {
            // MSB => lowest address => lower index in array
            // index from the end of the array (top of stack)
            MSBIndex = stack.length + MSBAddress; // MSBAddress should be negative

            // LSB => highest address => higher index in array
            // -1 because want to be inclusive
            LSBIndex = MSBIndex + length - 1;
        }

        /**
         * @return whether the range includes elements above the top of the stack (invalid)
         */
        boolean spansAboveStack() {
            return LSBIndex >= stack.length;
        }

        /**
         * @return whether the range includes elements below the 'stack pointer'
         *          (the lowest address written to up to this point, not to be confused
         *           with the actual stack pointer of the CPU)
         */
        boolean spansBelowSp() {
            return MSBIndex < 0;
        }
    }

	/**initialise stack to difference between stack pointer and lowest address
	 *
     * @param maxLength the maximum number of bytes the stack is allowed to grow to
	 */
	public StackSegment(int maxLength)
	{
		this.maxLength = maxLength;
        int initialLength = Math.min(100, maxLength); // either 100 or the maximum length if max length <100
		stack = new byte[initialLength]; // a small starting stack
	}

    /**
     * @param MSBAddress the address to rest (relative to the top of the stack) (should be negative)
     */
    private boolean insideStackSegment(int MSBAddress) {
	    return -maxLength <= MSBAddress && MSBAddress < 0;
    }

	/** method reads a number of bytes from the stack.
	 *
	 * Reads from 'address' to 'address'+'length'-1 inclusive
	 *
	 * @param MSBAddress the address of the most significant byte relative to the top of the stack (should be negative)
	 * @param length the number of bytes to read
	 * @return the byte array with the desired contents
	 * @throws StackException if reading goes out of bounds
	 */
	public byte[] getBytes(int MSBAddress, int length) throws StackException
	{
	    ArrayRange r = new ArrayRange(MSBAddress, length);

        if(length <= 0) {
            throw new StackException("Invalid read on stack. (non-positive length)", r.MSBIndex, r.LSBIndex);
        }

		if(r.spansAboveStack()) {
			throw new StackException("Invalid read on stack. (attempt to read above the top)", r.MSBIndex, r.LSBIndex);

		} else if(r.spansBelowSp()) { // at least part of the requested range is below the allocated range

            // range still within valid stack memory (because if MSB is and range doesn't span above the stack then LSB is too)
            if(insideStackSegment(MSBAddress)) {
                // below the current stack pointer is only zeroes until written to
                byte[] result = new byte[length];
                for (int i = 0; i < length; ++i) {
                    int index = r.MSBIndex + i;
                    result[i] = index < 0 ? 0 : stack[index];
                }
                return result;
            } else {
                throw new StackException("Stack Overflow. (attempt to read from the stack beyond its maximum length)", r.MSBIndex, r.LSBIndex);
            }

		} else {
			// copyFrom, from (inclusive), to (exclusive)
		    return Arrays.copyOfRange(stack, r.MSBIndex, r.LSBIndex+1); // +1 because exclusive
		}
	}

	/**
	 * read bytes until a null character is read. Use this to extract strings from memory.
	 * An exception is thrown if the end of the stack is reached while scanning for a null character
	 *
	 * @param MSBAddress the relative address to begin scanning at
	 * @return the bytes up to but _not_ including the null character
	 * @throws StackException
	 */
	public byte[] readUntilNull(int MSBAddress) throws StackException {
		ArrayRange r = new ArrayRange(MSBAddress, 1);
		int i = r.MSBIndex;

		if(insideStackSegment(MSBAddress)) {
			for (; i < stack.length; ++i) {
				if (stack[i] == '\0') {
					return Arrays.copyOfRange(stack, r.MSBIndex, i); // exclusive so null is excluded
				}
			}
		}
		throw new StackException("Reading from invalid area of memory (scanning for a null character)", r.MSBIndex, i);
	}
	
	/**goes about writing onto the stack
	 *
	 * @param MSBAddress the address of the most significant byte relative to the top of the stack (should be negative)
	 * @param toWrite the byte array to write
	 * @throws StackException if an invalid write is made
	 */
	public void setBytes(int MSBAddress, byte[] toWrite) throws StackException
	{
	    ArrayRange r = new ArrayRange(MSBAddress, toWrite.length);

        if(toWrite.length <= 0) {
            throw new StackException("Invalid write on stack. (non-positive length)", r.MSBIndex, r.LSBIndex);

        } else if(r.spansAboveStack()) {
			throw new StackException("Invalid write to stack. (attempt to write above the top)", r.MSBIndex, r.LSBIndex);

		} else if(r.spansBelowSp()) { // must grow stack to at least the whole span is in range

            // range still within valid stack memory (because if MSB is and range doesn't span above the stack then LSB is too)
            if(!insideStackSegment(MSBAddress)) {
                throw new StackException("Stack Overflow. (attempt to write to the stack beyond its maximum length)", r.MSBIndex, r.LSBIndex);
            }

			// at least a growth factor of 1.5. definitely enough to accommodate the range being written to
            // but no longer than the maximum length (if the growth factor extends past it)
			int newLength = Math.min(Math.max((int) (stack.length * 1.5), Math.abs(MSBAddress)), maxLength);
			byte[] newStack = new byte[newLength];
            // the old stack is placed at the end of the new stack
			System.arraycopy(stack, 0, newStack, newLength-stack.length, stack.length);
			stack = newStack;

            // re-calculate indices
            r = new ArrayRange(MSBAddress, toWrite.length);
			if(r.spansBelowSp() || r.spansAboveStack()) {
				throw new StackException("Invalid write (this shouldn't happen).", r.MSBIndex, r.LSBIndex);
            }
		}

        // src, srcPos, dest, destPos, length
        System.arraycopy(toWrite, 0, stack, r.MSBIndex, toWrite.length);
	}
}
