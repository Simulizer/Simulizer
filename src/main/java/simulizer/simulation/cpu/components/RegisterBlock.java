package simulizer.simulation.cpu.components;

import java.util.Observable;

import simulizer.simulation.data.representation.RegisterInfo;
import simulizer.simulation.data.representation.Word;

/**this class represents the entire block of general purpose registers
 * there are 32 of these registers
 * @author Charlie Street
 *
 */
public class RegisterBlock extends Observable
{
	private Register[] registers;
	
	/**constructor will initialise the buses and call for the registers to be set up
	 * 
	 * @param lsUnit the bus connecting the register block and the LS unit
	 * @param ALU the bus between the ALU and the register block
	 * @param controlUnit the bus connecting the register block and the control unit
	 */
	public RegisterBlock()
	{
		super();
		setUpRegisters();
	}
	
	/**method to declare and intialise the block of registers
	 * 
	 */
	private void setUpRegisters()
	{
		this.registers = new Register[32];
		for(int i = 0; i < this.registers.length; i++)
		{
			this.registers[i] = new Register(RegisterInfo.numberToName(i));//setting up register with appropriate name
		}
	}
	
	/**returns register at specified index
	 * 
	 * @param index the block index
	 * @return the register at that index
	 */
	public Register getRegister(int index)
	{
		return this.registers[index];
	}
	
	/**this class sets a selected register to a given word
	 * 
	 * @param index the register index
	 * @param word the word to insert into that register
	 */
	public void setRegister(int index, Word word)
	{
		this.registers[index].setData(word);
	}
	
}
