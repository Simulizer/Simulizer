package simulizer.assembler.representation;

import simulizer.assembler.representation.operand.Operand;

import java.util.Optional;

/**
 * store the information that the assembler needs to setup the variable in the
 * static data segment.
 * @author mbway
 */
public class Variable {

    public enum Type {
        Byte,
        Half,
        Word,
        ASCII,
        ASCIIZ,
        Space
    }

    private final Type type;
    private final int size;
    private final Optional<Operand> initialValue;
    private final int lineNumber;

    public Variable(Type type, int size, Optional<Operand> initialValue, int lineNumber) {
        this.type = type;
        this.size = size;
        this.initialValue = initialValue;
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        String initial = initialValue.isPresent() ? initialValue.get().toString() : "no initial value";
        return "Variable(" + type + "(" + size + " bytes) : " + initial + ")";
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public Optional<Operand> getInitialValue() {
        return initialValue;
    }

    public int getLineNumber() {
        return lineNumber;
    }


}
