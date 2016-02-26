package simulizer.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javafx.concurrent.Task;
import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;

public class SyntaxHighlighter {
	private CodeArea codeArea;

	private List<Problem> problems = new ArrayList<>();
	private StyleSpans<Collection<String>> errorHighlighting = new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), 0).create();

	private ExecutorService executor = Executors.newSingleThreadExecutor((r) -> new Thread(r, "SyntaxHighlighter"));

	private static final String[] KEYWORDS = getKeywords();
	private static final String[] REGISTERS = getRegisterNames();
	private static final String[] DIRECTIVES = getDirectives();

	private static final String KEYWORD_PATTERN = "(" + String.join("|", KEYWORDS) + ")";
	private static final String REGISTER_PATTERN = "(" + String.join("|", REGISTERS) + ")";
	private static final String DIRECTIVE_PATTERN = "(" + String.join("|", DIRECTIVES) + ")";
	private static final String LABEL_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*:";
	private static final String LABELREF_PATTERN = "[a-zA-Z_][a-zA-Z0-9_]*";
	private static final String CONSTANT_PATTERN = "\\b[0-9]*\\b";
	private static final String COMMENT_PATTERN = "#[^\n]*";
	private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
	private static final String ANNOTATION_PATTERN = "@[^\n]*";

	private static final Pattern PATTERN = Pattern.compile("(?<LABEL>" + LABEL_PATTERN + ")" + "|(?<DIRECTIVE>" + DIRECTIVE_PATTERN + ")" + "|(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<REGISTER>" + REGISTER_PATTERN + ")" + "|(?<LABELREF>" + LABELREF_PATTERN + ")" + "|(?<CONSTANT>" + CONSTANT_PATTERN + ")" + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")" + "|(?<STRING>" + STRING_PATTERN + ")" + "");

	public SyntaxHighlighter(CodeArea codeArea) {
		this.codeArea = codeArea;
	}

	private static String[] getRegisterNames() {
		List<String> tempList = new ArrayList<>();

		for (Register r : Register.class.getEnumConstants()) {
			tempList.add("\\$" + r.name());
		}

		return tempList.toArray(new String[tempList.size()]);
	}

	private static String[] getKeywords() {
		List<String> tempList = new ArrayList<>();

		for (Instruction i : Instruction.class.getEnumConstants()) {
			tempList.add("" + i.name());
		}

		tempList.sort((x, y) -> y.length() - x.length());

		return tempList.toArray(new String[tempList.size()]);
	}

	private static String[] getDirectives() {
		List<String> tempList = new ArrayList<>();
		tempList.addAll(Arrays.asList("\\.data", "\\.text", ".globl", ".ascii", ".asciiz", ".byte", ".half", ".word", ".space", ".align"));
		tempList.sort((x, y) -> y.length() - x.length());

		return tempList.toArray(new String[tempList.size()]);
	}

	/**
	 * This will not currently work with Problem objects that do not specify rangeStart and rangeEnd
	 *
	 * @param chIdx
	 *            the index of the character in the code editor
	 * @return the error message associated with the phrase containing the
	 *         specified character. Returns null if there is no relevant error
	 *         message
	 */
	public String getErrorMessage(int chIdx) {
		for (Problem problem : problems) {
			if (problem.rangeStart <= chIdx && chIdx <= problem.rangeEnd)
				return problem.message;
		}

		return null;
	}

	/**
	 * Thanks to: https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * Recalculates the regex highlighting and then applies it to the document.
	 *
	 */
	public void updateRegexHighlighting() {
		String text = codeArea.getText();

		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		while (matcher.find()) {
			int mStart = matcher.start();
			int mEnd = matcher.end();
			int cPosition = codeArea.getCaretPosition();

			// Don't override error messages, unless the caret is over an error block
			if ((cPosition < mStart || cPosition > mEnd) && (getErrorMessage(mStart) != null || getErrorMessage(mEnd) != null))
				continue;

			String styleClass = matcher.group("LABEL") != null ? "label" : matcher.group("KEYWORD") != null ? "recognised-instruction" : matcher.group("REGISTER") != null ? "register" : matcher.group("DIRECTIVE") != null ? "directiveid" : matcher.group("CONSTANT") != null ? "constant" : matcher.group("STRING") != null ? "constant" : matcher.group("ANNOTATION") != null ? "annotation" : matcher.group("COMMENT") != null ? "comment" : matcher.group("LABELREF") != null ? "label" : null;
			/* never happens */ assert styleClass != null;

			int start = matcher.start();
			int matcherEnd = matcher.end();

			int length = matcherEnd - start - (matcher.group("LABEL") != null ? 1 : 0);

			spansBuilder.add(Collections.emptyList(), start - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), length);
			lastKwEnd = start + length;
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

		// Add the regex highlighting to the error highlighting
		codeArea.setStyleSpans(0, spansBuilder.create().overlay(errorHighlighting, (normal, error) -> {
			List<String> svar = new ArrayList<>(normal);
			svar.addAll(error);
			return svar;
		}));
	}

	/**
	 * Thanks to: https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * Applies the specified highlighting to the text in the code editor
	 *
	 * @param errorHighlighting
	 */
	public void applyAndSaveErrorHighlighting(StyleSpans<Collection<String>> errorHighlighting) {
		this.errorHighlighting = errorHighlighting;
		codeArea.setStyleSpans(0, errorHighlighting);

		// After updating the error highlighting, tell the regex highlighting to update
		updateRegexHighlighting();
	}

	/**
	 * Thanks to:
	 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * @return
	 */
	public Task<StyleSpans<Collection<String>>> computeErrorHighlightingAsync() {
		String text = codeArea.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeErrorHighlighting(text);
			}
		};
		executor.execute(task);
		return task;
	}

	/**
	 * http://www.programcreek.com/java-api-examples/index.php?api=org.fxmisc.
	 * richtext.StyleSpansBuilder Throws a big exception when no text is entered
	 * (but you can still write in the editor fine, and syntax highlighting
	 * still applies)
	 *
	 * @param text
	 *            the plaintext content of the code editor
	 * @return the text, now split into sections with attached css classes for
	 *         styling
	 */
	private StyleSpans<Collection<String>> computeErrorHighlighting(String text) {
		text += "\n"; // new line to make sure the end of the input parses correctly

		SimpLexer lexer = new SimpLexer(new ANTLRInputStream(text));
		SimpParser parser = new SimpParser(new CommonTokenStream(lexer));

		// Go through the program again and find the error spots
		StoreProblemLogger log = new StoreProblemLogger();
		ProgramExtractor pExtractor = new ProgramExtractor(log);
		SimpParser.ProgramContext tree = parser.program();
		ParseTreeWalker.DEFAULT.walk(pExtractor, tree);

		this.problems = log.getProblems();

		StyleSpansBuilder<Collection<String>> errorSpansBuilder = new StyleSpansBuilder<>();
		// Make sure at least one span is added
		errorSpansBuilder.add(Collections.emptyList(), 0);

		// Then add error wrappers around the problems
		System.out.println("---- Current Problems ----");
		int lastTokenEnd = 0;
		for (Problem p : log.getProblems()) {
			System.out.println(p);
			// If it is a global error, just show it in the logger or something
			if (p.rangeStart == -1 && p.rangeEnd == -1 && p.lineNum == Problem.NO_LINE_NUM)
				continue;

			int spacing = p.rangeStart - lastTokenEnd;
			if (spacing > 0)
				errorSpansBuilder.add(Collections.emptyList(), spacing);

			int styleSize = p.rangeEnd - p.rangeStart + 1;
			errorSpansBuilder.add(Collections.singleton("error"), styleSize);

			lastTokenEnd = p.rangeEnd + 1;
		}

		return errorSpansBuilder.create();
	}

	public void stop() {
		executor.shutdown();
	}

}
