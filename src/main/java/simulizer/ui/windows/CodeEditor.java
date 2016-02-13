package simulizer.ui.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.reactfx.EventStream;

import javafx.concurrent.Task;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Popup;
import simulizer.SyntaxHighlighter;
import simulizer.assembler.extractor.ProgramExtractor;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.parser.SimpLexer;
import simulizer.parser.SimpParser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.theme.Theme;

/**
 * Bits of code from
 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/
 * main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
 *
 * @author Kelsey McKenna
 *
 */
public class CodeEditor extends InternalWindow {
	// @formatter:off
	// @formatter:on

	private CodeArea codeArea;
	private File currentFile = null;
	private boolean fileEdited = false;
	private ExecutorService executor = Executors.newSingleThreadExecutor();
	private Popup tooltipPopup = new Popup();
	private Label tooltipMsg = new Label();
	private List<Problem> problems;

	private final String TITLE = WindowEnum.toEnum(this).toString();

	public CodeEditor() {
		tooltipPopup.getContent().add(tooltipMsg);
		tooltipMsg.setTextFill(Color.WHITE);
		tooltipMsg.setStyle("-fx-background-color: black;");

		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

		// Thanks to:
		// https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
		EventStream<?> richChanges = codeArea.richChanges();
		richChanges.successionEnds(Duration.ofMillis(50)).supplyTask(this::computeAntlrHighlightingAsync).awaitLatest(richChanges).filterMap(t -> {
			if (t.isSuccess()) {
				return Optional.of(t.get());
			} else {
				t.getFailure().printStackTrace();
				return Optional.empty();
			}
		}).subscribe(this::applyHighlighting);

		// codeArea.richChanges().subscribe(change -> codeArea.setStyleSpans(0,
		// computeAntlrHighlighting(codeArea.getText())));
		codeArea.replaceText("");
		codeArea.setWrapText(true);

		// Thanks to:
		// https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/TooltipDemo.java
		codeArea.setMouseOverTextDelay(Duration.ofSeconds(1));
		codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
			int chIdx = e.getCharacterIndex();
			Point2D pos = e.getScreenPosition();

			String errorMessage = getErrorMessage(chIdx);
			if (errorMessage == null)
				return;

			tooltipMsg.setText(getErrorMessage(chIdx));
			tooltipPopup.show(codeArea, pos.getX(), pos.getY() + 10);
		});

		codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> tooltipPopup.hide());

		codeArea.setOnKeyTyped(e -> {
			fileEdited = true;
			updateTitleEditStatus();
		});

		setTitle(TITLE + " - New File");
		getContentPane().getChildren().add(codeArea);
	}

	/**
	 * @param chIdx
	 *            the index of the character in the code editor
	 * @return the error message associated with the phrase containing the
	 *         specified character. Returns null if there is no relevant error
	 *         message
	 */
	private String getErrorMessage(int chIdx) {
		for (Problem problem : problems) {
			if (problem.rangeStart <= chIdx && chIdx <= problem.rangeEnd)
				return problem.message;
		}

		return null;
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().clear();
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
	 * Thanks to:
	 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * @param highlighting
	 */
	private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
		codeArea.setStyleSpans(0, highlighting);
	}

	/**
	 * Thanks to:
	 * https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/
	 * src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
	 *
	 * @return
	 */
	private Task<StyleSpans<Collection<String>>> computeAntlrHighlightingAsync() {
		String text = codeArea.getText();
		Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
			@Override
			protected StyleSpans<Collection<String>> call() throws Exception {
				return computeAntlrHighlighting(text);
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
	private StyleSpans<Collection<String>> computeAntlrHighlighting(String text) {
		text += "\n"; // new line to make sure the end of the input parses
						// correctly

		SimpLexer lexer = new SimpLexer(new ANTLRInputStream(text));
		SimpParser parser = new SimpParser(new CommonTokenStream(lexer));

		StyleSpansBuilder<Collection<String>> normalSpansBuilder = new StyleSpansBuilder<>();
		SimpParser.ProgramContext tree = parser.program();
		SyntaxHighlighter extractor = new SyntaxHighlighter(normalSpansBuilder);
		ParseTreeWalker.DEFAULT.walk(extractor, tree);

//		// Make sure there is at least one style added
		normalSpansBuilder.add(Collections.emptyList(), 0);

		// Go through the program again and find the error spots
		StyleSpansBuilder<Collection<String>> errorSpansBuilder = new StyleSpansBuilder<>();
		StoreProblemLogger log = new StoreProblemLogger();
		ProgramExtractor pExtractor = new ProgramExtractor(log);
		tree = parser.program(); // Reset the tree
		ParseTreeWalker.DEFAULT.walk(pExtractor, tree);

		errorSpansBuilder.add(Collections.emptyList(), 0);

		// Then add error wrappers around the problems
		this.problems = log.getProblems();
		int lastTokenEnd = 0;
		for (Problem p : this.problems) {
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

		return normalSpansBuilder.create().overlay(errorSpansBuilder.create(), (normal, error) -> error);
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
				if (selectedFile.getParent() != null)
					System.setProperty("user.dir", selectedFile.getParent());

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
}
