package simulizer.simulation.cpu.components;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Annotation;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Label;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.Statement;
import simulizer.assembler.representation.operand.Operand;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.exceptions.ExecuteException;
import simulizer.simulation.exceptions.HeapException;
import simulizer.simulation.exceptions.InstructionException;
import simulizer.simulation.exceptions.MemoryException;
import simulizer.simulation.exceptions.ProgramException;
import simulizer.simulation.exceptions.StackException;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.messages.*;
import simulizer.simulation.messages.StageEnterMessage.Stage;

/**
 * this is the central CPU class
 * this is how the following components fit into this class
 * - Control Unit - implicit in this class
 * - Program Counter - Address Object
 * - Instruction Register - Statement object
 * - ALU - External object
 * - L/S Unit - Not required (will still be shown in visualisation)
 * - Registers - array of 4 byte words
 * - Main Memory - External Memory Object
 * 
 * @author Charlie Street
 *
 */
public class CPU {

	private MessageManager messageManager;

	protected Address programCounter;
	protected Statement instructionRegister;

	private ALU Alu;
	protected final Clock clock;
	protected long cycles;
	/**
	 * used for resume for single cycle
	 */
	protected boolean breakAfterCycle;

	private Word[] registers;
	private MainMemory memory;

	private Decoder decoder;
	private Executor executor;

	private Program program;// all information on how to run the program
	public Map<String, Address> labels;
	private Map<String, Label> labelMetaData;

	protected Map<Address, Annotation> annotations;

	protected boolean isRunning;// for program status
	protected Address lastAddress;// used to determine end of program

	private IO io;

	/**
	 * the constructor will set all the components up
	 *
	 * @param io
	 *            the io type being used
	 */
	public CPU(IO io) {
		this.messageManager = new MessageManager(io);
		this.clearRegisters();
		this.clock = new Clock();
		this.cycles = 0;
		this.breakAfterCycle = false;
		this.isRunning = false;
		this.io = io;
		this.decoder = new Decoder(this);
		this.executor = new Executor(this);
	}

	public void shutdown() {
		stopRunning();
		clock.shutdown();
		messageManager.shutdown();
	}

	public Clock getClock() {
		return clock;
	}

	/**
	 * set the number of cycles (fetch + decode + execute) to be run per second.
     * This method provides the same observable results regardless of whether
     * the CPU is pipelined or not
	 *
	 * @param freq the number of cycles per second
	 */
	public void setCycleFreq(double freq) {
		// non-pipelined: 1 cycle = 3 ticks
		// => same speed requires ticks to be 3 times faster
		clock.setTickFrequency(freq * 3);
	}

	public double getCycleFreq() {
		// non-pipelined: 1 cycle = 3 ticks
		// => same speed requires ticks to be 3 times faster
		return clock.getTickFrequency() / 3;
	}


	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * stop the running of a program
	 *
	 */
	public void stopRunning() {
		// harmless to call multiple times, only want to send one message
		if (isRunning) {
			isRunning = false;
			io.cancelRead();
			clock.stop();
			// make sure the simulation stopped message is the very last message
			messageManager.waitForAllRunningTasks();
			sendMessage(new SimulationMessage(SimulationMessage.Detail.SIMULATION_STOPPED));
		}
	}

	public void pause() {
		// isRunning remains true
		clock.stop();
	}

	public void resume() {
		if(isRunning) {
			breakAfterCycle = false;
			clock.start();
		} else {
			runProgram();
		}
	}

	public void resumeForOneCycle() {
		breakAfterCycle = true;
		if(!clock.isRunning()) {
			clock.start();
		}
	}

	protected void waitForNextTick() {
		try {
			if (isRunning) {
				// if the clock is stopped then it advances by 1 tick to unlock this thread
				clock.waitForNextTick();
			}
		} catch (InterruptedException e) {
			sendMessage(new SimulationMessage(SimulationMessage.Detail.SIMULATION_INTERRUPTED));
		}
	}

	/**
	 * Register a listener to receive messages
	 * wrapper for method in listener, makes more sense as wrapper for connecting to CPU
	 * 
	 * @param l
	 *            the listener to send messages to
	 */
	public void registerListener(SimulationListener l) {
		this.messageManager.registerListener(l);
	}

	/**
	 * Unregisters a listener from the list
	 * wrapper for method in listener, things listen to the CPU
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void unregisterListener(SimulationListener l) {
		this.messageManager.unregisterListener(l);
	}

	/**
	 * send a message to all of the registered listeners
	 * wrapper for method in listener, things listen and are sent from the CPU
	 * 
	 * @param m
	 *            the message to send
	 */
	public void sendMessage(Message m) {
		this.messageManager.sendMessage(m);
	}

	/**
	 * this method is used to set up the cpu whenever a new program is loaded into it
	 *
	 * @param program
	 *            the program received from the assembler
	 */
	public void loadProgram(Program program) {
		this.program = program;
		this.instructionRegister = null;// nothing to put in yet so null

		this.clearRegisters();// reset the registers

		// setting up memory
		Address textSegmentStart = this.program.textSegmentStart;
		Address dataSegmentStart = this.program.dataSegmentStart;
		Address dynamicSegmentStart = this.program.dynamicSegmentStart;
		Address stackPointer = new Address((int) DataConverter.decodeAsSigned(this.program.initialSP.getWord()));
		byte[] staticDataSegment = this.program.dataSegment;
		Map<Address, Statement> textSegment = this.program.textSegment;
		this.memory = new MainMemory(textSegment, staticDataSegment, textSegmentStart, dataSegmentStart, dynamicSegmentStart, stackPointer);

		labels = new HashMap<>();
		labelMetaData = new HashMap<>();
		for (Map.Entry<Label, Address> l : program.labels.entrySet()) {
			String key = l.getKey().getName();
			labels.put(key, l.getValue());
			labelMetaData.put(key, l.getKey());
		}

		annotations = program.annotations;

		try {
			this.programCounter = getEntryPoint();// set the program counter to the entry point to the program
		} catch (Exception e) {// if entry point load fails
			sendMessage(new ProblemMessage(e.getMessage()));// send problem to logger
		}

		this.registers[Register.gp.getID()] = this.program.initialGP;// setting global pointer
		sendMessage(new RegisterChangedMessage(Register.gp));
		this.registers[Register.sp.getID()] = this.program.initialSP;// setting up stack pointer
		sendMessage(new RegisterChangedMessage(Register.gp));

		this.Alu = new ALU();// initialising Alu
		this.lastAddress = program.textSegmentLast;

		sendMessage(new SimulationMessage(SimulationMessage.Detail.PROGRAM_LOADED));
	}

	/**
	 * this method resets the registers in the memory
	 * it then initialises them to some default value
	 */
	private void clearRegisters() {
		this.registers = new Word[32];
		for (int i = 0; i < this.registers.length; i++) {
			this.registers[i] = new Word(DataConverter.encodeAsUnsigned(0));
			sendMessage(new RegisterChangedMessage(Register.fromID(i)));// firing to visualisation
		}
	}

	/**
	 * this method will look for the main label and get it's corresponding address
	 * this is for use with the program counter
	 * 
	 * @throws ProgramException
	 *             thrown if no main label is found
	 */
	private Address getEntryPoint() throws ProgramException {
		Map<Label, Address> labels = this.program.labels;// map of labels

		for (Map.Entry<Label, Address> entry : labels.entrySet()) {// iterating through map
			if (entry.getKey().getName().toLowerCase().equals("main")) {// if main label found
				return entry.getValue();
			}
		}

		throw new ProgramException("No main label found.", this.program);
	}

	/**
	 * carries out the fetch part of the FDE cycle (non pipelined)
	 *
	 */
	protected void fetch() throws MemoryException {
		sendMessage(new StageEnterMessage(Stage.Fetch));// signal start of stage
		this.instructionRegister = this.memory.readFromTextSegment(this.programCounter);
		sendMessage(new DataMovementMessage(Optional.empty(), Optional.of(this.instructionRegister)));
		this.programCounter = new Address(this.programCounter.getValue() + 4);// incrementing the program counter
	}

	/**
	 * method decodes within the cpu
	 * wrapper from decoder class but necessary for a nice inheritance structure
	 * 
	 * @param instruction
	 *            the instruction format to decode
	 * @param operandList
	 *            the list of operands to be decoded
	 * @return InstructionFormat the instruction ready for execution
	 * @throws DecodeException
	 *             if something goes wrong during decode
	 */
	protected InstructionFormat decode(Instruction instruction, List<Operand> operandList) throws DecodeException {
		return this.decoder.decode(instruction, operandList);
	}

	/**
	 * this method will execute the instruction given to it
	 * wrapper for method in Executor, gives nice inheritance layout
	 * 
	 * @param instruction
	 *            instruction set up with all necessary data
	 * @throws Exception
	 *             thrown from execute in executor, refer to that method for more precise infro
	 */
	protected void execute(InstructionFormat instruction) throws InstructionException, ExecuteException, MemoryException, HeapException, StackException {
		this.programCounter = this.executor.execute(instruction, this.getProgramCounter());// will set the program counter if changed
	}


	/**
	 * this method will run a single cycle of the FDE cycle
	 * 
	 * @throws MemoryException
	 *             if problem accessing memory
	 * @throws DecodeException
	 *             if error during decode
	 * @throws InstructionException
	 *             if error with instruction
	 * @throws ExecuteException
	 *             if problem during execution
	 * @throws HeapException
	 *             if the heap goes wrong at some point
	 * @throws StackException
	 *
	 */
	protected void runSingleCycle() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException {

		// PC holds next instruction and is advanced by fetch,
		// messages should be sent about this instruction instead
		Address thisInstruction = programCounter;

		messageManager.waitForCrucialTasks();

		fetch();
		sendMessage(new PipelineStateMessage(thisInstruction, null, null));

		waitForNextTick();

		InstructionFormat instruction = decode(this.instructionRegister.getInstruction(), this.instructionRegister.getOperandList());
		sendMessage(new PipelineStateMessage(null, thisInstruction, null));

		waitForNextTick();

		execute(instruction);
		sendMessage(new PipelineStateMessage(null, null, thisInstruction));

		if (annotations.containsKey(thisInstruction)) {
			sendMessage(new AnnotationMessage(annotations.get(thisInstruction), thisInstruction));
		}

		waitForNextTick();


		if (this.programCounter.getValue() == this.lastAddress.getValue() + 4) {// if end of program reached
			// clean exit but representing in reality an error would be thrown
			stopRunning();
			sendMessage(new ProblemMessage("Program tried to execute a program outside the text segment. " + "This could be because you forgot to exit cleanly." + " To exit cleanly please call syscall with code 10."));
			return;
		}


		cycles++;
		if(breakAfterCycle) {
			pause();
		}
	}

	/**
	 * this method will run the program given to the CPU, it will operate under the clock cycle
	 *
	 */
	public void runProgram() {
		isRunning = true;
		breakAfterCycle = false;
		clock.resetTicks();
		cycles = 0;

		sendMessage(new SimulationMessage(SimulationMessage.Detail.SIMULATION_STARTED));

		// used for setting up the annotation environment eg loading visualisations
		// if clock speed set, then this applies on the first tick since the clock is
		// started below
		if (program.initAnnotation != null) {
			sendMessage(new AnnotationMessage(program.initAnnotation, null));
		}

		clock.start();

		while (isRunning) {
			long cycleStart = System.currentTimeMillis();

			try {
				this.runSingleCycle();// run one loop of Fetch,Decode,Execute
			} catch (MemoryException | DecodeException | InstructionException
					| ExecuteException | HeapException | StackException e) {
				sendMessage(new ProblemMessage(e.getMessage()));
				isRunning = false;
			}

			//TODO: aggregate stats like this into a window
			long cycleDuration = System.currentTimeMillis() - cycleStart;
		}
		stopRunning();
	}

	// Standard get methods, don't do anything special
	public Word[] getRegisters() {
		return registers;
	}

	public MainMemory getMainMemory() {
		return memory;
	}

	public Address getProgramCounter() {
		return programCounter;
	}

	public ALU getALU() {
		return Alu;
	}

	public IO getIO() {
		return io;
	}

	public Program getProgram() {
		return program;
	}

	public boolean isPipelined() {
		return false; // overridden in CPUPipeline
	}
}
