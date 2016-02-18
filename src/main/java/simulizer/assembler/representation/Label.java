package simulizer.assembler.representation;

/**
 * store the information that the assembler needs to setup the label for use in
 * the simulator
 * @author mbway
 */
public class Label {

    public enum Type {
        INSTRUCTION,
        VARIABLE
    }

    private String name;
    private int lineNumber;

    private Type type;

    public Label(String name, int lineNumber, Type type) {
        this.name = name;
        this.lineNumber = lineNumber;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Label(" + name + "->" + lineNumber + ":" + type + ")";
    }

    public String getName() {
        return name;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public Type getType() {
        return this.type;
    }


}
