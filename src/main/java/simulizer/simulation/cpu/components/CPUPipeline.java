package simulizer.simulation.cpu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import simulizer.assembler.representation.*;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;
import simulizer.simulation.instructions.AddressMode;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.JTypeInstruction;
import simulizer.simulation.instructions.SpecialInstruction;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.PipelineHazardMessage;
import simulizer.simulation.listeners.PipelineHazardMessage.Hazard;
import simulizer.simulation.listeners.ProblemMessage;

/**this class is an extension of the original CPU class
 * the difference is that the order of execution follows a very 
 * primitive pipeline. The features of this pipeline are as follows:
 * if in the decode, we find we need to read a register being written to in execute
 * then we will stall the pipeline for one cycle
 * if a successful branch is executed, the pipeline will be flushed
 * @author Charlie Street
 */
public class CPUPipeline extends CPU {

	private Statement IF;//used for storing between fetch and decode
	private InstructionFormat ID;//user for storing between decode and execute
	private boolean canFetch;//useful for pipeline stalling
	private boolean isFinished;//used for testing end of program
	
	/**constructor calls the super constructor
	 * as well as initialising the new pipeline related fields
	 * @param io the io class being used for syscall IO
	 */
	public CPUPipeline(IO io) {
		super(io);
		this.IF = createNopStatement();
		this.ID = createNopInstruction();
		this.canFetch = true;
		this.isFinished = false;
		
	}
	
	/**overwrites the normal cpu clock speed to make it representative of pipeline speed
	 * 
	 */
	public void setClockSpeed(int millis) {
		clock.tickMillis = millis;
	}
	
	/**method will go through a statement and extract the registers
	 * that will be read by this instruction
	 * @param statement the statement to be decoded and then executed
	 * @return the list of registers to be read
	 */
	private List<Register> registersRead(Statement statement) {
		ArrayList<Register> registers = new ArrayList<Register>();
		
		//now to get all registers read out from the statement
		OperandFormat opForm = statement.getInstruction().getOperandFormat();
		boolean readZero = opForm.equals(OperandFormat.register) || opForm.equals(OperandFormat.srcAddr) ||
						   opForm.equals(OperandFormat.cmpCmpLabel) || opForm.equals(OperandFormat.cmpLabel);
		
		boolean readOne = opForm.equals(OperandFormat.destSrc) || opForm.equals(OperandFormat.destSrcImm) ||
						  opForm.equals(OperandFormat.destSrcImmU) || opForm.equals(OperandFormat.destSrcSrc) ||
						  opForm.equals(OperandFormat.cmpCmpLabel);
		
		boolean readTwo = opForm.equals(OperandFormat.destSrcSrc);
		
		boolean readRegAddress = opForm.equals(OperandFormat.destAddr) || opForm.equals(OperandFormat.srcAddr);
		
		
		if(readZero) {//formats with a read in first operand
			registers.add(statement.getOperandList().get(0).asRegisterOp().value);
		}
		if(readOne) {//formats with a read in second operand
			registers.add(statement.getOperandList().get(1).asRegisterOp().value);
		}
		if(readTwo) {//formats with a read in third operand
			registers.add(statement.getOperandList().get(2).asRegisterOp().value);
		}
		
		if(readRegAddress) {//formats with an address represented as a base and offset
			if(!statement.getOperandList().get(1).asAddressOp().labelOnly()) {//if base and offset 
				registers.add(statement.getOperandList().get(1).asAddressOp().register.get());
			}
		}
		return registers;
	}
	
	
	/**method will look at the instruction being executed and retrieving the names
	 * of all instructions being written
	 * for most of our current instructions, only a single register will be returned
	 * however there may be a case with complicated instructions, multiple could be written
	 * and so the return type is as it is
	 * @param instruction the instruction being checked
	 * @return the registers being written to due to this instruction
	 */
	private List<Register> registersBeingWritten(InstructionFormat instruction) {
		ArrayList<Register> registers = new ArrayList<Register>();
		switch(instruction.mode) {
			case RTYPE://all rtype instructions have a destination register
				registers.add(instruction.asRType().getDestReg());
				break;
			case JTYPE://jal will write to the return address register
				if(instruction.getInstruction().equals(Instruction.jal))
				{
					registers.add(Register.ra);
				}
				break;
			case LSTYPE://load instructions write to registers
				Optional<Register> dest = instruction.asLSType().getRegisterName();
				if(dest.isPresent()) {
					registers.add(dest.get());
				}
				break;
			default:
				break;
		}
		
		return registers;
	}
	
	
	/**method will take the registers being read and the registers being written
	 * and determine whether or not any stalling needs to be done in the pipeline
	 * @param <A> the type of list being read, need multiple times so generics is appropriate
	 * @param reads the registers being read
	 * @param writes the registers being written
	 * @return whether or not there is a crossover between the two lists
	 */
	private <A> boolean needToBubble(List<A> reads, List<A> writes) {
		for(int i = 0; i < writes.size(); i++) {
			for(int j = 0; j < reads.size(); j++) {
				if(writes.get(i).equals(reads.get(j))) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**creates a dummy nop statement for the pipeline bubbling
	 * 
	 * @return the dummy nop statement
	 */
	private Statement createNopStatement() {
		return new Statement(Instruction.nop,new ArrayList<Operand>(),-1);
	}
	
	/**method will create a dummy nop instruction for the ID register
	 * when using bubbling
	 * @return the dummy nop instruction
	 */
	private SpecialInstruction createNopInstruction() {
		return new SpecialInstruction(Instruction.nop);
	}
	
	/**method will overwrite the method in the CPU class for running a cycle
	 * this method will mimic a primitive pipeline instead of a sequential execution
	 */
	public void runSingleCycle() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException {

		Address thisInstruction = programCounter;

		if(this.canFetch&&!this.isFinished){
			fetch();
			sendMessage(new ExecuteStatementMessage(thisInstruction));
		} else if (!this.canFetch) {
			this.canFetch = true;
		} else if(this.isFinished) {//ending termination
			//exiting cleanly but representing that in reality an error would be thrown
			this.isRunning = false;
			sendMessage(new ProblemMessage("Program tried to execute a program outside the text segment. "
					+ "This could be because you forgot to exit cleanly."
					+ " To exit cleanly please call syscall with code 10."));
			
		}
		
		if(this.programCounter.getValue() == this.lastAddress.getValue()+4) {//if end of program reached
			this.isFinished = true;//stop fetching essentially and begin to terminate program
        }
		
		boolean needToBubbleRAWReg = needToBubble(registersRead(IF),registersBeingWritten(ID));//detecting pipeline hazards
		
		if (needToBubbleRAWReg) { //if we need to stall to prevent incorrect reads
			
			sendMessage(new PipelineHazardMessage(Hazard.RAW));
		
			Statement nopBubble = createNopStatement();
			ID = decode(nopBubble.getInstruction(),nopBubble.getOperandList());
			this.canFetch = false;
		} else {
			ID = decode(IF.getInstruction(), IF.getOperandList());
			IF = instructionRegister;//updating IF
		}
		
		execute(ID);
		
		//jumped checks if either an unconditional jump is made or, a branch returning true
		boolean jumped = ID.mode.equals(AddressMode.JTYPE) || (ID.mode.equals(AddressMode.ITYPE) && ALU.branchFlag);
		
		if(jumped)//flush pipeline and allow continuation of running
		{
			sendMessage(new PipelineHazardMessage(Hazard.CONTROL));
			this.isFinished = false;//considering edge case where jump on last instruction
			this.isRunning = true;//keep the program running
			IF = createNopStatement();
			ID = createNopInstruction();
		}
		
		if(annotations.containsKey(new Address(thisInstruction.getValue()-8))) {//checking for annotations
			sendMessage(new AnnotationMessage(annotations.get(new Address(thisInstruction.getValue()-8))));//has to be -8 to counter pipeline 
		}
	}
	
	/**overwriting the run program method of CPU but adding some field changes before execution
	 * 
	 */
	public void runProgram()
	{
		this.canFetch = true;//resetting fields for new program
		this.isFinished = false;
		this.isRunning = true;//allow the program to start
		this.IF = createNopStatement();
		this.ID = createNopInstruction();
		super.runProgram();//calling original run program
	}

	/**overwriting instruction for pipeline due to problem with jal instruction getting incorrect program counter value
	 * @throws StackException 
	 * @throws HeapException 
	 * @throws MemoryException 
	 * @throws ExecuteException 
	 * @throws InstructionException 
	 * 
	 */
	protected void execute(InstructionFormat instruction) throws InstructionException, ExecuteException, MemoryException, HeapException, StackException {
		if(instruction.getInstruction().equals(Instruction.jal)) {//jal by default will take incorrect PC value, this needs to be dealt with
			long newCurrentAddress = DataConverter.decodeAsUnsigned(instruction.asJType().getCurrentAddress().get().getWord())-4;
			Optional<Word> trueCurrent = Optional.of(new Word(DataConverter.encodeAsUnsigned(newCurrentAddress)));
			instruction = new JTypeInstruction(Instruction.jal,instruction.asJType().getJumpAddress(),trueCurrent);
		}
		
		super.execute(instruction);
	}
	
}