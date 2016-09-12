package simulizer.simulation.cpu.components;

import java.util.List;
import java.util.Optional;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.AddressOperand;
import simulizer.assembler.representation.operand.IntegerOperand;
import simulizer.assembler.representation.operand.Operand;
import simulizer.assembler.representation.operand.OperandFormat;
import simulizer.assembler.representation.operand.RegisterOperand;
import simulizer.assembler.representation.operand.OperandFormat.OperandType;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.simulation.exceptions.DecodeException;
import simulizer.simulation.instructions.ITypeInstruction;
import simulizer.simulation.instructions.InstructionFormat;
import simulizer.simulation.instructions.JTypeInstruction;
import simulizer.simulation.instructions.LSInstruction;
import simulizer.simulation.instructions.RTypeInstruction;
import simulizer.simulation.instructions.SpecialInstruction;
import simulizer.simulation.messages.DataMovementMessage;
import simulizer.simulation.messages.StageEnterMessage;
import simulizer.simulation.messages.StageEnterMessage.Stage;

/**class deals with the decoding of instructions
 * the code here is in it's own class as part of a refactoring of the system
 * @author Charlie Street
 *
 */
public class Decoder {

	private CPU cpu;
	
	/**decoder needs access to cpu registers
	 * as well as the labels used in the program
	 * it also needs to send decode related messages
	 * @param cpu the cpu being used for instruction execution
	 */
	public Decoder(CPU cpu) {
		this.cpu = cpu;
	}
	
	/**this method carries out the decode of the FDE cycle, it will
     * look through the statement object and take the instruction and decode the operands
     * @param instruction the instruction format to decode
     * @param operandList the list of operands to be decoded
     * @return InstructionFormat the instruction ready for execution
     * @throws DecodeException if something goes wrong during decode
     */
    protected InstructionFormat decode(Instruction instruction, List<Operand> operandList) throws DecodeException {

    	cpu.sendMessage(new StageEnterMessage(Stage.Decode));//signal start of decode
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
        if(!instruction.getOperandFormat().valid(op1Type, op2Type, op3Type)) {
            throw new DecodeException("Not valid set of operands.", op1);//if invalid operands given
        }

        //separating into different instruction types now
        if(instruction.getOperandFormat() == OperandFormat.destSrcSrc) {
            // R-type instruction: 2 src, 1 dest
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Register destinationRegister = op1.asRegisterOp().value;//store destination register
            Optional<Word> src1 = Optional.of(decodeRegister(op2.asRegisterOp()));
            cpu.sendMessage(new DataMovementMessage(src1,Optional.empty()));
            Optional<Word> src2 = Optional.of(decodeRegister(op3.asRegisterOp()));
            cpu.sendMessage(new DataMovementMessage(src2,Optional.empty()));
            return new RTypeInstruction(instruction, Optional.empty(), destinationRegister, src1, src2);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destSrcImm) { //immediate arithmetic operations (signed or unsigned)
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Register destinationRegister = op1.asRegisterOp().value;
            Optional<Word> srcRegister = Optional.of(this.decodeRegister(op2.asRegisterOp()));
            cpu.sendMessage(new DataMovementMessage(srcRegister,Optional.empty()));
            Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(op3.asIntegerOp()));
            return new RTypeInstruction(instruction, Optional.empty(), destinationRegister, srcRegister, immValue);
        }
        else if(instruction.getOperandFormat() == OperandFormat.destSrc) {//single register ops like neg or abs (or move)
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Register destinationRegister = op1.asRegisterOp().value;
            Optional<Word> srcRegister = Optional.of(this.decodeRegister(op2.asRegisterOp()));
            cpu.sendMessage(new DataMovementMessage(srcRegister,Optional.empty()));
            return new RTypeInstruction(instruction, Optional.empty(), destinationRegister, srcRegister, Optional.empty());
        }
        else if(instruction.getOperandFormat() == OperandFormat.destImm) {//instructions such as li
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Register> destinationRegister = Optional.of(op1.asRegisterOp().value);
            Optional<Address> memAddress = Optional.empty();
            Optional<Word> immValue = Optional.of(this.decodeIntegerOperand(op2.asIntegerOp()));
            return new LSInstruction(instruction,Optional.empty(),destinationRegister,memAddress,immValue);
        }
        else if(instruction.getOperandFormat() == OperandFormat.noArguments||instruction.equals(Instruction.BREAK)) {//syscall, nop, break
            return new SpecialInstruction(instruction);
        }
        else if(instruction.getOperandFormat() == OperandFormat.label
                || (instruction.getOperandFormat() == OperandFormat.labelOrReg && op1 != null && op1.asAddressOp() != null)) {//branch, jal, j
            assert (op1 != null) && (op2 == null) && (op3 == null);

            Optional<Address> goToAddress = Optional.of(this.decodeAddressOperand(op1.asAddressOp()));//where to jump
            Optional<Word> currentAddress = Optional.of(new Word(DataConverter.encodeAsSigned((long)this.cpu.getProgramCounter().getValue())));
            return new JTypeInstruction(instruction,goToAddress,currentAddress);
        }
        else if(instruction.getOperandFormat() == OperandFormat.register
                || (instruction.getOperandFormat() == OperandFormat.labelOrReg && op1 != null && op1.asRegisterOp() != null)) {//for jr or j
            assert (op1 != null) && (op2 == null) && (op3 == null);

            Word registerContents = this.decodeRegister(op1.asRegisterOp());//getting register contents
            cpu.sendMessage(new DataMovementMessage(Optional.of(registerContents),Optional.empty()));
            Optional<Address> registerAddress = Optional.of(new Address((int)DataConverter.decodeAsUnsigned(registerContents.getBytes())));//put into correct format
            return new JTypeInstruction(instruction,registerAddress,Optional.empty());
        }
        else if(instruction.getOperandFormat() == OperandFormat.cmpCmpLabel) {//for branch equal etc.
            assert (op1 != null) && (op2 != null) && (op3 != null);

            Optional<Word> cmp1 = Optional.of(this.decodeRegister(op1.asRegisterOp()));//first comparison value
            cpu.sendMessage(new DataMovementMessage(cmp1,Optional.empty()));
            Optional<Word> cmp2 = Optional.of(this.decodeRegister(op2.asRegisterOp()));//second comparison value
            cpu.sendMessage(new DataMovementMessage(cmp2,Optional.empty()));
            Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(op3.asAddressOp()));//where to branch to if comparison returns true
            return new ITypeInstruction(instruction,cmp1,cmp2,branchAddr);
        }
        else if(instruction.getOperandFormat() == OperandFormat.cmpLabel) {//for bltz etc
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> cmp = Optional.of(this.decodeRegister(op1.asRegisterOp()));//value to compare
            cpu.sendMessage(new DataMovementMessage(cmp,Optional.empty()));
            Optional<Address> branchAddr = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));//branch address
            return new ITypeInstruction(instruction,cmp,Optional.empty(),branchAddr);
        }
        else if(instruction.getOperandFormat() == OperandFormat.srcAddr) {//for store instructions
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Word> src = Optional.of(this.decodeRegister(op1.asRegisterOp()));//word to store
            cpu.sendMessage(new DataMovementMessage(src,Optional.empty()));
            Optional<Address> toStore = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));
            return new LSInstruction(instruction,src,Optional.empty(),toStore,Optional.empty());
        }
        else if(instruction.getOperandFormat() == OperandFormat.destAddr) {//for load stuff
            assert (op1 != null) && (op2 != null) && (op3 == null);

            Optional<Register> loadInto = Optional.of(op1.asRegisterOp().value);//register to store
            Optional<Address> toRetrieve = Optional.of(this.decodeAddressOperand(op2.asAddressOp()));
            return new LSInstruction(instruction,Optional.empty(),loadInto,toRetrieve,Optional.empty());
        }
        else {
            //invalid instruction format
            throw new DecodeException("Invalid instruction format.", op1);
        }
    }
	
	/**this method will decode an integer operand into a 4 byte word
    *
    * @param operand the operand to decode
    * @return the decoded word
    * @throws DecodeException if something goes wrong during decode
    */
   private Word decodeIntegerOperand(IntegerOperand operand) throws DecodeException {
       if(operand.getOperandFormatType().equals(OperandFormat.OperandType.UNSIGNED_IMMEDIATE)) {//if unsigned
           return new Word(DataConverter.encodeAsUnsigned((long)operand.value));
       }
       else if(operand.getOperandFormatType().equals(OperandType.IMMEDIATE)) {//signed immediate
           return new Word(DataConverter.encodeAsSigned((long)operand.value));
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
           labelAddress = cpu.labels.getOrDefault(operand.labelName.get(), Address.NULL).getValue();
       }
       if(operand.constant.isPresent()) {
           constantAddress = operand.constant.get();
       }
       if(operand.register.isPresent()) {
           Register r = operand.register.get();
           registerAddress = (int) DataConverter.decodeAsUnsigned(cpu.getRegister(r).getBytes());
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
           return cpu.getRegister(operand.value);//return the word stored at that register
       }
       else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.TARGET_REGISTER)) {
           return this.cpu.getRegister(operand.value);//this is probably wrong for now
       }
       else if(operand.getOperandFormatType().equals(OperandFormat.OperandType.REGISTER)) {//standard register
           return this.cpu.getRegister(operand.value);//return the word stored at that register
       }
       else {
           throw new DecodeException("Error decoding Register.", operand);
       }
   }
	
}
