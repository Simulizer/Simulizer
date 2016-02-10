package simulizer.assembler.representation.operand;

import simulizer.assembler.representation.Register;

public class RegisterOperand extends Operand {

    public Register r;

    @Override
    public Type getType() {
        return Type.Register;
    }

    @Override
    public OperandFormat.OperandType getOperandFormatType() {
        return OperandFormat.OperandType.REGISTER;
    }

    @Override
    public RegisterOperand asRegisterOp() {
        return this;
    }

    public RegisterOperand(Register r) {
        this.r = r;
    }

}
