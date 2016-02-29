package simulizer.assembler.representation.operand;

/**
 * store a string operand to an assembler directive
 * @author mbway
 */
public class StringOperand extends Operand {

    public final String value;

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
