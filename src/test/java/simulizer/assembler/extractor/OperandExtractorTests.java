package simulizer.assembler.extractor;

import category.UnitTests;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.*;

import org.junit.rules.ExpectedException;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Register;
import simulizer.assembler.representation.operand.AddressOperand;
import simulizer.assembler.representation.operand.Operand;
import simulizer.parser.SimpParser;

import java.util.List;
import java.util.Optional;


/**
 * Test the OperandExtractor class
 * @author mbway
 */
@Category({UnitTests.class})
public class OperandExtractorTests {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private StoreProblemLogger log;
    private OperandExtractor ex;

    private Parser parserDoNotUseDirectly;
    private SimpParser parse(String s) {
        parserDoNotUseDirectly = new Parser();
        return parserDoNotUseDirectly.parse(s);
    }
    private List<String> getParserErrors() {
        return parserDoNotUseDirectly.getErrors();
    }
    private ParserRuleContext giveParseException(ParserRuleContext ctx) {
        ctx.exception = new InputMismatchException(parserDoNotUseDirectly.p);
        return ctx;
    }


    private void expectGood() {
        assertTrue(getParserErrors().isEmpty());
        assertTrue(log.getProblems().isEmpty());
        log.getProblems().clear();
    }
    private void expectValidParseButProblem(String messageContains) {
        assertTrue(getParserErrors().isEmpty());
        assertFalse(log.getProblems().isEmpty());

        assertTrue(log.getProblems().stream().anyMatch(pr -> pr.message.contains(messageContains)));
        log.getProblems().clear();
    }
    private void expectBadParse(String messageContains) {
        assertFalse(getParserErrors().isEmpty());
        assertFalse(log.getProblems().isEmpty());

        assertTrue(log.getProblems().stream().anyMatch(pr -> pr.message.contains(messageContains)));
        log.getProblems().clear();
    }

    private void extractGoodInteger(int value, String valueString) {
        assertEquals(value, ex.extractInteger(parse(valueString).integer()));
        expectGood();
    }

    @Test
    public void testExtractInteger() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // Test good decimals
        extractGoodInteger(123, "123");
        extractGoodInteger(123, "0123");
        extractGoodInteger(123, "+123");
        extractGoodInteger(123, "+ 123");
        extractGoodInteger(123, "+ \t123");
        extractGoodInteger(1230, "+ \t0001230");

        extractGoodInteger(-123, "-123");
        extractGoodInteger(-123, "-0123");
        extractGoodInteger(-123, "- 123");
        extractGoodInteger(-123, "- \t123");
        extractGoodInteger(-1230, "- \t0001230");

        // Test Hex
        extractGoodInteger(0xAB51, "0xAB51");
        extractGoodInteger(0xAB51, "+0xAB51");
        extractGoodInteger(0xAB510, "+0x00AB510");
        extractGoodInteger(0x00AB510, "+0x00AB510");

        extractGoodInteger(0xAB51, "0xab51");
        extractGoodInteger(0xAB51, "+0xab51");
        extractGoodInteger(0xAB51, "+ 0xab51");
        extractGoodInteger(0xAB51, "+ \t0xab51");

        extractGoodInteger(-0xAB51, "-0xab51");
        extractGoodInteger(-0xAB51, "- 0xab51");
        extractGoodInteger(-0xAB51, "- \t0xab51");

        // capital 0X: good
        extractGoodInteger(0xAB51, "0XAB51");
        extractGoodInteger(0xAB51, "+ \t0XAB51");
        extractGoodInteger(-0xAB51, "-0XAB51");
        extractGoodInteger(-0xAB51, "- \t0XAB51");
        extractGoodInteger(0xAB51, "0Xab51");
        extractGoodInteger(-0xAB51, "-0Xab51");
        extractGoodInteger(-0xAB51, "- \t0Xab51");


        // Test Limits

        // MAX_VALUE: good
        extractGoodInteger(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE));
        extractGoodInteger(Integer.MAX_VALUE, "0x" + Integer.toString(Integer.MAX_VALUE, 16));

        // MIN_VALUE not parsable with parseInt, testing MIN_VALUE+1
        extractGoodInteger(Integer.MIN_VALUE+1, Integer.toString(Integer.MIN_VALUE+1));
        extractGoodInteger(Integer.MIN_VALUE+1, "-0x" + Integer.toString(-(Integer.MIN_VALUE+1), 16));


        // 2^33: bad
        assertEquals(0, ex.extractInteger(parse("8589934592").integer()));
        expectValidParseButProblem("too large");

        // 2^33 as hex: bad
        assertEquals(0, ex.extractInteger(parse("0x200000000").integer()));
        expectValidParseButProblem("too large");

        // -2^33: bad
        assertEquals(0, ex.extractInteger(parse("-8589934592").integer()));
        expectValidParseButProblem("too large");

        // -2^33 as hex: bad
        assertEquals(0, ex.extractInteger(parse("-0x200000000").integer()));
        expectValidParseButProblem("too large");


        // Should be a bad parse but would catch elsewhere in the parse
        assertEquals(-0xdef, ex.extractInteger(parse("-0xDEFGH").integer()));
        expectGood();

        // Bad parses
        assertEquals(0, ex.extractInteger(null));
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertEquals(0, ex.extractInteger(parse("").integer()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractInteger((SimpParser.IntegerContext)
            giveParseException(parse("123").integer())));
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");

        assertEquals(0, ex.extractInteger(parse("-\n123").integer()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractInteger(parse("ABC").integer()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractInteger(parse("0x ABC").integer()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractInteger(parse("-0x ABC").integer()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractInteger(parse("+0x ABC").integer()));
        expectBadParse("invalid parse");
    }


    private void extractGoodUnsignedInteger(int value, String valueString) {
        assertEquals(value, ex.extractUnsignedInteger(parse(valueString).unsignedInteger()));
        expectGood();
    }

    @Test
    public void testExtractUnsignedInteger() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // no sign allowed
        extractGoodUnsignedInteger(123, "123");
        extractGoodUnsignedInteger(123, "00123");
        extractGoodUnsignedInteger(1230, "001230");

        // Test Hex
        extractGoodUnsignedInteger(0xAB51, "0xAB51");
        extractGoodUnsignedInteger(0xAB51, "0xab51");
        extractGoodUnsignedInteger(0xAB51, "0x00ab51");

        // capital 0X: good
        extractGoodUnsignedInteger(0xAB51, "0XAB51");
        extractGoodUnsignedInteger(0xAB51, "0Xab51");
        extractGoodUnsignedInteger(0xAB51, "0X00ab51");


        // Test Limits

        // MAX_VALUE: good
        extractGoodUnsignedInteger(Integer.MAX_VALUE, Integer.toString(Integer.MAX_VALUE));
        extractGoodUnsignedInteger(Integer.MAX_VALUE, "0x" + Integer.toString(Integer.MAX_VALUE, 16));

        // zero
        extractGoodUnsignedInteger(0, "0");
        extractGoodUnsignedInteger(0, "000");
        extractGoodUnsignedInteger(0, "0x0");
        extractGoodUnsignedInteger(0, "0x000");
        extractGoodUnsignedInteger(0, "0X0");
        extractGoodUnsignedInteger(0, "0X000");


        // 2^33: bad
        assertEquals(0, ex.extractUnsignedInteger(parse("8589934592").unsignedInteger()));
        expectValidParseButProblem("too large");

        // 2^33 as hex: bad
        assertEquals(0, ex.extractUnsignedInteger(parse("0x200000000").unsignedInteger()));
        expectValidParseButProblem("too large");

        // bad parse
        assertEquals(0, ex.extractUnsignedInteger(null));
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertEquals(0, ex.extractUnsignedInteger(parse("").unsignedInteger()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractUnsignedInteger((SimpParser.UnsignedIntegerContext)
            giveParseException(parse("123").unsignedInteger())));
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");

        assertEquals(0, ex.extractUnsignedInteger(parse("0x ABC").unsignedInteger()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractUnsignedInteger(parse("+123").unsignedInteger()));
        expectBadParse("invalid parse");

        assertEquals(0, ex.extractUnsignedInteger(parse("-123").unsignedInteger()));
        expectBadParse("invalid parse");

    }

    private void expectAddress(Optional<String> label, Optional<Integer> constant, Optional<Register> register, AddressOperand addr) {
        assertEquals(label, addr.labelName);
        assertEquals(constant, addr.constant);
        assertEquals(register, addr.register);
    }



    @Test
    public void testExtractAddress() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        {
            AddressOperand addr = ex.extractAddress(parse("mylabel").address());
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(), addr);
            assertTrue(addr.labelName.isPresent());
            assertFalse(addr.constant.isPresent());
            assertFalse(addr.register.isPresent());
            assertTrue(addr.labelOnly());
            expectGood();
        }

        {
            AddressOperand addr = ex.extractAddress(parse("mylabel + 10").address());
            expectAddress(Optional.of("mylabel"), Optional.of(10), Optional.empty(), addr);
            assertTrue(addr.labelName.isPresent());
            assertTrue(addr.constant.isPresent());
            assertFalse(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }

        {
            AddressOperand addr = ex.extractAddress(parse("( $s0 )").address());
            expectAddress(Optional.empty(), Optional.empty(), Optional.of(Register.s0), addr);
            assertFalse(addr.labelName.isPresent());
            assertFalse(addr.constant.isPresent());
            assertTrue(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }

        {
            AddressOperand addr = ex.extractAddress(parse("mylabel + 0x15($zero)").address());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.zero), addr);
            assertTrue(addr.labelName.isPresent());
            assertTrue(addr.constant.isPresent());
            assertTrue(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }

        {
            AddressOperand addr = ex.extractAddress(parse("0x15($zero)").address());
            expectAddress(Optional.empty(), Optional.of(0x15), Optional.of(Register.zero), addr);
            assertFalse(addr.labelName.isPresent());
            assertTrue(addr.constant.isPresent());
            assertTrue(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }
        {
            AddressOperand addr = ex.extractAddress(parse("-0x15($zero)").address());
            expectAddress(Optional.empty(), Optional.of(-0x15), Optional.of(Register.zero), addr);
            assertFalse(addr.labelName.isPresent());
            assertTrue(addr.constant.isPresent());
            assertTrue(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }

        {
            AddressOperand addr = ex.extractAddress(parse("mylabel - 0x15 ( $zero )").address());
            expectAddress(Optional.of("mylabel"), Optional.of(-0x15), Optional.of(Register.zero), addr);
            assertTrue(addr.labelName.isPresent());
            assertTrue(addr.constant.isPresent());
            assertTrue(addr.register.isPresent());
            assertFalse(addr.labelOnly());
            expectGood();
        }


        // bad parses

        assertEquals(null, ex.extractAddress(null));
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertEquals(null, ex.extractAddress(parse("").address()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractAddress((SimpParser.AddressContext)
            giveParseException(parse("mylabel").address())));
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");


        {
            // parses as an integer instead
            AddressOperand addr = ex.extractAddress(parse("10").address());
            assertEquals(null, addr);
            expectBadParse("invalid parse");
        }
        {
            // parses as an integer instead
            AddressOperand addr = ex.extractAddress(parse("-10").address());
            assertEquals(null, addr);
            expectBadParse("invalid parse");
        }
        {
            // parses as an integer instead
            AddressOperand addr = ex.extractAddress(parse("10 ( )").address());
            assertEquals(null, addr);
            expectBadParse("invalid parse");
        }
        {
            // must have a constant if a sign is included because the sign
            // is attached to the constant and is lost otherwise
            // the mylabel parses correctly, but the remaining tokens do not
            {
                AddressOperand addr = ex.extractAddress(parse("mylabel + ($s0)").address());
                expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(), addr);
                expectGood();
            }
            {
                AddressOperand addr = ex.extractAddress(parse("+ ($s0)").address());
                assertEquals(null, addr);
                expectBadParse("invalid parse");
            }
            {
                AddressOperand addr = ex.extractAddress(parse("+").address());
                assertEquals(null, addr);
                expectBadParse("invalid parse");
            }
        }
        {
            // must have a constant if a sign is included because the sign
            // is attached to the constant and is lost otherwise
            // the mylabel parses correctly, but the remaining tokens do not
            {
                AddressOperand addr = ex.extractAddress(parse("mylabel - ($s0)").address());
                expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(), addr);
                expectGood();
            }
            {
                AddressOperand addr = ex.extractAddress(parse("- ($s0)").address());
                assertEquals(null, addr);
                expectBadParse("invalid parse");
            }
        }
        {
            // must declare a sign and a constant too
            // Should be a bad parse but would catch elsewhere in the parse
            AddressOperand addr = ex.extractAddress(parse("mylabel ($s0)").address());
            // ($s0) not included in the parse
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(), addr);
            expectGood();
        }
    }

    @Test
    public void testExtractString() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // test string with no escapes
        assertEquals("hello world", ex.extractString(parse("\"hello world\"").string()));
        expectGood();
        assertEquals(".directive label: mylabel + 2($abc)", ex.extractString(parse("\".directive label: mylabel + 2($abc)\"").string()));
        expectGood();
        assertEquals("", ex.extractString(parse("\"\"").string()));
        expectGood();

        // test escapes
        assertEquals("\t", ex.extractString(parse("\"\\t\"").string())); expectGood();
        assertEquals("\n", ex.extractString(parse("\"\\n\"").string())); expectGood();
        assertEquals("\"", ex.extractString(parse("\"\\\"\"").string())); expectGood();
        assertEquals("\\", ex.extractString(parse("\"\\\\\"").string())); expectGood();
        assertEquals("\0", ex.extractString(parse("\"\\0\"").string())); expectGood();
        assertEquals("\123", ex.extractString(parse("\"\\123\"").string())); expectGood();
        assertEquals("\253", ex.extractString(parse("\"\\xab\"").string())); expectGood();
        assertEquals("\u00ab", ex.extractString(parse("\"\\xab\"").string())); expectGood();

        // test escapes with surrounding text
        assertEquals("abc\tdef", ex.extractString(parse("\"abc\\tdef\"").string())); expectGood();
        assertEquals("abc\ndef", ex.extractString(parse("\"abc\\ndef\"").string())); expectGood();
        assertEquals("abc\"def", ex.extractString(parse("\"abc\\\"def\"").string())); expectGood();
        assertEquals("abc\\def", ex.extractString(parse("\"abc\\\\def\"").string())); expectGood();
        assertEquals("abc\\\\def", ex.extractString(parse("\"abc\\\\\\\\def\"").string())); expectGood();
        assertEquals("abc\0def", ex.extractString(parse("\"abc\\0def\"").string())); expectGood();
        assertEquals("abc\123def", ex.extractString(parse("\"abc\\123def\"").string())); expectGood();
        assertEquals("abc\253def", ex.extractString(parse("\"abc\\xabdef\"").string())); expectGood();
        assertEquals("abc\u00abdef", ex.extractString(parse("\"abc\\xabdef\"").string())); expectGood();

        // max allowed octal
        assertEquals("abc\377def", ex.extractString(parse("\"abc\\377def\"").string())); expectGood();
        // interpreted as two octal digits for the escape sequence
        // followed by a the last digit literally because \477 >= 256 (decimal)
        assertEquals("abc\477def", ex.extractString(parse("\"abc\\477def\"").string())); expectGood();
        assertEquals("abc\400def", ex.extractString(parse("\"abc\\400def\"").string())); expectGood();
        // interpreted as \012 followed by a literal '3' because the
        // digits have a fixed 3 character limit
        assertEquals("abc\0123def", ex.extractString(parse("\"abc\\0123def\"").string())); expectGood();


        // test bad parses

        assertEquals(null, ex.extractString(null));
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertEquals(null, ex.extractString(parse("").string()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractString((SimpParser.StringContext)
            giveParseException(parse("\"abc\"").string())));
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");


        assertEquals(null, ex.extractString(parse("").string()));
        expectBadParse("invalid parse");
        assertEquals(null, ex.extractString(parse("abc").string()));
        expectBadParse("invalid parse");

        // no ending "
        assertEquals(null, ex.extractString(parse("\"abc").string()));
        expectBadParse("invalid parse");
        // ending " on next line
        assertEquals(null, ex.extractString(parse("\"abc\n\"").string()));
        expectBadParse("invalid parse");

        // lone backslash
        // bad parse because ending " is escaped
        assertEquals(null, ex.extractString(parse("\"\\\"").string()));
        expectBadParse("invalid parse");
        // good parse from grammar point of view
        assertEquals(null, ex.extractString(parse("\"abc\\def\"").string()));
        expectValidParseButProblem("invalid escape sequence");

        assertEquals(null, ex.extractString(parse("\"abc\\\\\\def\"").string()));
        expectValidParseButProblem("invalid escape sequence");

        assertEquals(null, ex.extractString(parse("\"abc\\999def\"").string()));
        expectValidParseButProblem("invalid escape sequence");

        assertEquals(null, ex.extractString(parse("\"abc\\'def\"").string()));
        expectValidParseButProblem("invalid escape sequence");
    }


    private void extractGoodRegister(Register expected, String rString) {
        assertEquals(expected, ex.extractRegister(parse(rString).register()));
        expectGood();
    }

    @Test
    public void testExtractRegister() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // test good
        for(Register r : Register.values()) {
            extractGoodRegister(r, "$" + r.getName());
            extractGoodRegister(r, "$" + r.getID());
        }

        // leading zeroes are allowed
        assertEquals(Register.fromID(0), ex.extractRegister(parse("$000").register()));
        expectGood();

        assertEquals(Register.fromID(10), ex.extractRegister(parse("$0010").register()));
        expectGood();

        // test bad
        assertEquals(null, ex.extractRegister(parse("$abc").register()));
        expectValidParseButProblem("no such register");

        assertEquals(null, ex.extractRegister(parse("$100").register()));
        expectValidParseButProblem("no such register");

        // should be bad parse but would catch elsewhere in the parse
        assertEquals(Register.fromID(0), ex.extractRegister(parse("$0x2").register()));
        expectGood();


        // bad parses

        assertEquals(null, ex.extractRegister(null));
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertEquals(null, ex.extractRegister(parse("").register()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractRegister(parse("$").register()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractRegister((SimpParser.RegisterContext)
            giveParseException(parse("$s0").register())));
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");

        assertEquals(null, ex.extractRegister(parse("$ s0").register()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractRegister(parse("$ 00").register()));
        expectBadParse("invalid parse");

        assertEquals(null, ex.extractRegister(parse("$-1").register()));
        expectBadParse("invalid parse");
    }

    @Test
    public void testExtractDirectiveOperands() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // test good
        // directives operands can be integers, strings or addresses
        // the operand extractor does not check the sanity of any of the
        // operands (that is the program extractor's job)
        // for example, the 4th operand would not be acceptable for a directive
        // but it parses correctly
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\", 123, mylabel, mylabel + 0x15($s0)").directiveOperandList());
            assertEquals(4, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            expectGood();
        }
        // test single argument
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("123").directiveOperandList());
            assertEquals(1, ops.size());
            assertEquals(Operand.Type.Integer, ops.get(0).getType());
            assertEquals(123, ops.get(0).asIntegerOp().value);
            expectGood();
        }
        // test optional commas
        {
            {
                List<Operand> ops = ex.extractDirectiveOperands(parse("123 456").directiveOperandList());
                assertEquals(2, ops.size());
                assertEquals(Operand.Type.Integer, ops.get(0).getType());
                assertEquals(Operand.Type.Integer, ops.get(1).getType());
                assertEquals(123, ops.get(0).asIntegerOp().value);
                assertEquals(456, ops.get(1).asIntegerOp().value);
                expectGood();
            }
            {
                List<Operand> ops = ex.extractDirectiveOperands(parse("\"str1\"\"str2\"").directiveOperandList());
                assertEquals(2, ops.size());
                assertEquals(Operand.Type.String, ops.get(0).getType());
                assertEquals(Operand.Type.String, ops.get(1).getType());
                assertEquals("str1", ops.get(0).asStringOp().value);
                assertEquals("str2", ops.get(1).asStringOp().value);
                expectGood();
            }
            // should parse as a single address
            {
                List<Operand> ops = ex.extractDirectiveOperands(parse("mylabel +10").directiveOperandList());
                assertEquals(1, ops.size());
                assertEquals(Operand.Type.Address, ops.get(0).getType());
                expectAddress(Optional.of("mylabel"), Optional.of(10), Optional.empty(),
                    ops.get(0).asAddressOp());
                expectGood();
            }
            // should parse as a single address
            {
                List<Operand> ops = ex.extractDirectiveOperands(parse("mylabel +10($v0)").directiveOperandList());
                assertEquals(1, ops.size());
                assertEquals(Operand.Type.Address, ops.get(0).getType());
                expectAddress(Optional.of("mylabel"), Optional.of(10), Optional.of(Register.v0),
                    ops.get(0).asAddressOp());
                expectGood();
            }
        }

        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\", 123, mylabel, mylabel + 0x15($s0), 456").directiveOperandList());
            assertEquals(5, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals(Operand.Type.Integer, ops.get(4).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            assertEquals(456, ops.get(4).asIntegerOp().value);
            expectGood();
        }
        // same as above but without commas
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\" 123 mylabel mylabel + 0x15($s0) 456").directiveOperandList());
            assertEquals(5, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals(Operand.Type.Integer, ops.get(4).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            assertEquals(456, ops.get(4).asIntegerOp().value);
            expectGood();
        }
        // test a mixture of comma and no comma
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\" 123, 456").directiveOperandList());
            assertEquals(3, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Integer, ops.get(2).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            assertEquals(456, ops.get(2).asIntegerOp().value);
            expectGood();
        }



        // test bad

        assertTrue(ex.extractDirectiveOperands(null).isEmpty());
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertTrue(ex.extractDirectiveOperands(parse("").directiveOperandList()).isEmpty());
        expectBadParse("invalid parse");

        assertTrue(ex.extractDirectiveOperands((SimpParser.DirectiveOperandListContext)
            giveParseException(parse("123").directiveOperandList()))
            .isEmpty());
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");

        // registers are not allowed as operands
        {
            assertTrue(ex.extractDirectiveOperands(
                parse("$s0").directiveOperandList())
                .isEmpty());
            expectBadParse("invalid parse for directive operand");
        }
        // registers are not allowed as operands
        {
            assertTrue(ex.extractDirectiveOperands(
                parse("$0").directiveOperandList())
                .isEmpty());
            expectBadParse("invalid parse for directive operand");
        }
        // invalid address but the label could be interpreted as a valid
        // first operand. However it is not
        {
            assertTrue(ex.extractDirectiveOperands(
                parse("mylabel + ($s0)").directiveOperandList())
                .isEmpty());
            expectBadParse("invalid parse");
        }
        // test trailing comma (not allowed but extracts the information correctly)
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\" 123, ").directiveOperandList());
            assertEquals(2, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectBadParse("invalid parse for directive operand");
        }
        // test leading comma
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse(", \"abc\" 123, 456").directiveOperandList());
            assertEquals(3, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Integer, ops.get(2).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            assertEquals(456, ops.get(2).asIntegerOp().value);
            expectBadParse("invalid parse for directive operand");
        }
        // test multiple commas (not allowed but extracts the information correctly)
        {
            List<Operand> ops = ex.extractDirectiveOperands(parse("\"abc\",, 123, ").directiveOperandList());
            assertEquals(2, ops.size());
            assertEquals(Operand.Type.String, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals("abc", ops.get(0).asStringOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectBadParse("invalid parse for directive operand");
        }

    }

    @Test
    public void testExtractStatementOperands() {
        // refresh objects
        log = new StoreProblemLogger();
        ex = new OperandExtractor(log);

        // test good
        // statement operands can be register, integers, or addresses
        {
            List<Operand> ops = ex.extractStatementOperands(
                parse("$s0, 123, mylabel, mylabel + 0x15($s0)").statementOperandList());
            assertEquals(4, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals(Register.s0, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            expectGood();
        }
        // test single argument
        {
            List<Operand> ops = ex.extractStatementOperands(parse("123").statementOperandList());
            assertEquals(1, ops.size());
            assertEquals(Operand.Type.Integer, ops.get(0).getType());
            assertEquals(123, ops.get(0).asIntegerOp().value);
            expectGood();
        }
        // test optional commas
        {
            {
                List<Operand> ops = ex.extractStatementOperands(parse("123 456").statementOperandList());
                assertEquals(2, ops.size());
                assertEquals(Operand.Type.Integer, ops.get(0).getType());
                assertEquals(Operand.Type.Integer, ops.get(1).getType());
                assertEquals(123, ops.get(0).asIntegerOp().value);
                assertEquals(456, ops.get(1).asIntegerOp().value);
                expectGood();
            }
            // should parse as a single address
            {
                List<Operand> ops = ex.extractStatementOperands(parse("mylabel +10").statementOperandList());
                assertEquals(1, ops.size());
                assertEquals(Operand.Type.Address, ops.get(0).getType());
                expectAddress(Optional.of("mylabel"), Optional.of(10), Optional.empty(),
                    ops.get(0).asAddressOp());
                expectGood();
            }
            // should parse as a single address
            {
                List<Operand> ops = ex.extractStatementOperands(parse("mylabel +10($v0)").statementOperandList());
                assertEquals(1, ops.size());
                assertEquals(Operand.Type.Address, ops.get(0).getType());
                expectAddress(Optional.of("mylabel"), Optional.of(10), Optional.of(Register.v0),
                    ops.get(0).asAddressOp());
                expectGood();
            }
        }

        {
            List<Operand> ops = ex.extractStatementOperands(parse("$s0, 123, mylabel, mylabel + 0x15($s0), 456").statementOperandList());
            assertEquals(5, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals(Operand.Type.Integer, ops.get(4).getType());
            assertEquals(Register.s0, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            assertEquals(456, ops.get(4).asIntegerOp().value);
            expectGood();
        }
        // same as above but without commas
        {
            List<Operand> ops = ex.extractStatementOperands(parse("$s0 123 mylabel mylabel + 0x15($s0) 456").statementOperandList());
            assertEquals(5, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Address, ops.get(2).getType());
            assertEquals(Operand.Type.Address, ops.get(3).getType());
            assertEquals(Operand.Type.Integer, ops.get(4).getType());
            assertEquals(Register.s0, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectAddress(Optional.of("mylabel"), Optional.empty(), Optional.empty(),
                ops.get(2).asAddressOp());
            expectAddress(Optional.of("mylabel"), Optional.of(0x15), Optional.of(Register.s0),
                ops.get(3).asAddressOp());
            assertEquals(456, ops.get(4).asIntegerOp().value);
            expectGood();
        }
        // test a mixture of comma and no comma
        {
            List<Operand> ops = ex.extractStatementOperands(parse("$0 123, 456").statementOperandList());
            assertEquals(3, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Integer, ops.get(2).getType());
            assertEquals(Register.zero, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            assertEquals(456, ops.get(2).asIntegerOp().value);
            expectGood();
        }



        // test bad

        assertTrue(ex.extractStatementOperands(null).isEmpty());
        // appears to be a valid parse because the parser was not involved
        expectValidParseButProblem("invalid parse");

        assertTrue(ex.extractStatementOperands(parse("").statementOperandList()).isEmpty());
        expectBadParse("invalid parse");

        assertTrue(ex.extractStatementOperands((SimpParser.StatementOperandListContext)
            giveParseException(parse("123").statementOperandList()))
            .isEmpty());
        // appears to be a valid parse because the exception was added after
        expectValidParseButProblem("invalid parse");

        // strings are not allowed as operands
        {
            assertTrue(ex.extractStatementOperands(
                parse("\"abc\"").statementOperandList())
                .isEmpty());
            expectBadParse("invalid parse for statement operand");
        }
        // invalid address but the label could be interpreted as a valid
        // first operand. However it is not
        {
            assertTrue(ex.extractStatementOperands(
                parse("mylabel + ($s0)").statementOperandList())
                .isEmpty());
            expectBadParse("invalid parse");
        }
        // test trailing comma (not allowed but extracts the information correctly)
        {
            List<Operand> ops = ex.extractStatementOperands(parse("$0 123, ").statementOperandList());
            assertEquals(2, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Register.zero, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectBadParse("invalid parse for statement operand");
        }
        // test leading comma
        {
            List<Operand> ops = ex.extractStatementOperands(parse(", $0 123, 456").statementOperandList());
            assertEquals(3, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Operand.Type.Integer, ops.get(2).getType());
            assertEquals(Register.zero, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            assertEquals(456, ops.get(2).asIntegerOp().value);
            expectBadParse("invalid parse for statement operand");
        }
        // test multiple commas (not allowed but extracts the information correctly)
        {
            List<Operand> ops = ex.extractStatementOperands(parse("$0,, 123, ").statementOperandList());
            assertEquals(2, ops.size());
            assertEquals(Operand.Type.Register, ops.get(0).getType());
            assertEquals(Operand.Type.Integer, ops.get(1).getType());
            assertEquals(Register.zero, ops.get(0).asRegisterOp().value);
            assertEquals(123, ops.get(1).asIntegerOp().value);
            expectBadParse("invalid parse for statement operand");
        }
    }

}
