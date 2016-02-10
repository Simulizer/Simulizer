package simulizer.simulation.set_up;

import java.math.BigInteger;
import java.util.Map;

import com.sun.xml.internal.ws.org.objectweb.asm.Label;
import com.sun.xml.internal.ws.wsdl.writer.document.http.Address;

import simulizer.simulation.cpu.components.*;
import simulizer.simulation.data.representation.Word;

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
		this.programData = programData;//all program information
		
		this.mainMemory = new MainMemory(this.programData.textSegment,this.programData.dataSegment,this.programData.textSegmentStart,
				this.programData.dataSegmentStart, this.programData.dynamicSegmentStart);
		
		this.registers = new RegisterBlock();
		this.controlUnit = new ControlUnit(null,null,this.registers,null,null);//setting null to start with to make the linking easier
		this.ALU = new ALU(this.registers,this.controlUnit);
		this.LSUnit = new LSUnit(this.registers,this.mainMemory,null,this.controlUnit);
		this.programCounter = new ProgramCounter(this.getFirstInstruction(),this.controlUnit,null,this.LSUnit);
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
	
	/**method will look through the program object to get the address associated with the maLabelbel
	 * 
	 */
	private Word getFirstInstruction()
	{
		Map<Label,Address> labels = this.programData.labels;
		
		for(Map.Entry<Label, Address> entry : labels.entrySet())//iterate through map
		{
			if(entry.getKey().getName().toLowerCase().equals("main"))//if main found
			{
				BigInteger address = BigInteger.valueOf(entry.getValue().getValue());//put itinto word format
				return new Word(address);
			}
		}
		
		return null;//if main not in (would be a bad program if this was the case)
	}
	
}
