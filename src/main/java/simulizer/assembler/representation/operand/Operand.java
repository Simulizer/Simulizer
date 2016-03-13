package simulizer.assembler.representation.operand;

import com.google.gson.Gson;

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

    @Override
    public String toString() {
        Type t = getType();
        String value = "";
        switch(t) {
            case Integer:  value = Integer.toString(asIntegerOp().value);  break;
            case String:
                // use Gson to escape the string
                Gson g = new Gson();
                value = g.toJson(asStringOp().value);
                break;
            case Address:
                AddressOperand ao = asAddressOp();
                String l = ao.labelName.isPresent() ? ao.labelName.get() : "";
                String c = "";
                if(ao.constant.isPresent()) {
                    int v = ao.constant.get();
                    c += Integer.signum(v) == -1 ? '-' : '+';
                    c += Integer.toString(v);
                }
                String r = ao.register.isPresent() ? "($" + ao.register.get().toString() + ")" : "";
                value =  l + c + r;
                break;
            case Register: value = "$" + asRegisterOp().value.toString(); break;
        }
        return value;
    }

    public AddressOperand  asAddressOp() { return null; }
    public IntegerOperand  asIntegerOp() { return null; }
    public StringOperand   asStringOp()  { return null; }
    public RegisterOperand asRegisterOp(){ return null; }
}
