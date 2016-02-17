package simulizer.assembler.representation.operand;

public class IntegerOperand extends Operand {

    public int value;

    @Override
    public Type getType() {
        return Type.Integer;
    }

    @Override
    public OperandFormat.OperandType getOperandFormatType() {
        if(value >= 0) {
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
