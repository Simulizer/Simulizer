package simulizer.simulation.CPU_Components;

import java.util.Observable;

import simulizer.simulation.Data_Representation.RegisterInfo;
import simulizer.simulation.Data_Representation.Word;

/**this class represents the entire block of general purpose registers
 * there are 32 of these registers
 * @author Charlie Street
 *
 */
public class RegisterBlock extends Observable
{
	private Register[] registers;
	private RegisterBus LSBus;
	private RegisterBus ALUBus;
	private RegisterBus controlBus;
	
	/**constructor will initialise the buses and call for the registers to be set up
	 * 
	 * @param LSBus the bus connecting the register block and the LS unit
	 * @param ALUBus the bus between the ALU and the register block
	 * @param controlBus the bus connecting the register block and the control unit
	 */
	public RegisterBlock(RegisterBus LSBus, RegisterBus ALUBus, RegisterBus controlBus)
	{
		super();
		setUpRegisters();
		this.LSBus = LSBus;
		this.ALUBus = ALUBus;
		this.controlBus = controlBus;
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
	
	/**retrieving info from the LSBus and storing it in the appropriate register
	 * 
	 */
	public void retrieveLSBus()
	{
		int index = this.LSBus.getRegisterIndex();//which register to store
		Word toStore = this.LSBus.getData();
		this.registers[index].setData(toStore);//setting the data appropriately
		
		notifyObservers();
		setChanged();
	}
	
}
