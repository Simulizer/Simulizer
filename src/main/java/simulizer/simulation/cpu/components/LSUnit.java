package simulizer.simulation.cpu.components;

import java.math.BigInteger;
import java.util.Observable;

import simulizer.simulation.data.representation.Word;


/**this class represents the Load Store Unit of the simulated CPU
 * 
 * @author Charlie Street
 *
 */
public class LSUnit extends Observable
{
	private Word temp;//used for temporary data storage and transport (alternative to buses)
	private RegisterBlock registers;//access to the registers
	private MainMemory memory;
	private InstructionRegister instructionRegister;
	private ControlUnit controlUnit;
	
	/**constructor initialises all fields
	 * 
	 * @param registers the block of registers in the CPU
	 * @param memory the RAM used for this simulation
	 * @param instructionRegister the instruction register used in the simulation
	 * @param controlUnit the control unit of the CPU
	 */
	public LSUnit(RegisterBlock registers, MainMemory memory, InstructionRegister instructionRegister, ControlUnit controlUnit)
	{
		super();
		this.temp = new Word();//initialising to default value
		this.registers = registers;
		this.memory = memory;
		this.instructionRegister = instructionRegister;
		this.controlUnit = controlUnit;
	}
	
	/**this method will return the temporary holding value in the LSunit
	 * 
	 * @return the temporary transport holding value
	 */
	public Word getData()
	{
		return this.temp;
	}
	
	/** this method sets the temporary holding value in the LSUnit
	 * 
	 * @param word the word to set temp to
	 */
	public synchronized void setData(Word word)
	{
		this.temp = word;
	}
	
	/**method reads something from a register
	 * 
	 * @param index the register index to select
	 * @return the word containing that register value
	 */
	public Word readFromRegister(int index)
	{
		notifyObservers();
		setChanged();
		return this.registers.getRegister(index).getData();
	}
	
	/**method writes to one of the registers
	 * 
	 * @param index the register index to write to
	 * @param toWrite the word to write to the intended register
	 */
	public synchronized void writeToRegister(int index, Word toWrite)
	{
		this.registers.setRegister(index, toWrite);
		notifyObservers();
		setChanged();
	}
	
	
	/**reads a word from memory at an integer address
	 * 
	 * @param address the memory index of the intended word
	 * @return the word stored at that location in memory
	 */
	public Word readFromMemory(int address)
	{
		notifyObservers();
		setChanged();
		return new Word(new BigInteger(this.memory.readFromMem(address,4)));
	}
	
	/**this method writes to memory from the L/S unit
	 * 
	 * @param address the address in memory to store
	 * @param toStore the word to store in memory
	 */
	public synchronized void writeToMemory(int address, Word toStore)
	{
		//assert (address >= this.memory.getDataEndHeapStart());//for testing, may change
		
		this.memory.writeToMem(address, toStore.getWord().toByteArray());
		
		notifyObservers();
		setChanged();
	}
	
	/**this method takes whatever is stored in the IR and puts it in it's
	 * temporary storage
	 */
	public void receiveInstructionRegister()
	{
		this.setData(this.instructionRegister.getData());
		
		notifyObservers();
		setChanged();
	}
	
	/**this method puts whatever is in the LS units
	 * temporary storage into the Instruction Register
	 */
	public void sendInstructionRegister()
	{
		this.instructionRegister.setData(this.getData());
		
		notifyObservers();
		setChanged();
	}
	
	/**this method receives data from the control unit and saves it in the
	 * temporary data of the Load/Store Unit
	 */
	public void receiveControlUnit()
	{
		this.setData(this.controlUnit.getData());
		
		notifyObservers();
		setChanged();
	}
	
	/**this method sends the contents of temp
	 * to the control unit
	 */
	public void sendControlUnit()
	{
		this.controlUnit.setData(this.getData());
		
		notifyObservers();
		setChanged();
	}
}
