package simulizer.simulation.set_up;

import simulizer.simulation.cpu.components.*;

/**this class will take a program object received from the assembler
 * and use this to set up our cpu
 * @author Charlie Street
 *
 */
public class SetUpCPU
{
	private ALU ALU;
	private ControlUnit controlUnit;
	private InstructionRegister instructionRegister;
	private LSUnit LSUnit;
	private MainMemory mainMemory;
	private ProgramCounter programCounter;
	private RegisterBlock registers;
	private Program programData;
	
	/**the constructor will set up all the components and fill everything appropriately
	 * 
	 * @param programData the program information received from the assembler
	 */
	public SetUpCPU(Program programData)
	{
		this.mainMemory = new MainMemory(null,null);//fill in
		this.registers = new RegisterBlock();
		this.controlUnit = new ControlUnit(null,null,this.registers,null,null);//setting null to start with to make the linking easier
		this.ALU = new ALU(this.registers,this.controlUnit);
		this.LSUnit = new LSUnit(this.registers,this.mainMemory,null,this.controlUnit);
		this.programCounter = new ProgramCounter(null,this.controlUnit,null,this.LSUnit);
		this.instructionRegister = new InstructionRegister(this.controlUnit,this.programCounter,this.LSUnit);
		//initialise memory, do all set calls and sort oout program
		
		//setting up rest of links
		this.controlUnit.setALU(this.ALU);
		this.controlUnit.setLSUnit(this.LSUnit);
		this.controlUnit.setProgramCounter(this.programCounter);
		this.controlUnit.setInstructionRegister(this.instructionRegister);
		this.LSUnit.setInstructionRegister(this.instructionRegister);
		this.programCounter.setInstructionRegister(this.instructionRegister);
		
		
	}
	
}
