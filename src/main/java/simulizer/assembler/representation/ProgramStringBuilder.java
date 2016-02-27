package simulizer.assembler.representation;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

/**
 * dump a program object to a string
 * @author mbway
 */
public class ProgramStringBuilder {

    public static void dumpToFile(Program p, String filename) {
        try {
            PrintWriter out = new PrintWriter(filename);
            out.print(dumpToString(p));
            out.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String dumpToString(Program p) {
        StringBuilder sb = new StringBuilder();

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
            sb.append(DatatypeConverter.printHexBinary(p.dataSegment));

            sb.append("\n\n");
        }


        sb.append("# Labels #\n");
        {
            List<Map.Entry<Label, Address>> entries = new ArrayList<>(p.labels.entrySet());
            entries.sort((e1, e2) ->
                Integer.compare(e1.getValue().getValue(), e2.getValue().getValue()));

            for(Map.Entry<Label, Address> e : entries) {
                sb.append("\t").append(e.getKey().getName()).append(" --> ")
                    .append(e.getValue()).append(" : ").append(e.getKey().getLineNumber())
                    .append(" (").append(e.getKey().getType()).append(")\n");
            }
            sb.append("\n\n");
        }

        sb.append("# Annotations #\n");
        {
            for(Map.Entry<Address, Annotation> e : p.annotations.entrySet()) {
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
                sb.append("\t").append(e.getKey()).append(" --> ").append(e.getValue()).append("\n");
            }
            sb.append("\n\n");
        }

        return sb.toString();
    }
}
