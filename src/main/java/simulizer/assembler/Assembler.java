package simulizer.assembler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.*;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Assembler {
    public Program assemble(String input) {

        input += '\n'; // to parse correctly, must end with a newline

        SimpLexer lexer = new SimpLexer(new ANTLRInputStream(input));
        SimpParser parser = new SimpParser(new CommonTokenStream(lexer));

        // try to parse a program from the input
        SimpParser.ProgramContext tree = parser.program();

        StoreProblemLogger log = new StoreProblemLogger();
        ProgramExtractor extractor = new ProgramExtractor(log);
        ParseTreeWalker.DEFAULT.walk(extractor, tree);

        if(!log.getProblems().isEmpty()) {
            return null;
        }

        Map<Integer, List<String>> reverseTextLabels = reverseMapping(extractor.textSegmentLabels);
        Map<Integer, List<String>> reverseDataLabels = reverseMapping(extractor.dataSegmentLabels);

        Program p = new Program();


        p.sourceHash = input.hashCode();

        Address address = new Address(0x00400000); // text segment offset

        p.textSegmentStart = address;

        for(int i = 0; i < extractor.textSegment.size(); i++) {
            Statement s = extractor.textSegment.get(i);

            if(reverseTextLabels.containsKey(i)) {
                for(String labelName : reverseTextLabels.get(i)) {
                    p.labels.put(new Label(labelName, s.getLineNumber(), Label.Type.INSTRUCTION), address);
                }
            }

            p.textSegment.put(address, s);
            p.lineNumbers.put(address, s.getLineNumber());

            address = new Address(address.getValue() + 4);
        }


        address = new Address(0x10010000); // (static) data segment skip over the 64KB .extern segment
        p.dataSegmentStart = address;

        List<Byte> tmpDataSegment = new ArrayList<>();

        for(int i = 0; i < extractor.dataSegment.size(); i++) {
            Variable v = extractor.dataSegment.get(i);

            if(reverseDataLabels.containsKey(i)) {
                for(String labelName : reverseDataLabels.get(i)) {
                    p.labels.put(new Label(labelName, v.getLineNumber(), Label.Type.VARIABLE), address);
                }
            }

            p.dataSegmentVariables.put(address, v);

            byte[] data = variableInitialBytes(v);
            assert data.length == v.getSize();

            for(byte b : data) {
                tmpDataSegment.add(b);
            }

            p.lineNumbers.put(address, v.getLineNumber());

            address = new Address(address.getValue() + v.getSize());
        }

        p.dataSegment = new byte[tmpDataSegment.size()];
        for(int i = 0; i < p.dataSegment.length; i++) {
            p.dataSegment[i] = tmpDataSegment.get(i);
        }

        p.dynamicSegmentStart = new Address(0x10040000); // start of the dynamic data segment

        p.initialGP = new Word(DataConverter.encodeAsUnsigned(0x10008000));
        // found by examining spim
        p.initialSP = new Word(DataConverter.encodeAsUnsigned(0x7ffff3c8));

        return p;
    }


    public static <K, V> Map<V, List<K>> reverseMapping(Map<K, V> map) {
        Map<V, List<K>> rev = new HashMap<>();

        for(Map.Entry<K, V> e : map.entrySet()) {
            V v = e.getValue();

            if(!rev.containsKey(v)) {
                rev.put(v, new ArrayList<>());
            }

            rev.get(v).add(e.getKey());
        }

        return rev;
    }

    private static byte[] variableInitialBytes(Variable v) {
        if(!v.getInitialValue().isPresent()) {
            return new byte[v.getSize()];
        }
        switch(v.getType()) {
            case Byte: {
                int val = v.getInitialValue().get().asIntegerOp().value;
                return new byte[]{(byte) val};
            }
            case Half: {
                int val = v.getInitialValue().get().asIntegerOp().value;
                return new byte[]{
                    (byte)((val >> 8) & 0xFF),
                    (byte)(val & 0xFF)
                };
            }
            case Word: {
                int val = v.getInitialValue().get().asIntegerOp().value;
                return ByteBuffer.allocate(4).putInt(val).array();
            }
            case ASCII:
            case ASCIIZ: {
                // null terminator was added earlier so these are equivalent
                String val = v.getInitialValue().get().asStringOp().value;
                return val.getBytes(Charset.forName("US-ASCII"));
            }
            case Space:
                return new byte[v.getSize()];
            default:
                throw new IllegalArgumentException();
        }
    }
}
