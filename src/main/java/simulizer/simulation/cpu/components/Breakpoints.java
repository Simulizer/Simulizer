package simulizer.simulation.cpu.components;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Program;
import simulizer.utils.DataUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A class to hold breakpoints
 */
public class Breakpoints {
    private static final Set<Address> breakpointAddresses = new HashSet<>();
    private static final List<Integer> breakpointLineNumbers = new ArrayList<>();
    private static final NavigableMap<Integer, Address> lineNums = new TreeMap<>();
    private static Program p = null;

    public static synchronized void clearBreakpoints() {
        breakpointAddresses.clear();
        breakpointLineNumbers.clear();
    }
    public static synchronized void addBreakpointLine(int line) {
        breakpointLineNumbers.add(line);
        if(p != null) { // has associated program
            breakpointAddresses.add(getAddressOfLine(line));
        }
    }

    public static synchronized void removeBreakpointLine(int line) {
        breakpointLineNumbers.remove(line);
        // note: if the program has changed since the breakpoint was added then the address will be different
        // and so probably won't be removed
        breakpointAddresses.remove(getAddressOfLine(line));
    }

    /**
     * the lineNums map is populated with the lowest address associated with each line of the program with instructions
     * on them. However if a line is requested that has no associated address: return the address associated with
     * the next highest line that does
     * @param line the line number to query
     */
    private static Address getAddressOfLine(int line) {
        if(p == null)
            throw new IllegalStateException("must specify a program to get address of line number");

        Map.Entry<Integer, Address> e = lineNums.ceilingEntry(line);
        if(e == null)
            return null;
        else
            return e.getValue();
    }

    /**
     * Specify the program object to use to perform the conversion from line numbers to addresses
     * specify a null program to stop using the program to convert breakpoints
     * @param p the program to use to convert line numbers to addresses
     */
    static synchronized void specifyProgram(Program p) {
        // object equality. if using the _exact_ same program then
        // keep the existing breakpoints. This should occur if no
        // changes are made in the editor due to caching of assembled programs
        if(p != null && p == Breakpoints.p) {
            // program has not changed: keep existing breakpoints
            return;
        }

        Breakpoints.p = p;
        lineNums.clear();

        if(p == null)
            return;

        // take only the smallest address for a given line
        // ie if multiple instructions are placed on the same line: break at the first one
        Map<Integer, List<Address>> linesToAddrList = DataUtils.reverseMapping(p.lineNumbers);
        for(Map.Entry<Integer, List<Address>> e : linesToAddrList.entrySet()) {
            Address smallest = null;
            for(Address a : e.getValue()) {
                if(smallest == null || a.getValue() < smallest.getValue()) {
                    smallest = a;
                }
            }
            lineNums.put(e.getKey(), smallest);
        }

        // any existing breakpoint addresses are now invalid because the program changed
        breakpointAddresses.clear();

        // convert all the buffered line numbers to actual addresses
        breakpointAddresses.addAll(breakpointLineNumbers.stream()
                .map(Breakpoints::getAddressOfLine)
                .collect(Collectors.toList()));
    }
    static synchronized boolean isBreakpoint(Address a) {
        if(p == null)
            throw new IllegalStateException("cannot query Breakpoints without specifying a program first!");

        return breakpointAddresses.contains(a);
    }
}
