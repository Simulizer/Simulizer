package simulizer.assembler.representation.operand;

/**
 * store an operand to an assembler directive or instruction
 * @author mbway
 */
public abstract class Operand {
    public enum Type {
        Integer,
        String,
        Address,
        Register
    }

    public abstract Type getType();
    public abstract OperandFormat.OperandType getOperandFormatType();

    public AddressOperand  asAddressOp() { return null; }
    public IntegerOperand  asIntegerOp() { return null; }
    public StringOperand   asStringOp()  { return null; }
    public RegisterOperand asRegisterOp(){ return null; }
}
