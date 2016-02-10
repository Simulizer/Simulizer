package simulizer.assembler.representation;

import java.util.HashMap;
import java.util.Map;

public class Program {

    public Map<Address, Statement> textSegment;
    public Address textSegmentStart;

    public Map<Address, Variable> dataSegmentVariables;
    public Address dataSegmentStart;
    public byte[] dataSegment;
    public Address dynamicSegmentStart;

    public Map<Label, Address> labels;
    public Map<Address, Integer> lineNumbers;

    public int sourceHash;


    public Program() {
        textSegment = new HashMap<>();
        textSegmentStart = Address.NULL;
        dataSegmentVariables = new HashMap<>();
        dataSegmentStart = Address.NULL;
        dataSegment = null;
        dynamicSegmentStart = Address.NULL;
        labels = new HashMap<>();
        lineNumbers = new HashMap<>();
        sourceHash = -1;
    }

}
