package simulizer.assembler.extractor;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;
import simulizer.assembler.extractor.problem.ImpossibleSituation;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.ProblemLogger;
import simulizer.assembler.extractor.problem.ValidityListener;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.*;
import simulizer.parser.SimpParser;
import simulizer.parser.SimpParser;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * A class to extract operands from assembler directives and program statements
 * from Simp source code. The extractor then packages the information into
 * Java objects for use in the simulation.
 */
public class OperandExtractor {

    /**
     * The logger to send encountered problems to
     */
    ProblemLogger log;

    /**
     * when accessing a sub-rule from a grammar rule, the result may be null
     * or it may be non-null but with an exception showing the encountered error
     * @param ctx the context to check
     * @return whether there was a good parse for the grammar rule
     */
    private boolean goodMatch(ParserRuleContext ctx) {
        return (ctx != null) && (ctx.exception == null) &&
            ValidityListener.goodChildren(ctx.children);
    }

    /**
     * whether a terminal node matches correctly
     * @param n the terminal node to test validity
     * @return whether the terminal node parsed correctly
     */
    private boolean goodMatch(TerminalNode n) {
        return n != null && !(n instanceof ErrorNode);
    }


    /**
     * @param log the problem logger to use
     */
    public OperandExtractor(ProblemLogger log) {
        this.log = log;
    }


    /**
     * Extract the operands from an assembler directive.
     * No sanity checks are performed. The operands can be any number and any type.
     *
     * @param ctx a valid parse of the directive list grammar rule to extract from
     * @return the extracted valid operands
     */
    public List<Operand> extractDirectiveOperands(SimpParser.DirectiveOperandListContext ctx) {
        List<Operand> operands = new ArrayList<>();

        if(!goodMatch(ctx)) {
            log.logParseError("directive operand list", ctx);
            return new ArrayList<>();
        }

        List<SimpParser.DirectiveOperandContext> operandContexts = ctx.directiveOperand();

        for(SimpParser.DirectiveOperandContext opCtx : operandContexts) {
            if(!goodMatch(opCtx)) {
                log.logParseError("directive operand", opCtx);
                continue;
            }

            if(goodMatch(opCtx.integer())) {
                operands.add(new IntegerOperand(extractInteger(opCtx.integer())));
            } else if(goodMatch(opCtx.string())) {
                operands.add(new StringOperand(extractString(opCtx.string())));
            } else if(goodMatch(opCtx.address())) {
                operands.add(extractAddress(opCtx.address()));
            } else {
                log.logProblem("invalid directive operand", opCtx);
            }
        }

        return operands;
    }

    /**
     * Extract the operands from a program statement (assembler operation)
     * No sanity checks are performed. The operands can be any number and any type.
     *
     * @param ctx a valid parse of the operand list grammar rule to extract from
     * @return the extracted valid operands
     */
    public List<Operand> extractStatementOperands(SimpParser.StatementOperandListContext ctx) {
        List<Operand> operands = new ArrayList<>();

        if(!goodMatch(ctx)) {
            log.logParseError("statement operand list", ctx);
            return new ArrayList<>();
        }

        List<SimpParser.StatementOperandContext> operandContexts = ctx.statementOperand();

        for(SimpParser.StatementOperandContext opCtx : operandContexts) {
            if(!goodMatch(opCtx)) {
                log.logParseError("statement operand", opCtx);
                continue;
            }

            if(goodMatch(opCtx.register())) {
                operands.add(new RegisterOperand(extractRegister(opCtx.register())));
            } else if(goodMatch(opCtx.integer())) {
                operands.add(new IntegerOperand(extractInteger(opCtx.integer())));
            } else if(goodMatch(opCtx.address())) {
                operands.add(extractAddress(opCtx.address()));
            } else {
                log.logProblem("invalid instruction operand", opCtx);
            }
        }

        return operands;
    }


    /**
     * Extract an integer (which may have a +/- sign and be decimal or hex with
     * an 0x prefix)
     * The integer must be within the range of a Java int (32 bit signed)
     * @param ctx a valid parse of the integer grammar rule to extract from
     * @return the extracted integer with the correct sign or 0 if a problem is
     *         encountered
     */
    public int extractInteger(SimpParser.IntegerContext ctx) {
        int abs = 0;

        if(!goodMatch(ctx)) {
            log.logParseError("integer", ctx);
            return 0;
        }

        try {
            if(goodMatch(ctx.decInt())) {
                abs = Integer.parseInt(ctx.decInt().DEC_INT().getText());
            } else if(goodMatch(ctx.hexInt())) {
                // decode uses the 0x to determine hex
                abs = Integer.decode(ctx.hexInt().HEX_INT().getText());
            } else {
                log.logParseError("integer", ctx);
            }
        } catch(NumberFormatException e) {
            log.logProblem("NumberFormatException while extracting an integer: \"" +
                ctx.getText() + "\". This value is probably too large to fit into a 32 bit integer.", ctx);
        }

        if(ctx.SIGN() == null || ctx.SIGN().getText().equals("+")) { // positive
            return abs;
        } else { // negative
            return -abs;
        }
    }

    /**
     * Extract an integer without a +/- sign and may be decimal or hex with an
     * 0x prefix
     * The integer must be within the range of a Java int (32 bit signed)
     * @param ctx a valid parse of the unsigned integer grammar rule to extract from
     * @return the extracted integer or 0 if a problem is encountered
     */
    public int extractUnsignedInteger(SimpParser.UnsignedIntegerContext ctx) {
        int abs = 0;

        if(!goodMatch(ctx)) {
            log.logParseError("unsigned integer", ctx);
            return 0;
        }

        try {
            if(goodMatch(ctx.decInt())) {
                abs = Integer.parseInt(ctx.decInt().DEC_INT().getText());
            } else if(goodMatch(ctx.hexInt())) {
                // decode uses the 0x to determine hex
                abs = Integer.decode(ctx.hexInt().HEX_INT().getText());
            } else {
                log.logParseError("unsigned integer", ctx);
            }
        } catch(NumberFormatException e) {
            log.logProblem("NumberFormatException while extracting an unsigned integer: \"" +
                ctx.getText() + "\". This value is probably too large to fit into a 32 bit integer.", ctx);
        }

        return abs;
    }


    /**
     * Extract a string with the following escaped characters supported:
     * - \t     tab
     * - \n     newline
     * - \"     "
     * - \\     \
     * - \nnn   octal ASCII character code (1-3 digits)
     * - \xhh   hexadecimal ASCII character code (2 digits)
     *
     * @param ctx a valid parse of the string to extract from
     * @return the extracted string or null if a problem is encountered
     */
    public String extractString(SimpParser.StringContext ctx) {

        if(!goodMatch(ctx) || !goodMatch(ctx.STRING_LITERAL())) {
            log.logParseError("string", ctx);
            return null;
        }

        String s = ctx.getText();
        s = s.substring(1, s.length()-1); // strip " from both ends

        // SPIM escaping code here: $SPIM/CPU/scanner.l
        // look at copy_str NOT scan_escape because the escape codes in
        // scan_escape are NOT actually supported by the string literals
        // of the assembly source code.
        // follows the c escape convention: https://en.wikipedia.org/wiki/Escape_sequences_in_C#Table_of_escape_sequences
        // java escape codes: https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.10.6

        // differences from spim:
        // correctly parses \nnn as octal (spim thinks \100 is actually \010)
        // allows 'X' or 'x' for denoting hex, while spim only allows 'X'


        StringBuilder sb = new StringBuilder();
        boolean problem = false;

        for(int i = 0; i < s.length(); i++) {
            if(s.charAt(i) != '\\') {
                sb.append(s.charAt(i));
            } else {
                // backslash entered

                if(i+1 >= s.length()) {
                    // last character is \\
                    problem = true;
                    break;
                }

                char c = s.charAt(i + 1);
                i++; // read 1 char past \

                String intString = ""; // a string to be parsed as an int
                int parseBase = 0; // the base to treat the string as

                switch(c) {
                    case 't':
                        sb.append('\t');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7': {
                        // see: http://udojava.com/2013/09/28/unescape-a-string-that-contains-standard-java-escape-sequences/
                        int extraDigits = 0;
                        for(int numSearch = i+1; numSearch < s.length() && numSearch <= i + 2; numSearch++) {
                            char c2 = s.charAt(numSearch);
                            if(c2 >= '0' && c2 <= '7') {
                                extraDigits++;
                            } else {
                                break;
                            }
                        }
                        intString = s.substring(i, (i + extraDigits) + 1);
                        parseBase = 8;
                        i += extraDigits; // first digit already counted
                        break;
                    }
                    case 'x':
                    case 'X': {
                        if(i + 2 < s.length()) {
                            intString = s.substring(i + 1, (i + 2) + 1);
                            parseBase = 16;
                        } else {
                            problem = true;
                            break;
                        }
                        i += 2; // 'x' already counted
                        break;
                    }
                    default:
                        problem = true;
                        break;
                }

                if(problem) {
                    break;
                }

                if(intString.length() != 0) {
                    try {
                        int v = Integer.parseInt(intString, parseBase);

                        if(v < 256) {
                            sb.append((char) v);
                        } else if(v >= 256 && parseBase == 8 && intString.length() == 3) {
                            // re-try parse with only 2 octal digits
                            v = Integer.parseInt(intString.substring(0, 2), 8);
                            sb.append((char) v);

                            // re-consider the last digit as literal
                            // and not part of the escape sequence
                            i--;
                        }
                        else {
                            problem = true;
                            break;
                        }
                    } catch(NumberFormatException ignored) {
                        problem = true;
                        break;
                    }
                }
            }
        }

        if(problem) {
            log.logProblem(
                "string contains an invalid escape sequence, must be one of:" +
                    "\\\\,\\n,\\t,\\\",\\nnn,\\xhh " +
                    "where nnn is an octal integer (1-3 digits) < 377 (255 in base 10) "+
                    "and hh is a hexadecimal integer", ctx);
            return null;
        }

        return sb.toString();
    }

    /**
     * extract an address of one of the following forms:
     *
     * ($register)
     * +/- integer ($register)
     * label
     * label +/- integer
     * label +/- integer ($register)
     *
     * @param ctx a valid parse of the address grammar rule to extract from
     * @return the extracted address or null if a problem is encountered
     */
    public AddressOperand extractAddress(SimpParser.AddressContext ctx) {

        if(!goodMatch(ctx)) {
            log.logParseError("address", ctx);
            return null;
        }

        AddressOperand a = new AddressOperand();

        if(goodMatch(ctx.labelID())) {
            a.labelName = Optional.of(ctx.labelID().IDENTIFIER().getText());
        }

        if(goodMatch(ctx.integer())) {
            // case of "integer ($register)"
            // integer on its own is not allowed because it may be negative
            if(goodMatch(ctx.SIGN()) || goodMatch(ctx.unsignedInteger())
                || !goodMatch(ctx.baseAddress())) {
                log.logParseError("address", ctx);
                return null;
            }

            int x = extractInteger(ctx.integer());
            a.constant = Optional.of(x);
        } else {
            // case of "+/- offset"

            if(goodMatch(ctx.unsignedInteger())) {
                int x = extractUnsignedInteger(ctx.unsignedInteger());
                a.constant = Optional.of(x);
            }

            if(goodMatch(ctx.SIGN())) {
                if(!a.constant.isPresent()) {
                    // must have a constant if a sign is included because the sign
                    // is attached to the constant and is lost otherwise
                    log.logParseError("address", ctx);
                    return null;
                }
                if(ctx.SIGN().getText().equals("-")) { // negative otherwise positive
                    int x = a.constant.get();
                    a.constant = Optional.of(-x);
                }
            }
        }

        if(goodMatch(ctx.baseAddress()) && goodMatch(ctx.baseAddress().register())) {
            Register r = extractRegister(ctx.baseAddress().register());
            if(r != null) {
                a.register = Optional.of(r);
            } else {
                log.logParseError("address", ctx); // propagate error
                return null;
            }
        }

        // I don't think this case is reachable because the grammar
        // doesn't match the situation that would produce this.
        if(a.labelName.isPresent() && !a.constant.isPresent() && a.register.isPresent()) {
            // "label ($register)" not allowed
            log.logParseError("address", ctx);
            return null;
        }

        if(a.labelName.isPresent() || a.constant.isPresent() || a.register.isPresent()) {
            return a;
        } else {
            log.logParseError("address", ctx); // parse error because nothing matched
            return null;
        }

    }

    /**
     * extract a register from a string "$ID" where ID is a valid register name
     * or numeric ID (as a decimal number)
     * @see simulizer.assembler.representation.Register
     * @param ctx a valid parse of the register grammar rule to extract from
     * @return the extracted register or null if a problem is encountered
     */
    public Register extractRegister(SimpParser.RegisterContext ctx) {

        if(!goodMatch(ctx)) {
            log.logParseError("register", ctx);
            return null;
        }

        try {
            if(goodMatch(ctx.NAMED_REGISTER())) {
                // strip leading $
                String name = ctx.NAMED_REGISTER().getText().substring(1);
                return Register.fromString(name);

            } else if(goodMatch(ctx.NUMBERED_REGISTER())) {
                // strip leading $
                String ID = ctx.NUMBERED_REGISTER().getText().substring(1);
                return Register.fromID(Integer.parseInt(ID));

            } else {
                log.logParseError("register", ctx);
            }
        } catch (NoSuchElementException | NumberFormatException e) {
            log.logProblem("no such register: \"" + ctx.getText() + "\"", ctx);
        }

        return null;
    }
}
