package simulizer.ui.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.reactfx.EventStream;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.theme.Theme;

/**
 * Bits of code from https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/
 * main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
 *
 * @author Kelsey McKenna
 */
public class CodeEditor extends InternalWindow {
	// @formatter:off
	// @formatter:on

	private CodeArea codeArea;
	private File currentFile = null;
	private boolean fileEdited = false, codeWrap = true;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Popup tooltipPopup = new Popup();
	private Label tooltipMsg = new Label();
	private List<Problem> problems = new ArrayList<>();

	private StyleSpans<Collection<String>> normalHighlighting =
		new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), 0).create();
	private StyleSpans<Collection<String>> errorHighlighting =
		new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), 0).create();

	private final String TITLE = WindowEnum.toEnum(this).toString();

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

	private static final Pattern PATTERN = Pattern.compile("(?<LABEL>" + LABEL_PATTERN + ")" + "|(?<DIRECTIVE>" + DIRECTIVE_PATTERN + ")"
		+ "|(?<KEYWORD>" + KEYWORD_PATTERN + ")" + "|(?<REGISTER>" + REGISTER_PATTERN + ")" + "|(?<LABELREF>" + LABELREF_PATTERN + ")"
		+ "|(?<CONSTANT>" + CONSTANT_PATTERN + ")" + "|(?<ANNOTATION>" + ANNOTATION_PATTERN + ")" + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
		+ "|(?<STRING>" + STRING_PATTERN + ")" + "");

	public CodeEditor() {
		tooltipPopup.getContent().add(tooltipMsg);
		tooltipMsg.getStyleClass().add("tooltip");

		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		// Change behaviour of pressing TAB
		EventHandler<? super KeyEvent> tabHandler =
			EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> codeArea.replaceSelection("    ")).create();
		EventHandlerHelper.install(codeArea.onKeyPressedProperty(), tabHandler);

		// Thanks to:
		// https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
		EventStream<?> plainTextChanges = codeArea.plainTextChanges();
		plainTextChanges.successionEnds(Duration.ofMillis(1000)).supplyTask(this::computeErrorHighlightingAsync).awaitLatest(plainTextChanges)
			.filterMap(t -> {
				if (t.isSuccess()) {
					return Optional.of(t.get());
				} else {
					t.getFailure().printStackTrace();
					return Optional.empty();
				}
			}).subscribe(this::applyErrorHighlighting);

		// Update regex highlighting and edit status on key press
		codeArea.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			updateRegexHighlighting();

			fileEdited = true;
			updateTitleEditStatus();
		});

		// Thanks to:
		// https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/TooltipDemo.java
		// Show tooltips
		codeArea.setMouseOverTextDelay(Duration.ofMillis(250));
		codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
			int chIdx = e.getCharacterIndex();
			Point2D pos = e.getScreenPosition();

			String errorMessage = getErrorMessage(chIdx);
			if (errorMessage == null) return;

			tooltipMsg.setText(wrap(getErrorMessage(chIdx), 45));
			tooltipMsg.setWrapText(true);
			tooltipPopup.show(codeArea, pos.getX(), pos.getY() + 10);
		});
		codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> tooltipPopup.hide());

		codeArea.setCursor(Cursor.TEXT);
		codeArea.replaceText("");
		codeArea.setWrapText(true);

		setTitle(TITLE + " - New File");
		getContentPane().getChildren().add(codeArea);
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

	private String wrap(String text, int width) {
		String svar = "";

		while (!text.isEmpty()) {
			text = text.trim();

			String c = text.substring(0, Math.min(width, text.length()));

			if (text.length() >= width && text.charAt(c.length()) != ' ') {
				int spaceIndex = c.lastIndexOf(' ');
				if (spaceIndex >= 0) {
					c = c.substring(0, spaceIndex);
				}
			}

			svar += c + "\n";
			text = text.substring(c.length());
		}

		return svar;
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
	private String getErrorMessage(int chIdx) {
		for (Problem problem : problems) {
			if (problem.rangeStart <= chIdx && chIdx <= problem.rangeEnd) return problem.message;
		}

		return null;
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().clear(); // Should this be here?
		getStylesheets().add(theme.getStyleSheet("code.css"));
	}

	public void setText(String text) {
		codeArea.replaceText(text);
	}

	public String getText() {
		return codeArea.getText();
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File f) {
		currentFile = f;
	}

	public boolean isFileEdited() {
		return fileEdited;
	}

	public void updateTitleEditStatus() {
		String title = getTitle();
		char lastChar = title.charAt(title.length() - 1);

		title = title.substring(0, lastChar == '*' ? title.length() - 1 : title.length());
		setTitle(title + (fileEdited ? "*" : ""));
	}

	/**
	 * Thanks to: https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * Applies the specified highlighting to the text in the code editor
	 *
	 * @param highlighting
	 */
	private void applyErrorHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
		errorHighlighting = highlighting;

		updateRegexHighlighting();
	}

	private void addBoth() {
		codeArea.setStyleSpans(0, normalHighlighting.overlay(errorHighlighting, (normal, error) -> {
			List<String> svar = new ArrayList<>(normal);
			svar.addAll(error);
			return svar;
		}));
	}

	/**
	 * Thanks to: https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * Recalculates the regex highlighting and then applies it to the document.
	 *
	 */
	private void updateRegexHighlighting() {
		String text = codeArea.getText();

		Matcher matcher = PATTERN.matcher(text);
		int lastKwEnd = 0;
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		while (matcher.find()) {
			int mStart = matcher.start();
			int mEnd = matcher.end();
			int cPosition = codeArea.getCaretPosition();

			if ((getErrorMessage(mStart) != null || getErrorMessage(mEnd) != null)
				&& (cPosition < mStart || cPosition > mEnd)) continue;

			String styleClass = matcher.group("LABEL") != null ? "label"
				: matcher.group("KEYWORD") != null ? "recognised-instruction"
					: matcher.group("REGISTER") != null ? "register"
						: matcher.group("DIRECTIVE") != null ? "directiveid"
							: matcher.group("CONSTANT") != null ? "constant"
								: matcher.group("STRING") != null ? "constant" : matcher.group("ANNOTATION") != null ? "annotation"
									: matcher.group("COMMENT") != null ? "comment" : matcher.group("LABELREF") != null ? "label" : null;
			/* never happens */ assert styleClass != null;

			int start = matcher.start();
			int matcherEnd = matcher.end();

			int length = matcherEnd - start - (matcher.group("LABEL") != null ? 1 : 0);

			spansBuilder.add(Collections.emptyList(), start - lastKwEnd);
			spansBuilder.add(Collections.singleton(styleClass), length);
			lastKwEnd = start + length;
		}

		spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
		normalHighlighting = spansBuilder.create();

		addBoth();
	}

	/**
	 * Thanks to:
	 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * @return
	 */
	private Task<StyleSpans<Collection<String>>> computeErrorHighlightingAsync() {
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
			if (p.rangeStart == -1 && p.rangeEnd == -1 && p.lineNum == Problem.NO_LINE_NUM) continue;

			int spacing = p.rangeStart - lastTokenEnd;
			if (spacing > 0) errorSpansBuilder.add(Collections.emptyList(), spacing);

			int styleSize = p.rangeEnd - p.rangeStart + 1;
			errorSpansBuilder.add(Collections.singleton("error"), styleSize);

			lastTokenEnd = p.rangeEnd + 1;
		}

		return errorSpansBuilder.create();
	}

	public void newFile() {
		setCurrentFile(null);
		fileEdited = false;
		setTitle(TITLE + " - New File");
		setText("");
	}

	public void loadFile(File selectedFile) {
		if (selectedFile != null) {
			try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile));) {
				String codeIn = "";
				String c;
				boolean first = true;
				while ((c = reader.readLine()) != null) {
					if (!first) {
						codeIn += "\n" + c;
					} else {
						codeIn += c;
						first = false;
					}
				}

				// Save the directory the user last opened (for convenience)
				if (selectedFile.getParent() != null) System.setProperty("user.dir", selectedFile.getParent());

				// Save the destination of the current file
				setCurrentFile(selectedFile);

				// Show the code in the editor
				setText(codeIn);
				fileEdited = false;
				setTitle(TITLE + " - " + selectedFile.getName());
				updateTitleEditStatus();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void saveFile() {
		if (currentFile != null) {
			try (PrintWriter writer = new PrintWriter(currentFile);) {
				writer.print(getText());
				setTitle(WindowEnum.toEnum(this).toString() + " - " + currentFile.getName());
				fileEdited = false;
				updateTitleEditStatus();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void toggleLineWrap() {
		codeArea.setWrapText(!codeWrap);
		codeWrap = !codeWrap;
	}
}
