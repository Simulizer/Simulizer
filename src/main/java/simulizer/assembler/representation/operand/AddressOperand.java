package simulizer.assembler.representation.operand;

import simulizer.assembler.representation.Register;

import java.util.Optional;

public class AddressOperand extends Operand {

    // either as an base or as the only part
    public Optional<String> labelName;
    // because of the way the grammar works, this is never on its own, it is always an offset
    // may be positive or negative
    public Optional<Integer> constant;
    // either as a base, offset, or as the only part
    public Optional<Register> register;

    @Override
    public Type getType() {
        return Type.Address;
    }

    @Override
    public OperandFormat.OperandType getOperandFormatType() {
        boolean l = labelName.isPresent();
        boolean o = constant.isPresent();
        boolean r = register.isPresent();

        if(l && !o && !r) { // label only
            return OperandFormat.OperandType.LABEL;
        } else {
            return OperandFormat.OperandType.BASE_OFFSET;
        }
    }

    @Override
    public AddressOperand asAddressOp() {
        return this;
    }

    public AddressOperand() {
        labelName = Optional.empty();
        constant = Optional.empty();
        register = Optional.empty();
    }

    public boolean labelOnly() {
        return labelName.isPresent() && !constant.isPresent() && !register.isPresent();
    }
    public boolean constantOnly() {
        return !labelName.isPresent() && constant.isPresent() && !register.isPresent();
    }
}
