package simulizer.assembler.representation;

import simulizer.assembler.representation.operand.Operand;

import java.util.List;

public class Statement {

    public Instruction i;
    public List<Operand> operandList;
    public int lineNumber;

    public Statement(Instruction i, List<Operand> operandList, int lineNumber) {
        this.i = i;
        this.operandList = operandList;
        this.lineNumber = lineNumber;
    }

}
