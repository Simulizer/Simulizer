package simulizer.simulation.cpu.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Annotation;
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
import simulizer.simulation.instructions.ITypeInstruction;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.JTypeInstruction;
import simulizer.simulation.instructions.LSInstruction;
import simulizer.simulation.instructions.RTypeInstruction;
import simulizer.simulation.instructions.SpecialInstruction;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.DataMovementMessage;
import simulizer.simulation.listeners.ExecuteStatementMessage;
import simulizer.simulation.listeners.Message;
import simulizer.simulation.listeners.ProblemMessage;
import simulizer.simulation.listeners.RegisterChangedMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.simulation.listeners.StageEnterMessage;
import simulizer.simulation.listeners.StageEnterMessage.Stage;

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

    private List<SimulationListener> listeners;

    protected Address programCounter;
    protected Statement instructionRegister;

    private ALU Alu;
    private Clock clock;//clock cycle for IE cycle

    private Word[] registers;
    private MainMemory memory;

    private Program program;//all information on how to run the program
    public Map<String, Address> labels;
    private Map<String, Label> labelMetaData;

	protected Map<Address, List<Annotation>> annotations;

    protected boolean isRunning;//for program status
    protected Address lastAddress;//used to determine end of program
    
    private IO io;


    /**the constructor will set all the components up
     *
     * @param io the io type being used
     */
    public CPU(IO io) {
        listeners = new ArrayList<>();
        this.clearRegisters();
        this.clock = new Clock(100);
        this.isRunning = false;
        this.io = io;
    }

    /**this method will set the clock controlling
     * the execution cycle to run
     */
    public void startClock() {
        clock.reset();
        clock.startRunning();
        if(!clock.isAlive()) {
            clock.start();//start the clock thread
        }
    }

    /**this method will set the clock controlling
     * the execution cycle to run
     */
    public void pauseClock() {
        if(clock != null) {
            clock.stopRunning();
        }
    }

    /**sets the speed in ms of the clock
     * 
     * @param tickMillis the time for one clock cycle
     */
    public void setClockSpeed(int tickMillis) {
        clock.tickMillis = tickMillis;
    }

    /**stop the running of a program
     * 
     */
    public void stopRunning() {
        isRunning = false;
        if(clock != null) {
            pauseClock();
            try {
                clock.join(); // stop the clock thread
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Register a listener to receive messages
     * @param l the listener to send messages to
     */
    public void registerListener(SimulationListener l) {
        listeners.add(l);
    }

    /**
     * send a message to all of the registered listeners
     * @param m the message to send
     */
    protected void sendMessage(Message m) {
        for(SimulationListener l : listeners) {
            l.processMessage(m);

			if(m instanceof AnnotationMessage) {
				l.processAnnotationMessage((AnnotationMessage) m);
			} else if(m instanceof DataMovementMessage) {
                l.processDataMovementMessage((DataMovementMessage) m);
            } else if(m instanceof ExecuteStatementMessage) {
                l.processExecuteStatementMessage((ExecuteStatementMessage) m);
            } else if(m instanceof ProblemMessage) {
                l.processProblemMessage((ProblemMessage) m);
            } else if(m instanceof StageEnterMessage) {
                l.processStageEnterMessage((StageEnterMessage) m);
            } else if(m instanceof RegisterChangedMessage) {
            	l.processRegisterChangedMessage((RegisterChangedMessage) m);
            }
        }
    }

    /**this method is used to set up the cpu whenever a new program is loaded into it
     *
     * @param program the program received from the assembler
     */
    public void loadProgram(Program program) {
        this.program = program;

        this.instructionRegister = null;//nothing to put in yet so null

        this.clearRegisters();//reset the registers

        //setting up memory
        Address textSegmentStart = this.program.textSegmentStart;
        Address dataSegmentStart = this.program.dataSegmentStart;
        Address dynamicSegmentStart = this.program.dynamicSegmentStart;
        Address stackPointer = new Address((int)DataConverter.decodeAsSigned(this.program.initialSP.getWord()));
        byte[] staticDataSegment = this.program.dataSegment;
        Map<Address,Statement> textSegment = this.program.textSegment;
        this.memory = new MainMemory(textSegment,staticDataSegment,textSegmentStart,dataSegmentStart,dynamicSegmentStart,stackPointer);

        labels = new HashMap<>();
        labelMetaData = new HashMap<>();
        for(Map.Entry<Label, Address> l : program.labels.entrySet()) {
            String key = l.getKey().getName();
            labels.put(key, l.getValue());
            labelMetaData.put(key, l.getKey());
        }

		annotations = program.annotations;

        try {
            this.programCounter = getEntryPoint();//set the program counter to the entry point to the program
        } catch(Exception e) {//if entry point load fails
            sendMessage(new ProblemMessage(e.getMessage()));//send problem to logger
        }

        this.registers[Register.gp.getID()] = this.program.initialGP;//setting global pointer
        sendMessage(new RegisterChangedMessage(Register.gp));
        this.registers[Register.sp.getID()] = this.program.initialSP;//setting up stack pointer
        sendMessage(new RegisterChangedMessage(Register.gp));

        this.Alu = new ALU();//initialising Alu
        this.getLastAddress();
    }

    public Program getProgram() {
        return program;
    }

    /**this method resets the registers in the memory
     * it then initialises them to some default value
     */
    private void clearRegisters() {
        this.registers = new Word[32];
        for(int i = 0; i < this.registers.length; i++) {
            this.registers[i] = new Word(DataConverter.encodeAsUnsigned(0));
            sendMessage(new RegisterChangedMessage(Register.fromID(i)));//firing to visualisation
        }
    }

    /**this method will look for the main label and get it's corresponding address
     * this is for use with the program counter
     * @throws ProgramException thrown if no main label is found
     */
    private Address getEntryPoint() throws ProgramException {
        Map<Label,Address> labels = this.program.labels;//map of labels

        for(Map.Entry<Label, Address> entry : labels.entrySet()) {//iterating through map
            if(entry.getKey().getName().toLowerCase().equals("main")) {//if main label found
                return entry.getValue();
            }
        }

        throw new ProgramException("No main label found.", this.program);
    }

    /**method gets the last address for use of ending the FDE cycle
     *
     */
    private void getLastAddress() {
        Address maxAddress = new Address(0);//to store max (last)

        for(Map.Entry<Address, Statement> entry : this.program.textSegment.entrySet()) {
            if(entry.getKey().getValue() >= maxAddress.getValue()) {//if greater than current last address
                maxAddress = entry.getKey();
            }
        }
        this.lastAddress = maxAddress;
    }

    /**carries out the fetch part of the FDE cycle (non pipelined)
     *
     */
    protected void fetch() throws MemoryException {
    	sendMessage(new StageEnterMessage(Stage.Fetch));//signal start of stage
        this.instructionRegister = this.memory.readFromTextSegment(this.programCounter);
        this.programCounter = new Address(this.programCounter.getValue() + 4);//incrementing the program counter
    }

    /**this method carries out the decode of the FDE cycle, it will
     * look through the statement object and take the instruction and decode the operands
     * @param instruction the instruction format to decode
     * @param operandList the list of operands to be decoded
     * @return InstructionFormat the instruction ready for execution
     * @throws DecodeException if something goes wrong during decode
     */
    protected InstructionFormat decode(Instruction instruction, List<Operand> operandList) throws DecodeException {

    	sendMessage(new StageEnterMessage(Stage.Decode));//signal start of decode
        Operand op1 = null;
        OperandType op1Type = null;
        Operand op2 = null;
        OperandType op2Type = null;
        Operand op3 = null;
        OperandType op3Type = null;

        if(operandList.size() > 0) {
            op1 = operandList.get(0);
            op1Type = op1.getOperandFormatType();
        }
        if(operandList.size() > 1) {
            op2 = operandList.get(1);
            op2Type = op2.getOperandFormatType();
        }
        if(operandList.size() > 2) {
            op3 = operandList.get(2);
            op3Type = op3.getOperandFormatType();
        }
        if(operandList.size() > 3) {
            throw new DecodeException("Too many operands.",op1);
        }
        if(!instruction.getOperandFormat().valid(op1Type,op2Type,op3Type)) {
            throw new DecodeException("Not valid set of operands.", op1);//if invalid operands given
        }

        //separating into different instruction types now
        if(instruction.getOperandFormat() == OperandFormat.destSrcSrc) {
            // R-type instruction: 2 src, 1 dest
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Optional<Word> dest = Optional.empty();//destination always empty to start
            Register destinationRegister = op1.asRegisterOp().value;//store destination register
            Optional<Word> src1 = Optional.of(decodeRegister(op2.asRegisterOp()));
            Optional<Word> src2 = Optional.of(decodeRegister(op3.asRegisterOp()));
            return new RTypeInstruction(instruction, dest, destinationRegister, src1, src2);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destSrcImm || instruction.getOperandFormat() == OperandFormat.destSrcImmU) {//immediate arithmetic operations (signed and unsigned)
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Optional<Word> dest = Optional.empty();
            Register destinationRegister = op1.asRegisterOp().value;
            Optional<Word> srcRegister = Optional.of(this.decodeRegister(op2.asRegisterOp()));
            Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(op3.asIntegerOp()));
            return new RTypeInstruction(instruction, dest, destinationRegister, srcRegister, immValue);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destSrc) {//single register ops like neg or abs (or move)
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> dest = Optional.empty();
            Register destinationRegister = op1.asRegisterOp().value;
            Optional<Word> srcRegister = Optional.of(this.decodeRegister(op2.asRegisterOp()));
            Optional<Word> fakeSecondRegister = Optional.empty(); //dummy word to pass into the alu (dealt with in the alu easily)
            return new RTypeInstruction(instruction, dest, destinationRegister, srcRegister, fakeSecondRegister);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destImm) {//instructions such as li
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> destRegister = Optional.empty();
            Optional<Register> destinationRegister = Optional.of(op1.asRegisterOp().value);
            Optional<Address> memAddress = Optional.empty();
            Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(op2.asIntegerOp()));
            return new LSInstruction(instruction,destRegister,destinationRegister,memAddress,immValue);
        }
        else if(instruction.getOperandFormat() == OperandFormat.noArguments) {//syscall, nop, break
            return new SpecialInstruction(instruction);
        }
        else if(instruction.getOperandFormat() == OperandFormat.label) {//branch, jal, j
            assert (op1 != null) && (op2 == null) && (op3 == null);

            Optional<Address> goToAddress = Optional.of(this.decodeAddressOperand(op1.asAddressOp()));//where to jump
            Optional<Word> currentAddress = Optional.empty();//storing current address if needed by jal
            currentAddress = Optional.of(new Word(DataConverter.encodeAsSigned((long)this.programCounter.getValue())));
            return new JTypeInstruction(instruction,goToAddress,currentAddress);
        }
        else if(instruction.getOperandFormat() == OperandFormat.register) {//for jr
            assert (op1 != null) && (op2 == null) && (op3 == null);

            Word registerContents = this.decodeRegister(op1.asRegisterOp());//getting register contents
            Optional<Address> registerAddress = Optional.of(new Address((int)decodeU(registerContents.getWord())));//put into correct format
            Optional<Word> fakeCurrentWord = Optional.empty();
            return new JTypeInstruction(instruction,registerAddress,fakeCurrentWord);
        }
        else if(instruction.getOperandFormat() == OperandFormat.cmpCmpLabel) {//for branch equal etc.
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Optional<Word> cmp1 = Optional.of(this.decodeRegister(op1.asRegisterOp()));//first comparison value
            Optional<Word> cmp2 = Optional.of(this.decodeRegister(op2.asRegisterOp()));//second comparison value
            Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(op3.asAddressOp()));//where to branch to if comparison returns true
            return new ITypeInstruction(instruction,cmp1,cmp2,branchAddr);
        }
        else if(instruction.getOperandFormat() == OperandFormat.cmpLabel) {//for bltz etc
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> cmp = Optional.of(this.decodeRegister(op1.asRegisterOp()));//value to compare
            Optional<Word> fakeCmp = Optional.empty();//used to make ALU calculations easier
            Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));//branch address
            return new ITypeInstruction(instruction,cmp,fakeCmp,branchAddr);
        }
        else if(instruction.getOperandFormat() == OperandFormat.srcAddr) {//for store instructions
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> src = Optional.of(this.decodeRegister(op1.asRegisterOp()));//word to store
            Optional<Register> destReg = Optional.empty();
            Optional<Address> toStore = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));
            Optional<Word> immVal = Optional.empty();
            return new LSInstruction(instruction,src,destReg,toStore,immVal);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destAddr) {//for load stuff
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> dest = Optional.empty();//where to store retrieved data
            Optional<Register> loadInto = Optional.of(op1.asRegisterOp().value);//register to store
            Optional<Address> toRetrieve = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));
            Optional<Word> immValue = Optional.empty();
            return new LSInstruction(instruction,dest,loadInto,toRetrieve,immValue);
        }
        else {//invalid instruction format or BREAK
            throw new DecodeException("Invalid instruction format.", op1);
        }
    }

    /**this method will execute the instruction given to it
     *
     * @param instruction instruction set up with all necessary data
     * @throws InstructionException if problem during execution
     * @throws ExecuteException if problem during execution
     * @throws HeapException if problem with heap
     * @throws MemoryException if problem accessing memory
     * @throws StackException 
     */
    protected void execute(InstructionFormat instruction) throws InstructionException, ExecuteException, MemoryException, HeapException, StackException {
        sendMessage(new StageEnterMessage(Stage.Execute));//signal start of execution
    	switch(instruction.mode) {//switch based on instruction format
            case RTYPE:
                Word result = this.Alu.execute(instruction.getInstruction(), instruction.asRType().getSrc1(), instruction.asRType().getSrc2());
                this.registers[instruction.asRType().getDestReg().getID()] = result;//storing result
                sendMessage(new RegisterChangedMessage(instruction.asRType().getDestReg()));
                break;
            case ITYPE:
                Word branchTest = this.Alu.execute(instruction.getInstruction(), instruction.asIType().getCmp1(), instruction.asIType().getCmp2());//carrying out comparison
                if(equalByteArrays(branchTest.getWord(), ALU.branchTrue)) {
                    this.programCounter = instruction.asIType().getBranchAddress().get();//set the program counter
                }
                break;
            case SPECIAL:
                if(instruction.getInstruction().equals(Instruction.syscall)) {//syscall
                    int v0 = (int)DataConverter.decodeAsSigned(this.registers[Register.v0.getID()].getWord());//getting code for syscall
                    syscall(v0);//carry out specified syscall op
                }
                else if(instruction.getInstruction().equals(Instruction.BREAK)) {
                	this.pauseClock();//stop clock for now
                }
                else if(!instruction.getInstruction().equals(Instruction.nop)) {
                    throw new ExecuteException("Error with zero argument instruction", instruction);
                }
                break;
            case JTYPE:
                if(instruction.getInstruction().equals(Instruction.jal)) {//making sure i put current address in ra
                    this.registers[Register.ra.getID()] = instruction.asJType().getCurrentAddress().get();
                    sendMessage(new RegisterChangedMessage(Register.ra));
                }

                this.programCounter = instruction.asJType().getJumpAddress().get();//loading new address into the PC
                break;
            case LSTYPE:
                if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.destImm)) {//li
                    this.registers[instruction.asLSType().getRegisterName().get().getID()] = instruction.asLSType().getImmediate().get();
                    sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                }
                else if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.destAddr)) {//load

                    int retrieveAddress = instruction.asLSType().getMemAddress().get().getValue();
                    if(instruction.getInstruction().equals(Instruction.la)) {//have to be careful with la
                    	Word address = new Word(DataConverter.encodeAsSigned((long)retrieveAddress));
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = address;
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                	}
                    else if(instruction.getInstruction().equals(Instruction.lw)) {
                    	int length = 4;//this may be wrong, hence outside, may depend on instruction
                    	byte[] read = this.memory.readFromMem(retrieveAddress, length);
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = new Word(read);
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                    }
                    else if(instruction.getInstruction().equals(Instruction.lb)) {
                    	byte[] read = this.memory.readFromMem(retrieveAddress, 1);
                    	long val = DataConverter.decodeAsSigned(read);
                    	byte[] signed = DataConverter.encodeAsSigned(val);
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = new Word(signed);
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                    }
                    else if(instruction.getInstruction().equals(Instruction.lbu)) {
                    	byte[] read = this.memory.readFromMem(retrieveAddress, 1);
                    	long val = DataConverter.decodeAsUnsigned(read);
                    	byte[] unsigned = DataConverter.encodeAsUnsigned(val);
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = new Word(unsigned);
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                    }
                    else if(instruction.getInstruction().equals(Instruction.lh)) {
                    	byte[] read = this.memory.readFromMem(retrieveAddress, 2);
                    	long val = DataConverter.decodeAsSigned(read);
                    	byte[] signed = DataConverter.encodeAsSigned(val);
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = new Word(signed);
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                    }
                    else if(instruction.getInstruction().equals(Instruction.lhu)) {
                    	byte[] read = this.memory.readFromMem(retrieveAddress, 2);
                    	long val = DataConverter.decodeAsUnsigned(read);
                    	byte[] unsigned = DataConverter.encodeAsUnsigned(val);
                    	this.registers[instruction.asLSType().getRegisterName().get().getID()] = new Word(unsigned);
                    	sendMessage(new RegisterChangedMessage(instruction.asLSType().getRegisterName().get()));
                    }
                }
                else if(instruction.getInstruction().getOperandFormat().equals(OperandFormat.srcAddr)) {//store
                	
                	if(instruction.getInstruction().equals(Instruction.sb)) {
                		byte toStore = instruction.asLSType().getRegister().get().getWord()[3];//lowest byte
                		int storeAddress = instruction.asLSType().getMemAddress().get().getValue();
	                    this.memory.writeToMem(storeAddress, new byte[]{toStore});//this might be better though still naive
                	}
                	else if(instruction.getInstruction().equals(Instruction.sh)) {
                		byte[] toStore = instruction.asLSType().getRegister().get().getWord();
                		toStore = new byte[]{toStore[2],toStore[3]};
                		int storeAddress = instruction.asLSType().getMemAddress().get().getValue();
	                    this.memory.writeToMem(storeAddress, toStore);//this might be better though still naive
                	}
                	else {//sw
	                    byte[] toStore = instruction.asLSType().getRegister().get().getWord();//all 4 bytes
	                    int storeAddress = instruction.asLSType().getMemAddress().get().getValue();
	                    this.memory.writeToMem(storeAddress, toStore);//this might be better though still naive
                	}
                }
                else {
                    throw new ExecuteException("Error executing load/store instruction.", instruction);
                }
                break;
            default:
                throw new ExecuteException("Error during Execution", instruction);
        }
    }

    /**will use IO to enable the use of system calls with the user
     * 
     * @param v0 the syscall code retrieved from the v0 register
     * @throws InstructionException if invalid syscall code
     * @throws HeapException if problem using sbrk like a0 not multiple of 4
     * @throws MemoryException if problem reading from memory for read string
     * @throws StackException 
     */
    private void syscall(int v0) throws InstructionException, HeapException, MemoryException, StackException {
    	int a0 = (int)DataConverter.decodeAsSigned(this.registers[Register.a0.getID()].getWord());//getting main argument register
    	switch(v0) {
    		case 1://print int
    			this.io.printInt(a0);//printing to console
    			break;
    		case 4://print string
    			String toPrint = "";//initial string
    			byte[] currentByte;
    			int addressPStr = a0;
    			currentByte = this.memory.readFromMem(a0, 1);//reading in blocks of 4 bytes, i.e 1 character
    			while(DataConverter.decodeAsSigned(currentByte) != 0) {//while not at null terminator
    				toPrint += new String(currentByte);//converting to char
    				addressPStr += 1;//incrementing address to next byte
    				currentByte = this.memory.readFromMem(addressPStr, 1);//next word to read
    			}
    			this.io.printString(toPrint);
    			break;
    		case 5://read int
    			int read = this.io.readInt();//reading in from console
    			Word readAsWord = new Word(DataConverter.encodeAsSigned((long)read));
    			this.registers[Register.v0.getID()] = readAsWord;//storing in v0
    			sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		case 8://read string
    			String readInString = this.io.readString();//this string will be cut to maxChars -1 i.e last one will be null terminator
    			int a1 = (int)DataConverter.decodeAsSigned(this.registers[Register.a1.getID()].getWord());//max chars stored here
    			int addressIBuf = a0;//start of input buffer
    			if(readInString.length() >= a1) {//truncating string
    				readInString = readInString.substring(0, a1-1);
    			}
    			for(int i = 0; i < readInString.length(); i++) {//writing each character to memory
    				String toWriteStr = "" + readInString.charAt(i);
    				byte[] toWrite = toWriteStr.getBytes();
    				this.memory.writeToMem(addressIBuf, toWrite);//write into memory
    				addressIBuf += 1;//moving to next word in memory
    			}
    			
    			byte[] nullTerminator = new byte[]{0x00,0x00,0x00,0x00};//null terminator for string
    			this.memory.writeToMem(addressIBuf, nullTerminator);//adding terminator signals end of string
    			break;
    		case 9://sbrk
    			Address newBreak = this.memory.getHeap().sbrk(a0);
    			this.registers[Register.v0.getID()] = new Word(DataConverter.encodeAsSigned(newBreak.getValue()));
    			sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		case 10://exit program
    			this.stopRunning();
    			break;
    		case 11://print char
    			char toPrintChar = new String(new byte[]{DataConverter.encodeAsUnsigned(a0)[3]}).charAt(0);//int directly to char
    			this.io.printChar(toPrintChar);
    			break;
    		case 12://read char
    			String readChar = this.io.readChar() + "";//from console
    			byte[] asBytes = readChar.getBytes();
    			long asLong = DataConverter.decodeAsSigned(asBytes);
    			Word toWord = new Word(DataConverter.encodeAsSigned(asLong));//format for register storage
    			this.registers[Register.v0.getID()] = toWord;
    			sendMessage(new RegisterChangedMessage(Register.v0));
    			break;
    		default://if invalid syscall code
    			throw new InstructionException("Invalid syscall operation", Instruction.syscall);
    	}
    }
    
    /**this method will run a single cycle of the FDE cycle
     * @throws MemoryException if problem accessing memory
     * @throws DecodeException if error during decode
     * @throws InstructionException if error with instruction
     * @throws ExecuteException if problem during execution
     * @throws HeapException if the heap goes wrong at some point
     * @throws StackException 
     *
     */
    public void runSingleCycle() throws MemoryException, DecodeException, InstructionException, ExecuteException, HeapException, StackException {
        fetch();
        sendMessage(new ExecuteStatementMessage(programCounter));
		InstructionFormat instruction = decode(this.instructionRegister.getInstruction(),this.instructionRegister.getOperandList());
        execute(instruction);
		if(annotations.containsKey(programCounter)) {
			for(Annotation a : annotations.get(programCounter)) {
				sendMessage(new AnnotationMessage(a));
			}
		}

        if(this.programCounter.getValue() == this.lastAddress.getValue()+4) {//if end of program reached
            this.isRunning = false;//stop running
        }
    }

    /**this method will run the program given to the CPU, it will operate under the clock cycle
     *
     */
    public void runProgram() {
    	this.startClock();
        System.out.println("---- Program Execution Started ----");
        this.isRunning = true;
        while(isRunning) {//need something to stop this
        	try {
        		this.runSingleCycle();//run one loop of Fetch,Decode,Execute
        	} catch(MemoryException | DecodeException | InstructionException | ExecuteException | HeapException | StackException e) {
        		sendMessage(new ProblemMessage(e.getMessage()));
        	}
            try {
                if(isRunning) {
                    clock.waitForNextTick();
                }
            } catch(InterruptedException | BrokenBarrierException e) {
				System.out.println("CPU interrupted");
            }
		}
        System.out.println("---- Program Execution Ended ----");
        stopRunning();
    }

    /**useful auxiliary methods to check if 2 byte arrays equal
     *
     * @param arr1 first array
     * @param arr2 second array
     * @return are they equal?
     */
    private boolean equalByteArrays(byte[] arr1, byte[] arr2) {
        for(int i = 0; i < arr1.length; i++) {
            if(arr1[i] != arr2[i]) {
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
    private Word decodeIntegerOperand(IntegerOperand operand) throws DecodeException {
        if(operand.getOperandFormatType().equals(OperandFormat.OperandType.UNSIGNED_IMMEDIATE)) {//if unsigned
            return encodeU((long)operand.value);
        }
        else if(operand.getOperandFormatType().equals(OperandType.IMMEDIATE)) {//signed immediate
            return encodeS((long)operand.value);
        }
        else {
            throw new DecodeException("Error decoding integer operand.", operand);
        }
    }

    /**
     * calculate the address for an operand with the current simulation state
     *
     * @param operand the operand to decode
     * @return the calculated address
     */
    private Address decodeAddressOperand(AddressOperand operand) throws DecodeException {
        int labelAddress    = 0;
        int constantAddress = 0;
        int registerAddress = 0;

        if(operand.labelName.isPresent()) {
            labelAddress = labels.getOrDefault(operand.labelName.get(), Address.NULL).getValue();
        }
        if(operand.constant.isPresent()) {
            constantAddress = operand.constant.get();
        }
        if(operand.register.isPresent()) {
            Register r = operand.register.get();
            registerAddress = (int) decodeU(registers[r.getID()].getWord());
        }
        return new Address(labelAddress + constantAddress + registerAddress);
    }

    /**this method will decode a register operand
     * if it is not a destination register then the data will be retrieved, otherwise
     * a null Word will be returned
     * @param operand the operand to decode
     * @return the word of data from the register
     * @throws DecodeException if something goes wrong during decode
     */
    private Word decodeRegister(RegisterOperand operand) throws DecodeException {
        if(operand == null) {
            return Word.ZERO;
        }
        else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.DEST_REGISTER)) {
            return Word.ZERO;
        }
        else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.SRC_REGISTER)) {
            return this.registers[operand.value.getID()];//return the word stored at that register
        }
        else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.TARGET_REGISTER)) {
            return this.registers[operand.value.getID()];//this is probably wrong for now
        }
        else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.REGISTER)) {//standard register
            return this.registers[operand.value.getID()];//return the word stored at that register
        }
        else {
            throw new DecodeException("Error decoding Register.", operand);
        }
    }


    /**
     * interpret a byte array as a 4 byte signed integer
     *
     * @param word the word to interpret
     * @return the interpreted value
     */
    private static long decodeS(byte[] word) {
        return DataConverter.decodeAsSigned(word);
    }

    /**
     * interpret a byte array as a 4 byte unsigned integer
     *
     * @param word the word to interpret
     * @return the interpreted value
     */
    private static long decodeU(byte[] word) {
        return DataConverter.decodeAsUnsigned(word);
    }

    /**
     * take a value interpreted as having a sign and encode it as a word
     *
     * @param value the signed value to encode
     * @return the encoded value
     */
    private static Word encodeS(long value) {
        return new Word(DataConverter.encodeAsSigned(value));
    }

    /**
     * take a value interpreted as being unsigned and encode it as a word
     *
     * @param value the unsigned value to encode
     * @return the encoded value
     */
    private static Word encodeU(long value) {
        return new Word(DataConverter.encodeAsUnsigned(value));
    }

    /**returns register array for retrieval by the UI
     * 
     * @return the array of words representing the registers
     */
	public Word[] getRegisters() {
		return registers;
	}
}
