package simulizer.assembler.representation.operand;

/**
 * store an integer operand to either an assembler directive or instruction can
 * have an optional +/- sign followed by either decimal digits or '0x' followed
 * by hexadecimal digits
 *
 * @author mbway
 */
public class IntegerOperand extends Operand {

    public final int value;

    @Override
    public Type getType() {
        return Type.Integer;
    }

    @Override
    public OperandFormat.OperandType getOperandFormatType() {
        if(value >= 0) {
            // also considered a valid IMMEDIATE
            return OperandFormat.OperandType.UNSIGNED_IMMEDIATE;
        } else {
            return OperandFormat.OperandType.IMMEDIATE;
        }
    }

    @Override
    public IntegerOperand asIntegerOp() {
        return this;
    }

    public IntegerOperand(int value) {
        this.value = value;
    }
}
