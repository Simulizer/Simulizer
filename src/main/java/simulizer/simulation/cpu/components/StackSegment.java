package simulizer.simulation.cpu.components;

import java.util.ArrayList;

import simulizer.assembler.representation.Address;
import simulizer.simulation.exceptions.StackException;


/**represents the stack of our main memory
 * 
 * @author Charlie Street
 *
 */
public class StackSegment {
	
	private Address stackPointer;//the stack pointer (memory and cpu will figure everything else out)
	private Address lowestAddress;//heap start + 1MB
	private ArrayList<Byte> stack;
	
	/**initialise stack to difference between stack pointer and lowest address
	 * 
	 * @param stackPointer the pointer in the stack
	 * @param lowestAddress the lowest address in memory
	 */
	public StackSegment(Address stackPointer, Address lowestAddress)
	{
		this.stackPointer = stackPointer;
		this.lowestAddress = lowestAddress;
		this.stack = new ArrayList<>();
		for(int i = 0; i < 4; i++)//giving stack an initial 4 bytes to work with, it then expands as necessary
		{
			this.stack.add((byte) 0x00);//initialising stack
		}
	}
	
	/**method reads a number of bytes from the stack
	 * 
	 * @param address the address relative to the 0 point of the stack
	 * @param length the number of bytes to read
	 * @return the byte array with the desired contents
	 * @throws StackException if reading goes out of bounds
	 */
	public byte[] getBytes(int address, int length) throws StackException
	{
		byte[] result = new byte[length];
		for(int i = 0; i < length; i++)
		{
			if(address+i < this.stack.size())
			{
				result[i] = this.stack.get(address+i);
			}
			else
			{
				throw new StackException("Invalid read on stack.", address+i);
			}
		}
		return result;
	}
	
	/**goes about writing onto the stack
	 * 
	 * @param address the start address of the setting
	 * @param toWrite the byte array to write
	 * @throws StackException if an invalid write is made
	 */
	public void setBytes(int address, byte[] toWrite) throws StackException
	{
		for(int i = address; i < address + toWrite.length; i++)
		{
			if(stack.size() > this.stackPointer.getValue() - this.lowestAddress.getValue())//bounds checking (need to do before and after)
			{
				throw new StackException("Stack overflow.", i);
			}
			
			if(i < this.stack.size())
			{
					this.stack.set(i, toWrite[i-address]);
			}
			else if (i == this.stack.size())
			{
				
				this.stack.add(toWrite[i-address]);//growing stack
			}
			else
			{
				throw new StackException("Invalid write onto stack.", i);
			}
			
			if(stack.size() > this.stackPointer.getValue() - this.lowestAddress.getValue())//bounds checking
			{
				throw new StackException("Stack overflow.", i);
			}
		}
	}
	
	/**gets the size of the stack
	 * 
	 * @return the stack size
	 */
	public int size()
	{
		return this.stack.size();
	}
}
