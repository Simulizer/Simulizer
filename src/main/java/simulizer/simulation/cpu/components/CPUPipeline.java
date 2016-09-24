package simulizer.simulation.cpu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.Statement;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.*;
import simulizer.simulation.instructions.*;
import simulizer.simulation.messages.*;
import simulizer.simulation.messages.PipelineHazardMessage.Hazard;

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
	private int isFinished;//used for testing end of program
	private int nopCount;//used to check for pipeline hazards when sending messages
	private boolean rawOccured;//used to check if a raw hazard has just occured
	
	/**constructor calls the super constructor
	 * as well as initialising the new pipeline related fields
	 * @param io the io class being used for syscall IO
	 */
	public CPUPipeline(IO io) {
		super(io);
		this.IF = createNopStatement();
		this.ID = createNopInstruction();
		this.canFetch = true;
		this.isFinished = 0;
		this.nopCount = 2;//initially 2
		this.rawOccured = false;//initially false
		
	}

	/**override the setCycleFreq method in CPU
	 * 
	 */
	@Override
	public void setCycleFreq(double freq) {
		// pipelined: 1 cycle = 1 tick
		clock.setTickFrequency(freq);
		sendMessage(new SimulationMessage(SimulationMessage.Detail.SPEED_CHANGED));
	}

	/**override the getCycleFreq method in CPU
	 * 
	 */
	@Override
	public double getCycleFreq() {
		// pipelined: 1 cycle = 1 tick
		return clock.getTickFrequency();
	}

	/**method will go through a statement and extract the registers
	 * that will be read by this instruction
	 * @param statement the statement to be decoded and then executed
	 * @return the list of registers to be read
	 */
	private List<Register> registersRead(Statement statement) {
		ArrayList<Register> registers = new ArrayList<>();
		
		//now to get all registers read out from the statement
		OperandFormat opForm = statement.getInstruction().getOperandFormat();
		boolean readZero = opForm.equals(OperandFormat.register) || opForm.equals(OperandFormat.srcAddr) ||
						   opForm.equals(OperandFormat.cmpCmpLabel) || opForm.equals(OperandFormat.cmpLabel);
		
		boolean readOne = opForm.equals(OperandFormat.destSrc) || opForm.equals(OperandFormat.destSrcImm) ||
						  opForm.equals(OperandFormat.destSrcSrc) || opForm.equals(OperandFormat.cmpCmpLabel);
		
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
		ArrayList<Register> registers = new ArrayList<>();
		switch(instruction.mode) {
			case RTYPE://all rtype instructions have a destination register
				registers.add(instruction.asRType().getDestReg());
				break;
			case JTYPE://jal will write to the return address register
				if(instruction.getInstruction().equals(Instruction.jal)
						|| instruction.getInstruction().equals(Instruction.jalr))
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
			case SPECIAL:
				if(instruction.getInstruction().equals(Instruction.syscall)) {
					long syscallCode = DataConverter.decodeAsSigned(getRegister(Register.v0).getBytes());
					if(syscallCode == 5||syscallCode==8||syscallCode==9||syscallCode==12) {//these syscall codes write to v0
						registers.add(Register.v0);
					}
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
		for (A write : writes) {
			for (A read : reads) {
				if (write.equals(read)) {
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
		return new Statement(Instruction.nop,new ArrayList<>(),-1);
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
	@Override
	protected void runSingleCycle() throws MemoryException, DecodeException, InstructionException,
			ExecuteException, HeapException, StackException, EndedException {

		Address fetchAddress   = programCounter;
		Address decodeAddress  = new Address(programCounter.getValue()-4);
		Address executeAddress = new Address(programCounter.getValue()-8);

		// only hit the breakpoint once, then allow progress to continue
		if(Breakpoints.isBreakpoint(fetchAddress)) {
			pause();
		}

		if(canFetch && isFinished==0) {
			fetch();
		} else if (!canFetch) {
			canFetch = true;
		} else if(isFinished == 1 || isFinished == 2) {//getting closer to termination
			isFinished++;
		} else if(isFinished == 3 && isRunning) { //ending termination
			//exiting cleanly but representing that in reality an error would be thrown
			sendMessage(new ProblemMessage(
					new MemoryException("" +
							"Program tried to execute a program outside the text segment.\n" +
                            "  This could be because you forgot to exit cleanly.\n" +
                            "  To exit cleanly please call syscall with code 10.\n", fetchAddress)));
			stopRunning();
			return;
		}
		
		if(fetchAddress.getValue() == lastAddress.getValue()+4 && isFinished == 0) {//if end of program reached
			isFinished = 1;//stop fetching essentially and begin to terminate program
        }
		
		boolean needToBubbleRAWReg = needToBubble(registersRead(IF), registersBeingWritten(ID));//detecting pipeline hazards
		
		InstructionFormat oldIDToExecute = ID;//storing old value of ID before overwritten, this is what I should be executing
		if (needToBubbleRAWReg) { //if we need to stall to prevent incorrect reads
			sendMessage(new PipelineHazardMessage(Hazard.RAW));
			Statement nopBubble = createNopStatement();
			ID = decode(nopBubble.getInstruction(),nopBubble.getOperandList());
			this.canFetch = false;
		} else {
			ID = decode(IF.getInstruction(), IF.getOperandList());
			IF = instructionRegister;//updating IF
		}
		
		execute(oldIDToExecute);
	    
		//jumped checks if either an unconditional jump is made or, a branch returning true
		boolean jumped = oldIDToExecute.mode.equals(AddressMode.JTYPE) ||
				(oldIDToExecute.mode.equals(AddressMode.ITYPE) && ALU.branchFlag);
		
		if(jumped) {//flush pipeline and allow continuation of running
			sendMessage(new PipelineHazardMessage(Hazard.CONTROL));
			this.isFinished = 0;//considering edge case where jump on last instruction
			this.isRunning = true;//keep the program running
			IF = createNopStatement();
			ID = createNopInstruction();
		}

		if(annotations.containsKey(executeAddress) && nopCount==0) {//checking for annotations (not when a fake nop is executed)
			sendMessage(new AnnotationMessage(annotations.get(executeAddress), executeAddress));
		}
		
		//Dealing with pipeline state messages
		if(needToBubbleRAWReg) {//got bubbling slightly wrong with RAW, need to add this to fix
			decodeAddress = null;
			this.rawOccured = true;//need to treat raws differently to jump flushes
			fetchAddress = new Address(fetchAddress.getValue()-4);
		}
		
		if(nopCount == 2) {
			decodeAddress = null;
		} 
		if(nopCount >= 1) {
			executeAddress = null;
			if(rawOccured) {//need to do some additional stuff if a raw has previously occurred
				fetchAddress = new Address(fetchAddress.getValue()-4);
				decodeAddress = new Address(fetchAddress.getValue()-4);
				rawOccured = false;
			}
		}
		sendMessage(new PipelineStateMessage(fetchAddress, decodeAddress, executeAddress));

		// decrement until 0 but no further
		nopCount = (nopCount <= 0) ? 0 : nopCount-1;
		
		if(needToBubbleRAWReg) {//if raw hazard has happened in this cycle
			nopCount = 1;
		} else if(jumped) {//if jump occurred in this cycle
			nopCount = 2;
		}

		waitForNextTick();

		cycles++;
		if(breakAfterCycle) {
			pause();
		}
	}
	
	/**overwriting the run program method of CPU but adding some field changes before execution
	 * 
	 */
	@Override
	public void runProgram()
	{
		this.canFetch = true;//resetting fields for new program
		this.isFinished = 0;
		this.nopCount = 2;//decode and execute bubbled initially
		this.rawOccured = false;
		this.IF = createNopStatement();
		this.ID = createNopInstruction();
		super.runProgram();//calling original run program
	}

	/**overwriting instruction for pipeline due to problem with jal instruction getting incorrect program counter value
	 *
	 */
	@Override
	protected void execute(InstructionFormat instruction) throws InstructionException, ExecuteException, MemoryException, HeapException, StackException {
		if(instruction.getInstruction().equals(Instruction.jal)
				|| instruction.getInstruction().equals(Instruction.jalr)) {//jal by default will take incorrect PC value, this needs to be dealt with
			// regardless of whether a register or address is passed: will decode into a JType instruction
            long newCurrentAddress = DataConverter.decodeAsUnsigned(instruction.asJType().getCurrentAddress().get().getBytes()) - 4;
            Optional<Word> trueCurrent = Optional.of(new Word(DataConverter.encodeAsUnsigned(newCurrentAddress)));
            instruction = new JTypeInstruction(instruction.getInstruction(),instruction.asJType().getJumpAddress(),trueCurrent);
		}
		
		super.execute(instruction);
	}

	
	/**override isPipelined in CPU
	 * 
	 */
	@Override
	public boolean isPipelined() {
		return true;
	}
}
