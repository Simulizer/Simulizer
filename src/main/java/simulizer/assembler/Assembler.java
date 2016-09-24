package simulizer.assembler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.*;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.ProblemCountLogger;
import simulizer.assembler.extractor.problem.ProblemLogger;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.*;
import simulizer.assembler.representation.operand.Operand;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;
import simulizer.simulation.data.representation.DataConverter;
import simulizer.simulation.data.representation.Word;
import simulizer.utils.DataUtils;

/**
 * Assemble SIMP source code into a form that is consumable be the simulation.
 * Also provide information about
 * @author mbway
 */
public class Assembler {
    /**
     * Performs the first stage of assembling a program. But stops once it
     * determines whether the program is valid or not. This is useful when only
     * the validity of the program needs to be known.
     * @param input the program string to assemble
     * @return any problems with the program (empty list if program valid)
     */
    public static List<Problem> checkForProblems(String input) {
        StoreProblemLogger log = new StoreProblemLogger();

        input += '\n'; // to parse correctly, must end with a newline

        SimpLexer lexer = new SimpLexer(new ANTLRInputStream(input));
        SimpParser parser = new SimpParser(new CommonTokenStream(lexer));

        // prevent outputting to the console
        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        // try to parse a program from the input
        SimpParser.ProgramContext tree = parser.program();

        ProgramExtractor extractor = new ProgramExtractor(log);
        ParseTreeWalker.DEFAULT.walk(extractor, tree);

        return log.getProblems();
    }

    /**
     * Assemble a problem and catch any problem output
     * @param input the program string to assemble
     * @param log the logger to send the error messages (may be null)
     * @return the assembled program (or null if errors are encountered)
     */
    public static Program assemble(String input, ProblemLogger log, boolean permissive) {

        input += '\n'; // to parse correctly, must end with a newline

        SimpLexer lexer = new SimpLexer(new ANTLRInputStream(input));
        SimpParser parser = new SimpParser(new CommonTokenStream(lexer));

        // prevent outputting to the console
        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        // try to parse a program from the input
        SimpParser.ProgramContext tree = parser.program();

        ProblemCountLogger counter = new ProblemCountLogger(log);
        ProgramExtractor extractor = new ProgramExtractor(counter);
        ParseTreeWalker.DEFAULT.walk(extractor, tree);

        if(permissive) {
            if(counter.criticalCount > 0) {
                return null;
            }
        } else {
            if(counter.problemCount > 0) {
                return null;
            }
        }

        Map<Integer, List<String>> reverseTextLabels = DataUtils.reverseMapping(extractor.textSegmentLabels);
        Map<Integer, List<String>> reverseDataLabels = DataUtils.reverseMapping(extractor.dataSegmentLabels);

        Program p = new Program();


        p.sourceHash = input.hashCode();

        Address address = new Address(0x00400000); // text segment offset

        p.textSegmentStart = address;

        if(!extractor.initAnnotationCode.isEmpty()) {
            p.initAnnotation = new Annotation(extractor.initAnnotationCode);
        }

        for(int i = 0; i < extractor.textSegment.size(); i++) {
            Statement s = extractor.textSegment.get(i);

            if(reverseTextLabels.containsKey(i)) {
                for(String labelName : reverseTextLabels.get(i)) {
                    p.labels.put(new Label(labelName, s.getLineNumber(), Label.Type.INSTRUCTION), address);
                }
            }

            if(extractor.annotations.containsKey(i)) {
                p.annotations.put(address, new Annotation(extractor.annotations.get(i)));
            }

            p.textSegment.put(address, s);
            p.lineNumbers.put(address, s.getLineNumber());

            address = new Address(address.getValue() + 4);
        }
        p.textSegmentLast = new Address(address.getValue() - 4);


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

            // Antlr line numbers start from 1
            // the convention in simulizer is to start from 0
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


    private static byte[] variableInitialBytes(Variable v) {
        Optional<Operand> operand = v.getInitialValue();
        Operand op;
        if(!operand.isPresent()) {
            return new byte[v.getSize()];
        } else {
            op = operand.get();
        }

        switch(v.getType()) {
            case Byte: {
                int val = op.asIntegerOp().value;
                return new byte[]{(byte) val};
            }
            case Half: {
                int val = op.asIntegerOp().value;
                return new byte[]{
                    (byte)((val >> 8) & 0xFF),
                    (byte)(val & 0xFF)
                };
            }
            case Word: {
                int val = op.asIntegerOp().value;
                return ByteBuffer.allocate(4).putInt(val).array();
            }
            case ASCII:
            case ASCIIZ: {
                // null terminator was added earlier so these are equivalent
                String val = op.asStringOp().value;
                return val.getBytes(Charset.forName("US-ASCII"));
            }
            case Space:
                return new byte[v.getSize()];
            default:
                throw new IllegalArgumentException();
        }
    }
}
