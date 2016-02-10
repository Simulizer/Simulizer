package simulizer.assembler.representation.operand;

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
