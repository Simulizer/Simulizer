package simulizer.simulation.cpu.components;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Label;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.Statement;
import simulizer.assembler.representation.operand.AddressOperand;
import simulizer.assembler.representation.operand.IntegerOperand;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.assembler.representation.operand.OperandFormat.OperandType;
import simulizer.assembler.representation.operand.RegisterOperand;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.ProgramException;
import simulizer.simulation.instructions.ITypeInstruction;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.RTypeInstruction;
import simulizer.simulation.instructions.SpecialInstruction;

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
		
		try {
			this.programCounter = getEntryPoint();//set the program counter to the entry point to the program
		} catch(Exception e) {//if entry point load fails
			//SEND TO LOGGER HERE
		}
		
		this.registers[Register.gp.getID()] = new Word(new byte[]{0x10,0x00,(byte)0x80,0x00});//setting global pointer
		
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
	 * @throws ProgramException thrown if no main label is found
	 */
	private Address getEntryPoint() throws ProgramException
	{
		Map<Label,Address> labels = this.program.labels;//map of labels
		
		for(Map.Entry<Label, Address> entry : labels.entrySet())//iterating through map
		{
			if(entry.getKey().getName().toLowerCase().equals("main"))//if main label found
			{
				return entry.getValue();
			}
		}
		
		throw new ProgramException("No main label found.", this.program);
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
	
	/**this method carries out the decode of the FDE cycle, it will
	 * look through the statement object and take the instruction and decode the operands
	 * @return InstructionFormat the instruction ready for execution
	 * @throws DecodeException if something goes wrong during decode
	 */
	private InstructionFormat decode() throws DecodeException
	{
		Instruction instruction = this.instructionRegister.i;
		List<Operand> operandList = this.instructionRegister.operandList;//shouldn't ever be more than 3 stored
		
		OperandFormat opForm = new OperandFormat();//used for checking operand validity
		OperandType op1 = operandList.get(0).getOperandFormatType();
		OperandType op2 = operandList.get(1).getOperandFormatType();
		OperandType op3 = operandList.get(2).getOperandFormatType();
		
		if(operandList.size() > 3)
		{
			throw new DecodeException("Too many operands.",operandList.get(0));
		}
		
		if(!opForm.valid(op1,op2,op3))
		{
			throw new DecodeException("Not valid set of operands.", operandList.get(0));//if invalid operands given
		}
		
		
		//separating into different instruction types now
		if(instruction.getOperandFormat() == OperandFormat.destSrcSrc)//will be r type instruction: 2 src, 1 dest
		{
			Optional<Word> dest = Optional.empty();//destination always empty to start
			Register destinationRegister = operandList.get(0).asRegisterOp().r;//store destination register
			Optional<Word> src1 = Optional.of(this.decodeRegister(operandList.get(1).asRegisterOp()));
			Optional<Word> src2 = Optional.of(this.decodeRegister(operandList.get(2).asRegisterOp()));
			return new RTypeInstruction(instruction, dest, destinationRegister, src1, src2);
		}
		else if(instruction.getOperandFormat() == OperandFormat.destSrcImm)//immediate arithmetic operations
		{
			Optional<Word> dest = Optional.empty();
			Register destinationRegister = operandList.get(0).asRegisterOp().r;
			Optional<Word> srcRegister = Optional.of(this.decodeRegister(operandList.get(1).asRegisterOp()));
			Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(operandList.get(2).asIntegerOp()));
			return new RTypeInstruction(instruction, dest, destinationRegister, srcRegister, immValue);
		}
		else if(instruction.getOperandFormat() == OperandFormat.destSrcImmU)//immediate unsigned arithmetic ops (same as destSrcImm code wise)
		{
			Optional<Word> dest = Optional.empty();
			Register destinationRegister = operandList.get(0).asRegisterOp().r;
			Optional<Word> srcRegister = Optional.of(this.decodeRegister(operandList.get(1).asRegisterOp()));
			Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(operandList.get(2).asIntegerOp()));
			return new RTypeInstruction(instruction, dest, destinationRegister, srcRegister, immValue);
		}
		else if(instruction.getOperandFormat() == OperandFormat.destSrc)//single register ops like neg or abs (or move)
		{
			Optional<Word> dest = Optional.empty();
			Register destinationRegister = operandList.get(0).asRegisterOp().r;
			Optional<Word> srcRegister = Optional.of(this.decodeRegister(operandList.get(1).asRegisterOp()));
			Optional<Word> fakeSecondRegister = Optional.empty(); //dummy word to pass into the alu (dealt with in the alu easily)
			return new RTypeInstruction(instruction, dest, destinationRegister, srcRegister, fakeSecondRegister);
		}
		else if(instruction.getOperandFormat() == OperandFormat.destImm)//instructions such as li
		{
			Register destinationRegister = operandList.get(0).asRegisterOp().r;
			Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(operandList.get(1).asIntegerOp()));
			return null;//FILL IN 
		}
		else if(instruction.getOperandFormat() == OperandFormat.noArguments)//syscall, nop, break (break has an op 
		{
			return new SpecialInstruction(instruction);
		}
		else if(instruction.getOperandFormat() == OperandFormat.label)//branch, jal, j
		{
			Optional<Address> goToAddress = Optional.of(this.decodeAddressOperand(operandList.get(0).asAddressOp()));//where to jump
			Optional<Address> currentAddress = Optional.empty();//storing current address if needed by jal
			
			Integer currentLine  = this.instructionRegister.lineNumber;//line number of current instruction
			for(Map.Entry<Address,Integer> entry : this.program.lineNumbers.entrySet())//iterating to find current address
			{
				if(entry.getValue() == currentLine)//if address found
				{
					currentAddress = Optional.of(entry.getKey());//current address for jal
				}
			}
			return null;//FILL IN//put in object here J
		}
		else if(instruction.getOperandFormat() == OperandFormat.register)//for jr
		{
			Word registerContents = this.decodeRegister(operandList.get(0).asRegisterOp());//getting register contents
			Optional<Address> registerAddress = Optional.of(new Address((int)loadAsUnsigned(registerContents.getWord())));//put into correct format
			return null;//FILL IN//put in object here J
		}
		else if(instruction.getOperandFormat() == OperandFormat.cmpCmpLabel)//for branch equal etc.
		{
			Optional<Word> cmp1 = Optional.of(this.decodeRegister(operandList.get(0).asRegisterOp()));//first comparison value
			Optional<Word> cmp2 = Optional.of(this.decodeRegister(operandList.get(1).asRegisterOp()));//second comparison value
			Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(operandList.get(2).asAddressOp()));//where to branch to if comparison returns true
			return new ITypeInstruction(instruction,cmp1,cmp2,branchAddr);
		}
		else if(instruction.getOperandFormat() == OperandFormat.cmpLabel)//for bltz etc
		{
			Optional<Word> cmp = Optional.of(this.decodeRegister(operandList.get(0).asRegisterOp()));//value to compare
			Optional<Word> fakeCmp = Optional.empty();//used to make ALU calculations easier
			Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(operandList.get(1).asAddressOp()));//branch address
			return new ITypeInstruction(instruction,cmp,fakeCmp,branchAddr);
		}
		else if(instruction.getOperandFormat() == OperandFormat.srcAddr)//for store instructions
		{
			Optional<Word> src = Optional.of(this.decodeRegister(operandList.get(0).asRegisterOp()));//word to store
			Optional<Address> toStore = Optional.of(this.decodeAddressOperand(operandList.get(1).asAddressOp()));
			return null;//FILL IN//put in object here ?
		}
		else if(instruction.getOperandFormat() == OperandFormat.destAddr)//for load stuff
		{
			Optional<Word> dest = Optional.empty();//where to store retrieved data
			Register loadInto = operandList.get(0).asRegisterOp().r;//register to store
			Optional<Address> toRetrieve = Optional.of(this.decodeAddressOperand(operandList.get(1).asAddressOp()));
			return null;//FILL IN//fill in ?
		}
		else//invalid instruction format or BREAK
		{
			if(instruction.equals(Instruction.BREAK))
			{
				//do some break stuff here
			}
			throw new DecodeException("Invalid instruction format.", operandList.get(0));
		}
		
	}
	
	/**this method will execute the instruction given to it
	 * 
	 * @param instruction instruction set up with all necessary data
	 * @throws InstructionException if problem during execution
	 * @throws ExecuteException if problem during execution
	 */
	private void execute(InstructionFormat instruction) throws InstructionException, ExecuteException
	{
		switch(instruction.mode)//switch based on instruction format
		{
			case RTYPE:
				Word result = this.ALU.execute(instruction.getInstruction(), instruction.asRType().getSrc1(), instruction.asRType().getSrc2());
				this.registers[instruction.asRType().getDestReg().getID()] = result;//storing result
				this.programCounter = new Address(this.programCounter.getValue() + 4);//incrementing PC
				break;
			case ITYPE:
				Word branchTest = this.ALU.execute(instruction.getInstruction(), instruction.asIType().getCmp1(), instruction.asIType().getCmp2());//carrying out comparison
				if(equalByteArrays(branchTest.getWord(),ALU.branchTrue))
				{
					this.programCounter = instruction.asIType().getBranchAddress().get();//set the program counter
					//do i still need to do +4?
				}
				else
				{
					this.programCounter = new Address(this.programCounter.getValue() + 4);//increment as normal
				}
				break;
			case SPECIAL:
				if(instruction.getInstruction().equals(Instruction.syscall))//syscall
				{
					//do whatever needs to be done
				}
				else if(instruction.getInstruction().equals(Instruction.nop))//no operation
				{
					//just do nothing
				}
				else
				{
					throw new ExecuteException("Error with zero argument instruction", instruction);
				}
				break;
			default:
				throw new ExecuteException("Error during Execution", instruction);
		}
	}
	
	/**this method will run a single cycle of the FDE cycle
	 * @throws MemoryException if problem accessing memory
	 * @throws DecodeExceptionif error during decode
	 * @throws InstructionException if error with instruction
	 * @throws ExecuteException if problem during execution
	 * 
	 */
	public void runSingleCycle() throws MemoryException, DecodeException, InstructionException, ExecuteException
	{
		fetch();
		InstructionFormat instruction = decode();
		execute(instruction);
	}
	
	/**useful auxillary methods to check if 2 byte arrays equal
	 * 
	 * @param arr1 first array
	 * @param arr2 second array
	 * @return are they equal?
	 */
	private boolean equalByteArrays(byte[] arr1, byte[] arr2)
	{
		for(int i = 0; i < arr1.length; i++)
		{
			if(arr1[i] != arr2[i])
			{
				return false;
			}
		}
		return true;
	}
	
	/**this method will decode an integer operand into a 4 byte word
	 * 
	 * @param operand the operand to decode
	 * @return the decoded word
	 * @throws DecodeException if something goes wrong during decode
	 */
	private Word decodeIntegerOperand(IntegerOperand operand) throws DecodeException
	{
		if(operand.getOperandFormatType().equals(OperandFormat.OperandType.UNSIGNED_IMMEDIATE))//if unsigned
		{
			return new Word(serialiseUnsigned((long)operand.value));
		}
		else if(operand.getOperandFormatType().equals(OperandType.IMMEDIATE))//signed immediate
		{
			return new Word(serialiseSigned((long)operand.value));
		}
		else
		{
			throw new DecodeException("Error decoding integer operand.", operand);
		}
	}
	
	/**this method will, in particular, decode the address operands
	 * 
	 * @param operand the operand to decode
	 * @return the decoded address in memory
	 */
	private Address decodeAddressOperand(AddressOperand operand) throws DecodeException
	{
		if(operand.labelOnly())//if the the address is that to a label in code
		{
			for(Map.Entry<Label,Address> entry : this.program.labels.entrySet())
			{
				if(operand.labelName.get().equals(entry.getKey().getName()))
				{
					return entry.getValue();
				}
			}
			throw new DecodeException("Error decoding an Address Operand", operand);
		}
		else if(operand.offsetOnly())//if offset only
		{
			return new Address(operand.constant.get());
		}
		else//base with(out) an offset
		{
			Address address = new Address((int) loadAsUnsigned(this.registers[operand.register.get().getID()].getWord()));
			if(operand.constant.isPresent())//if offset as well
			{
				Address off = new Address(operand.constant.get());
				address = new Address(address.getValue() + off.getValue());
			}
			return address;
		}
	}
	
	/**this method will decode a register operand
	 * if it is not a destination register then the data will be retrieved, otherwise
	 * a null Word will be returned
	 * @param operand the operand to decode
	 * @return the word of data from the register
	 * @throws DecodeException if something goes wrong during decode
	 */
	private Word decodeRegister(RegisterOperand operand) throws DecodeException
	{
		if(operand.getOperandFormatType().equals(OperandFormat.OperandType.DEST_REGISTER))
		{
			return null;//null word
		}
		else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.SRC_REGISTER))
		{
			return this.registers[operand.r.getID()];//return the word stored at that register
		}
		else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.TARGET_REGISTER))
		{
			return this.registers[operand.r.getID()];//this is probably wrong for now
		}
		else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.REGISTER))//standard register
		{
			return this.registers[operand.r.getID()];//return the word stored at that register
		}
		else
		{
			throw new DecodeException("Error decoding Register.", operand);
		}
	}
	
	/**method takes a byte array and returns it's signed value as a long
	 * 
	 * @param word the word to convert
	 * @return the byte[] converted to a signed long
	 */
	private long loadAsSigned(byte[] word) {
		return 1L;
	}
	
	/**method takes a byte array and returns it's unsigned value as a long
	 * 
	 * @param word the word to convert
	 * @return the byte[] converted to an unsigned long
	 */
	private long loadAsUnsigned(byte[] word) {
		return 1L;
	}
	
	
	/**this method takes a signed long and converts it to a byte array
	 * 
	 * @param value the long to convert
	 * @return the value as a byte array
	 */
	private byte[] serialiseSigned(long value) {
		return new byte[4];
	}
	
	/**this method takes an unsigned long and converts it to a byte array
	 * 
	 * @param value the long to convert
	 * @return the value as a byte array
	 */
	private byte[] serialiseUnsigned(long value) {
		return new byte[4];
	}
	
}
