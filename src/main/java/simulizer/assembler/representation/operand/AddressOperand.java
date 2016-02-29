package simulizer.assembler.representation.operand;

import simulizer.assembler.representation.Register;

import java.util.Optional;

/**
 * store an address operand to either an assembler directive or instruction can
 * take one of the following forms:
 *       +/- constant           <-- would be interpreted as an integer instead
 * label +/- constant
 *       +/- constant (base register)
 * label +/- constant (base register)
 *
 * @author mbway
 */
public class AddressOperand extends Operand {

    // either as an base or as the only part
    public final Optional<String> labelName;
    // because of the way the grammar works, this is never on its own, it is always an offset
    // may be positive or negative
    public final Optional<Integer> constant;
    // either as a base, offset, or as the only part
    public final Optional<Register> register;

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

    public AddressOperand(Optional<String> labelName, Optional<Integer> constant, Optional<Register> register) {
        this.labelName = labelName;
        this.constant  = constant;
        this.register  = register;
    }

    public boolean labelOnly() {
        return labelName.isPresent() && !constant.isPresent() && !register.isPresent();
    }
}
