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
	private LSUnit lsUnit;
	private ALU ALU;
	private ControlUnit controlUnit;
	
	/**constructor will initialise the buses and call for the registers to be set up
	 * 
	 * @param lsUnit the bus connecting the register block and the LS unit
	 * @param ALU the bus between the ALU and the register block
	 * @param controlUnit the bus connecting the register block and the control unit
	 */
	public RegisterBlock(LSUnit lsUnit, ALU ALU, ControlUnit controlUnit)
	{
		super();
		setUpRegisters();
		this.lsUnit = lsUnit;
		this.ALU = ALU;
		this.controlUnit = controlUnit;
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
	
	/**retrieving info from the lsUnit and storing it in the appropriate register
	 * 
	 */
	/*public void retrievelsUnit()
	{
		int index = this.lsUnit.getRegisterIndex();//which register to store
		Word toStore = this.lsUnit.getData();
		this.getRegister(index).setData(toStore);//setting the data appropriately
		
		notifyObservers();
		setChanged();
	}*/
	
	/**sending something on the lsUnit
	 * no register index is necessary so I will use a convention 
	 * of -1 for the index in this case
	 * @param index the index of the register to put on the bus
	 */
	/*public void sendlsUnit(int index)
	{
		this.lsUnit.setRegisterIndex(-1);
		this.lsUnit.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}*/
	
	
	/**retrieving info from the ALU and storing it in the appropriate register
	 * 
	 */
	/*public void retrieveALU()
	{
		int index = this.ALU.getRegisterIndex();
		Word storedWord = this.ALU.getData();
		this.getRegister(index).setData(storedWord);
		
		notifyObservers();
		setChanged();
	}*/
	
	/**sending something on the ALU bus
	 * setting register index to -1 as convention
	 * @param index the register to get the data from
	 */
	/*public void sendALU(int index)
	{
		this.ALU.setRegisterIndex(-1);
		this.ALU.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}*/
	
	/**retrieving info from the control unit and storing it in the appropriate register
	 * 
	 */
	/*public void retrievecontrolUnit()
	{
		int index = this.controlUnit.getRegisterIndex();
		Word storedWord = this.controlUnit.getData();
		this.getRegister(index).setData(storedWord);
		
		notifyObservers();
		setChanged();
	}*/
	
	/**sending something on the control bus
	 * the register index is set to -1 as it is not needed and that is my convention
	 */
	/*public void sendcontrolUnit(int index)
	{
		this.controlUnit.setRegisterIndex(-1);
		this.controlUnit.setData(this.getRegister(index).getData());
		
		notifyObservers();
		setChanged();
	}*/
}
