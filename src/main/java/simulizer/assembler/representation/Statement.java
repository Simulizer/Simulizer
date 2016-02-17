package simulizer.assembler.representation;

import simulizer.assembler.representation.operand.Operand;

import java.util.List;

/**
 * stores an instruction and its operands
 * @author mbway
 */
public class Statement {

    private Instruction instruction;
    private List<Operand> operandList;
    private int lineNumber;

    public Statement(Instruction instruction, List<Operand> operandList, int lineNumber) {
        this.instruction = instruction;
        this.operandList = operandList;
        this.lineNumber = lineNumber;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public List<Operand> getOperandList() {
        return operandList;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}
