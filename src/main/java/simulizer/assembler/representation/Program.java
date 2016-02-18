package simulizer.assembler.representation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * store the assembled information for a program, ready to be fed into the
 * simulator
 * @author mbway
 */
public class Program {

    /**
     * statements of the program
     */
    public Map<Address, Statement> textSegment;
    public Address textSegmentStart;

    /**
     * metadata and type information about areas of the static data segment
     */
    public Map<Address, Variable> dataSegmentVariables;
    public Address dataSegmentStart;

    /**
     * the initial state of the static data segment
     */
    public byte[] dataSegment;
    /**
     * the initial value of the break
     */
    public Address dynamicSegmentStart;

    public Map<Label, Address> labels;
    public Map<Address, List<Annotation>> annotations;
    public Map<Address, Integer> lineNumbers;

    /**
     * a hash of the source code to determine whether changes have occurred
     */
    public int sourceHash;


    public Program() {
        textSegment = new HashMap<>();
        textSegmentStart = Address.NULL;
        dataSegmentVariables = new HashMap<>();
        dataSegmentStart = Address.NULL;
        dataSegment = null;
        dynamicSegmentStart = Address.NULL;
        labels = new HashMap<>();
        annotations = new HashMap<>();
        lineNumbers = new HashMap<>();
        sourceHash = -1;
    }

}
