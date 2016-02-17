package simulizer.assembler.representation;

import java.util.HashMap;
import java.util.Map;

public class Program {

    public Map<Integer, Statement> textSegment;
    public Map<Integer, Variable>  dataSegment;

    public Map<Label, Integer> labels;
    // Address -> Line Number
    public Map<Integer, Integer> lineNumbers;

    public int sourceHash;


    public Program() {
        textSegment = new HashMap<>();
        dataSegment = new HashMap<>();
        labels = new HashMap<>();
        lineNumbers = new HashMap<>();
        sourceHash = -1;
    }

}
