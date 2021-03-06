package simulizer.assembler.representation;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.bind.DatatypeConverter;

import simulizer.Simulizer;
import simulizer.utils.FileUtils;
import simulizer.utils.StringUtils;

/**
 * dump a program object to a string
 * @author mbway
 */
public class ProgramStringBuilder {

    public static void dumpToFile(Program p, String filename) {
        Writer out = null;
        try {
            out = FileUtils.getUTF8FileWriter(filename);
            out.write(dumpToString(p));
        } catch(IOException e) {
            Simulizer.handleException(e);
        } finally {
            FileUtils.quietClose(out);
        }
    }

	private static String lineNumString(Integer i) {
		return "line:" + i.toString() + " (" + (i+1) + " in the editor)";
	}

    private static class DescriptiveStringBuilder {
		private final StringBuilder sb = new StringBuilder();

		@Override public String toString() {
			return sb.toString();
		}

		private DescriptiveStringBuilder append(Object o) {
			sb.append(o);
			return this;
		}
		private String giveName(Object o) {
			return o.getClass().getSimpleName() + "(" + o.toString() + ")";
		}

		private DescriptiveStringBuilder appendNamed(Object o) {
			return append(giveName(o));
		}



		public DescriptiveStringBuilder append(Address a) {
			return appendNamed(a);
		}
		public DescriptiveStringBuilder append(Annotation a) {
			return appendNamed(a);
		}
		public DescriptiveStringBuilder append(Label l) {
			return
			append("Label(").append(l.getName()).append("->").append(lineNumString(l.getLineNumber())).append(":")
					.append(l.getType()).append(")");
		}
		public DescriptiveStringBuilder append(Statement s) {
			String opString;
			if(s.getOperandList().isEmpty())
				opString = "no operands";
			else
				opString = s.getOperandList().stream()
					.map(this::giveName)
					.collect(Collectors.joining(", "));
			return append("Statement(Instruction(").append(giveName(s.getInstruction())).append("), ").append(opString).append(")");
		}
		public DescriptiveStringBuilder append(Variable v) {
			String initial = v.getInitialValue().isPresent() ? v.getInitialValue().get().toString() : "no initial value";
			return append("Variable(").append(v.getType()).append("(").append(v.getSize()).append(" bytes) : ").append(initial).append(")");
		}
	}

    private static String dumpToString(Program p) {
        DescriptiveStringBuilder sb = new DescriptiveStringBuilder();

        sb.append("# Misc Data #\n");
        sb.append("\tDynamic data segment (break) starting address: ").append(p.dynamicSegmentStart).append("\n");
        sb.append("\thash: ").append(p.sourceHash).append("\n");
        sb.append("\n\n");

        sb.append("# Text Segment starting: ").append(p.textSegmentStart).append(" #\n");
        {
            // make sure none are missed
            Set<Address> addresses = new HashSet<>(p.textSegment.keySet());
            {
                Address a = p.textSegmentStart;
                while(p.textSegment.containsKey(a)) {
                    sb.append("\t").append(a).append("\t").append(p.textSegment.get(a)).append("\n");
                    addresses.remove(a);
                    a = new Address(a.getValue() + 4);
                }
            }
            sb.append("\n\n");

            if(!addresses.isEmpty()) {
                sb.append("## Remaining Statements (problem: should be empty) ##\n");
                for(Address ad : addresses) {
                    sb.append("\t").append(ad).append("\t").append(p.textSegment.get(ad)).append("\n");
                }
                sb.append("\n\n");
            }
        }


        sb.append("# Data Segment starting: ").append(p.dataSegmentStart).append(" #\n");
        {
            // make sure none are missed
            Set<Address> addresses = new HashSet<>(p.dataSegmentVariables.keySet());
            {
                Address a = p.dataSegmentStart;
                while(p.dataSegmentVariables.containsKey(a)) {
                    Variable v = p.dataSegmentVariables.get(a);
                    sb.append("\t").append(a).append("\t").append(v).append("\n");
                    addresses.remove(a);
                    a = new Address(a.getValue() + v.getSize());
                }
            }
            sb.append("\n\n");

            if(!addresses.isEmpty()) {
                sb.append("## Remaining Variables (problem: should be empty) ##\n");
                for(Address ad : addresses) {
                    sb.append("\t").append(ad).append("\t").append(p.dataSegmentVariables.get(ad)).append("\n");
                }
                sb.append("\n\n");
            }

            sb.append("## Raw Data Segment (as hex) ##\n");
            // split hex into words
            sb.append(StringUtils.insert(DatatypeConverter.printHexBinary(p.dataSegment), " ", 4));

            sb.append("\n\n");
        }


        sb.append("# Labels #\n");
        {
            List<Map.Entry<Label, Address>> entries = new ArrayList<>(p.labels.entrySet());
            entries.sort((e1, e2) ->
                Integer.compare(e1.getValue().getValue(), e2.getValue().getValue()));

            for(Map.Entry<Label, Address> e : entries) {
                sb.append("\t").append(e.getKey().getName()).append(" --> ")
                    .append(e.getValue()).append(" : ").append(lineNumString(e.getKey().getLineNumber()))
                    .append(" (").append(e.getKey().getType()).append(")\n");
            }
            sb.append("\n\n");
        }

        sb.append("# Annotations #\n");
        {
            List<Map.Entry<Address, Annotation>> entries = new ArrayList<>(p.annotations.entrySet());
            entries.sort((e1, e2) ->
                    Integer.compare(e1.getKey().getValue(), e2.getKey().getValue()));

            for(Map.Entry<Address, Annotation> e : entries) {
                sb.append("\t").append(e.getKey()).append(" --> ").append(e.getValue()).append("\n");
            }
            sb.append("\n\n");
        }

        sb.append("# Line Numbers #\n");
        {
            List<Map.Entry<Address, Integer>> entries = new ArrayList<>(p.lineNumbers.entrySet());
            entries.sort((e1, e2) ->
                Integer.compare(e1.getValue(), e2.getValue()));

            for(Map.Entry<Address, Integer> e : entries) {
                sb.append("\t").append(e.getKey()).append(" --> ").append(lineNumString(e.getValue())).append("\n");
            }
            sb.append("\n\n");
        }

        return sb.toString();
    }
}
