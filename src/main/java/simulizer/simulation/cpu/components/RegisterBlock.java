package simulizer.simulation.cpu.components;

import java.util.Observable;

import simulizer.assembler.representation.Register;
import simulizer.simulation.data.representation.Word;

/**this class represents the entire block of general purpose registers
 * there are 32 of these registers
 * @author Charlie Street
 *
 */
public class RegisterBlock extends Observable
{
	private GPRegister[] registers;
	
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
		this.registers = new GPRegister[32];
		for(int i = 0; i < this.registers.length; i++)
		{
			this.registers[i] = new GPRegister(Register.fromID(i));//setting up register with appropriate name
		}
	}
	
	/**returns register at specified index
	 * 
	 * @param name the register to get from
	 * @return the register at that index
	 */
	public GPRegister getRegister(Register name)
	{
		return this.registers[name.getID()];
	}
	
	/**this class sets a selected register to a given word
	 * 
	 * @param name the register name to write to
	 * @param word the word to insert into that register
	 */
	public void setRegister(Register name, Word word)
	{
		if(name.getID()!=0)//can't write to register zero
		{
			this.registers[name.getID()].setData(word);
		}
		else
		{
			//LOG PROBLEM
		}
	}
	
}
