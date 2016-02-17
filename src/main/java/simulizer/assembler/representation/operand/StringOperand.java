package simulizer.assembler.representation.operand;

public class StringOperand extends Operand {

    public String value;

    @Override
    public Type getType() {
        return Type.String;
    }

    @Override
    public OperandFormat.OperandType getOperandFormatType() {
        return null; // not a valid instruction operand
    }

    @Override
    public StringOperand asStringOp() {
        return this;
    }

    public StringOperand(String value) {
        this.value = value;
    }
}
