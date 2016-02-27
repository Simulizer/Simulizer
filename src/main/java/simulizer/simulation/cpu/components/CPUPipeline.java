package simulizer.simulation.cpu.components;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import simulizer.assembler.representation.Annotation;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.Statement;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.StackException;
import simulizer.simulation.instructions.AddressMode;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.SpecialInstruction;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.ExecuteStatementMessage;

/**this class is an extension of the original CPU class
 * the difference is that the order of execution follows a very 
 * primitive pipeline. The features of this pipeline are as follows:
 * if in the decode, we find we need to read a register being written to in execute
 * then we will stall the pipeline for one cycle
 * if a successful branch is executed, the pipeline will be flushed
 * @author Charlie Street
 *
 */
public class CPUPipeline extends CPU {

	private final ArrayList<OperandFormat> readInstructions;//list of formats which will read from 
	private Statement IF;//used for storing between fetch and decode
	private InstructionFormat ID;//user for storing between decode and execute
	private boolean canFetch;//useful for pipeline stalling
	
	/**constructor calls the super constructor
	 * as well as initialising the new pipeline related fields
	 * @param io the io class being used for syscall IO
	 */
	public CPUPipeline(IO io) {
		super(io);
		this.IF = createNopStatement();
		this.ID = createNopInstruction();
		this.canFetch = true;
		
		this.readInstructions = new ArrayList<OperandFormat>();//all operand formats that include reading registers
		this.readInstructions.add(OperandFormat.register);
		this.readInstructions.add(OperandFormat.destSrc);
		this.readInstructions.add(OperandFormat.destSrcImm);
		this.readInstructions.add(OperandFormat.destSrcImmU);
		this.readInstructions.add(OperandFormat.destSrcSrc);
		this.readInstructions.add(OperandFormat.srcAddr);
		this.readInstructions.add(OperandFormat.cmpCmpLabel);
		this.readInstructions.add(OperandFormat.cmpLabel);
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
		
		if(readZero) {//formats with a read in first operand
			registers.add(statement.getOperandList().get(0).asRegisterOp().value);
		}
		if(readOne) {//formats with a read in second operand
			registers.add(statement.getOperandList().get(1).asRegisterOp().value);
		}
		if(readTwo) {//formats with a read in third operand
			registers.add(statement.getOperandList().get(2).asRegisterOp().value);
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
			case RTYPE:
				registers.add(instruction.asRType().getDestReg());
				break;
			case LSTYPE:
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
	 * @param reads the registers being read
	 * @param writes the registers being written
	 * @return whether or not there is a crossover between the two lists
	 */
	private boolean needToBubble(List<Register> reads, List<Register> writes) {
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
		if(this.canFetch){
			fetch();
			 sendMessage(new ExecuteStatementMessage(programCounter));
		} else {
			this.canFetch = true;
		}
		
		boolean needToBubble = needToBubble(registersRead(IF),registersBeingWritten(ID));
		if (needToBubble) { //if we need to stall to prevent incorrect reads
			Statement nopBubble = createNopStatement();
			ID = decode(nopBubble.getInstruction(),nopBubble.getOperandList());
			this.canFetch = false;
		} else {
			ID = decode(IF.getInstruction(), IF.getOperandList());
			IF = instructionRegister;//updating IF
		}
		
		int preExecuteAddress = this.programCounter.getValue();
		execute(ID);
		int newPC = this.programCounter.getValue();
		//jumped checks if either an unconditional jump is made or, a branch returning true
		boolean jumped = ID.mode.equals(AddressMode.JTYPE) || (ID.mode.equals(AddressMode.ITYPE) && (newPC - preExecuteAddress > 0));
		if(jumped)//flush pipeline
		{
			IF = createNopStatement();
			ID = createNopInstruction();
		}
		
		if(annotations.containsKey(programCounter)) {
			for(Annotation a : annotations.get(programCounter)) {
				sendMessage(new AnnotationMessage(a));
			}
		}

        if(this.programCounter.getValue() == this.lastAddress.getValue()+4) {//if end of program reached
            this.isRunning = false;//stop running
            IF = createNopStatement();//flushing pipeline for next program to run
            ID = createNopInstruction();
        }
	}

}
