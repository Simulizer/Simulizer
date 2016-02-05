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
		this.getRegister(index).setData(toStore);//setting the data appropriately
		
		notifyObservers();
		setChanged();
	}
	
	/**sending something on the LSBus
	 * no register index is necessary so I will use a convention 
	 * of -1 for the index in this case
	 * @param index the index of the register to put on the bus
	 */
	public void sendLSBus(int index)
	{
		this.LSBus.setRegisterIndex(-1);
		this.LSBus.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}
	
	
	/**retrieving info from the ALU and storing it in the appropriate register
	 * 
	 */
	public void retrieveALUBus()
	{
		int index = this.ALUBus.getRegisterIndex();
		Word storedWord = this.ALUBus.getData();
		this.getRegister(index).setData(storedWord);
		
		notifyObservers();
		setChanged();
	}
	
	/**sending something on the ALU bus
	 * setting register index to -1 as convention
	 * @param index the register to get the data from
	 */
	public void sendALUBus(int index)
	{
		this.ALUBus.setRegisterIndex(-1);
		this.ALUBus.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}
	
	/**retrieving info from the control unit and storing it in the appropriate register
	 * 
	 */
	public void retrieveControlBus()
	{
		int index = this.controlBus.getRegisterIndex();
		Word storedWord = this.controlBus.getData();
		this.getRegister(index).setData(storedWord);
		
		notifyObservers();
		setChanged();
	}
	
	/**sending something on the control bus
	 * the register index is set to -1 as it is not needed and that is my convention
	 */
	public void sendControlBus(int index)
	{
		this.controlBus.setRegisterIndex(-1);
		this.controlBus.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}
}
