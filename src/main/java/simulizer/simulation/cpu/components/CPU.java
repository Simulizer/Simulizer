package simulizer.simulation.cpu.components;

import java.util.Map;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Label;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.Statement;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.MemoryException;

/**this is the central CPU class
 * this is how the following components fit into this class
 *  - Control Unit - implicit in this class
 *  - Program Counter - Address Object
 *  - Instruction Register - Statement object
 *  - ALU - External object
 *  - L/S Unit - Not required (will still be shown in visualisation)
 *  - Registers - array of 4 byte words
 *  - Main Memory - External Memory Object
 * @author Charlie Street
 *
 */
public class CPU {

	private Address programCounter;
	private Statement instructionRegister;
	private ALU ALU;
	private Word[] registers;
	private MainMemory memory;
	private Program program;//all information on how to run the program
	
	/**the constructor will set all the components up
	 * 
	 * @param program the program information received from the assembler
	 */
	public CPU(Program program)
	{
		this.loadProgram(program);//set up the CPU with the program
	}
	
	/**this method is used to set up the cpu whenever a new program is loaded into it
	 * 
	 * @param program the program received from the assembler
	 */
	public void loadProgram(Program program)
	{
		this.program = program;
		
		this.instructionRegister = null;//nothing to put in yet so null
		
		this.clearRegisters();//reset the registers
		
		//setting up memory
		Address textSegmentStart = this.program.textSegmentStart;
		Address dataSegmentStart = this.program.dataSegmentStart;
		Address dynamicSegmentStart = this.program.dynamicSegmentStart;
		byte[] staticDataSegment = this.program.dataSegment;
		Map<Address,Statement> textSegment = this.program.textSegment;
		this.memory = new MainMemory(textSegment,staticDataSegment,textSegmentStart,dataSegmentStart,dynamicSegmentStart);
		
		this.programCounter = getEntryPoint();//set the program counter to the entry point to the program
		
		//this.registers[Register.gp.getID()] = dataSegmentStart.getValue() + 32000;//setting global pointer
		
		this.ALU = new ALU();//initialising ALU
	}
	
	/**this method resets the registers in the memory
	 * it then initialises them to some default value
	 */
	private void clearRegisters()
	{
		this.registers = new Word[32];
		for(int i = 0; i < this.registers.length; i++)
		{
			byte[] word = new byte[]{0x00,0x00,0x00,0x00};//initially setting to 0
			this.registers[i] = new Word(word);
		}
	}
	
	/**this method will look for the main label and get it's corresponding address
	 * this is for use with the program counter
	 */
	private Address getEntryPoint()
	{
		Map<Label,Address> labels = this.program.labels;//map of labels
		
		for(Map.Entry<Label, Address> entry : labels.entrySet())//iterating through map
		{
			if(entry.getKey().getName().toLowerCase().equals("main"))//if main label found
			{
				return entry.getValue();
			}
		}
		
		return null;//THROW ERROR NO MAIN LABEL!!!!!
	}
	
	/**carries out the fetch part of the FDE cycle (non pipelined)
	 * 
	 */
	private void fetch() throws MemoryException
	{
		Statement nextInstruction = this.memory.readFromTextSegment(this.programCounter);//retrieving from memory
		this.instructionRegister = nextInstruction;//set IR
		this.programCounter = new Address(this.programCounter.getValue() + 4);//incrementing the program counter
	}
	
}
