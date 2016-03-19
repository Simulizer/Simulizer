package simulizer.assembler.representation;

import java.util.HashMap;
import java.util.Map;
import simulizer.simulation.data.representation.Word;

/**
 * store the assembled information for a program, ready to be fed into the
 * simulator
 * @author mbway
 */
public class Program {

    /**
     * statements of the program
     */
    public final Map<Address, Statement> textSegment;
    public Address textSegmentStart;
    public Address textSegmentLast; // address of the last instruction

    /**
     * metadata and type information about areas of the static data segment
     */
    public final Map<Address, Variable> dataSegmentVariables;
    public Address dataSegmentStart;

    /**
     * the initial state of the static data segment
     */
    public byte[] dataSegment;
    /**
     * the initial value of the break
     */
    public Address dynamicSegmentStart;

    public final Map<Label, Address> labels;
    public final Map<Address, Annotation> annotations;
    public Annotation initAnnotation;
    public final Map<Address, Integer> lineNumbers;

    /**
     * initial value for the global pointer
     */
    public Word initialGP;
    /**
     * initial value for the stack pointer
     */
    public Word initialSP;

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
        initAnnotation = null;
        lineNumbers = new HashMap<>();
        initialGP = null;
        initialSP = null;
        sourceHash = -1;
    }

}
