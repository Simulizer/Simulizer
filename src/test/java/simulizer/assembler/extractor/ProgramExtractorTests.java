package simulizer.assembler.extractor;

import category.UnitTests;
import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.parser.SimpParser;

import java.util.List;

import static org.junit.Assert.*;

/**
 * test the ProgramExtractor class
 * 
 * @author mbway
 */
@Category({ UnitTests.class })
public class ProgramExtractorTests {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private StoreProblemLogger log;
	private ProgramExtractor ex;

	private Parser parserDoNotUseDirectly;

	private SimpParser parse(String s) {
		parserDoNotUseDirectly = new Parser();
		return parserDoNotUseDirectly.parse(s);
	}

	@SuppressWarnings("unused")
	private SimpParser parseWithTrace(String s) {
		parserDoNotUseDirectly = new Parser();
		return parserDoNotUseDirectly.parseWithTrace(s);
	}

	private List<String> getParserErrors() {
		return parserDoNotUseDirectly.getErrors();
	}

	@SuppressWarnings("unused")
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

	@Test
	public void testIgnoredRules() {
		// refresh objects
		log = new StoreProblemLogger();
		ex = new ProgramExtractor(log);

		// data directive
		{
			ex.enterDataDirective(parse(".data").dataDirective());
			expectGood();

			ex.enterDataDirective(parse(".data 123").dataDirective());
			expectGood();
		}
		// data directive bad
		{
			ex.enterDataDirective(parse(".data 123 345").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data -123").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data mylabel").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data mylabel+15").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data mylabel+15($s0)").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data 123, 345").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterDataDirective(parse(".data \"hello\"").dataDirective());
			expectValidParseButProblem("invalid operand(s)");

			// doesn't recognise $s0 as a directiveOperandList
			// errors would be detected later when parsing $s0
			ex.enterDataDirective(parse(".data $s0").dataDirective());
			expectGood();
		}

		// text directive
		{
			ex.enterTextDirective(parse(".text").textDirective());
			expectGood();

			ex.enterTextDirective(parse(".text 123").textDirective());
			expectGood();
		}
		// text directive bad
		{
			ex.enterTextDirective(parse(".text 123 345").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text -123").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text mylabel").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text mylabel+15").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text mylabel+15($s0)").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text 123, 345").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			ex.enterTextDirective(parse(".text \"hello\"").textDirective());
			expectValidParseButProblem("invalid operand(s)");

			// doesn't recognise $s0 as a directiveOperandList
			// errors would be detected later when parsing $s0
			ex.enterTextDirective(parse(".text $s0").textDirective());
			expectGood();
		}

	}

	@Test
	public void testStatements() {
		// refresh objects
		log = new StoreProblemLogger();

		// base offset addressing
		{
			String p = "sw $s0, 16($s1)";
			ex = new ProgramExtractor(log);
			ex.enterTextSegment(null); // pretend inside text segment
			ex.enterStatement(parse(p).statement());
			expectGood();
		}
	}

	@Test
	public void testWholeParses() {
		// refresh objects
		log = new StoreProblemLogger();

		// TODO: multiple data and text segments
		// TODO: not recognising syscall as an argument when placed on the last line

		{
			String p = "" + ".text\n" + "main: add $s0, $s0, $s1\n";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectGood();
		}
		{
			String p = "" + ".text\n" + "\n" + "main:\n" + "  li $t2 25\n";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectGood();
		}
		{
			String p = "" + ".data; .asciiz \"\\hello\\\"\n.text; main: syscall;";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectBadParse("Error node: \"\"\\hello\\\"\"");
		}
		{
			// Like spim: cannot place multiple labels on a single line
			String p = "" + ".data;.text; main:abc: nop;";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectBadParse("Error node: \":\"");
		}
		{
			// Like spim: _can_ place multiple labels on a single line if separated by ;
			String p = "" + ".data;.text; main:;abc: nop;";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
            expectGood();
		}
		{
			// label names should be trimmed so these two are the same which should give an error
			String p = "" + ".data;.text; main:; abc: nop; abc   : nop;";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectValidParseButProblem("the label name: \"abc\" is taken");
		}
		{
			// duplicate labels which point to the same location should still raise errors
			String p = "" + ".data;.text; main:; abc:; abc: nop;";
			ex = new ProgramExtractor(log);
			ParseTreeWalker.DEFAULT.walk(ex, parse(p).program());
			expectValidParseButProblem("the label name: \"abc\" is taken");
		}
	}
}
