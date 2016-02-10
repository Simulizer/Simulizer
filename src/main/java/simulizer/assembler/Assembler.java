package simulizer.assembler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.*;
import simulizer.parser.SmallMipsLexer;
import simulizer.parser.SmallMipsParser;
import simulizer.simulation.data.representation.DataConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Assembler {
    public Program assemble(String input) {

        input += '\n'; // to parse correctly, must end with a newline

        SmallMipsLexer lexer = new SmallMipsLexer(new ANTLRInputStream(input));
        SmallMipsParser parser = new SmallMipsParser(new CommonTokenStream(lexer));

        // try to parse a program from the input
        SmallMipsParser.ProgramContext tree = parser.program();

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
                    p.labels.put(new Label(labelName, s.lineNumber, Label.Type.INSTRUCTION), address);
                }
            }

            p.textSegment.put(address, s);
            p.lineNumbers.put(address, s.lineNumber);

            address = new Address(address.getValue() + 4);
        }


        address = new Address(0x10000000); // (static) data segment

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

            byte[] data = DataConverter.fromVariable(v);
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

        p.dynamicSegmentStart = address;

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
}
