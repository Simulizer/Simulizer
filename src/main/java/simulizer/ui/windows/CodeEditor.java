package simulizer.ui.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.Optional;
import java.util.function.IntFunction;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.MouseOverTextEvent;
import org.fxmisc.wellbehaved.event.EventHandlerHelper;
import org.fxmisc.wellbehaved.event.EventPattern;
import org.reactfx.EventStream;
import org.reactfx.value.Val;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Popup;
import simulizer.ui.SyntaxHighlighter;
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
	private CodeArea codeArea;
	private File currentFile = null;
	private SimpleIntegerProperty currentLine;
	private boolean fileEdited = false, lineWrap = true;
	private SyntaxHighlighter syntaxHighlighter;

	private final String TITLE = WindowEnum.toEnum(this).toString();

	public CodeEditor() {
		Popup tooltipPopup = new Popup();
		Label tooltipMsg = new Label();

		tooltipPopup.getContent().add(tooltipMsg);
		tooltipMsg.getStyleClass().add("tooltip");

		currentLine = new SimpleIntegerProperty(-1);

		codeArea = new CodeArea();
		syntaxHighlighter = new SyntaxHighlighter(codeArea);
		IntFunction<Node> numberFactory = LineNumberFactory.get(codeArea);
		IntFunction<Node> arrowFactory = new LineArrowFactory(currentLine.asObject());
		IntFunction<Node> combinedFactory = line -> {
			HBox hbox = new HBox(numberFactory.apply(line), arrowFactory.apply(line));
			hbox.setAlignment(Pos.CENTER_LEFT);
			return hbox;
		};
		codeArea.setParagraphGraphicFactory(combinedFactory);

		// Change behaviour of pressing TAB
		EventHandler<? super KeyEvent> tabHandler =
			EventHandlerHelper.on(EventPattern.keyPressed(KeyCode.TAB)).act(event -> codeArea.replaceSelection("    ")).create();
		EventHandlerHelper.install(codeArea.onKeyPressedProperty(), tabHandler);

		// Thanks to:
		// https://github.com/TomasMikula/RichTextFX/blob/master/richtextfx-demos/src/main/java/org/fxmisc/richtext/demo/JavaKeywordsAsync.java
		EventStream<?> plainTextChanges = codeArea.plainTextChanges();
		plainTextChanges.successionEnds(Duration.ofMillis(1000)).supplyTask(syntaxHighlighter::computeErrorHighlightingAsync)
			.awaitLatest(plainTextChanges).filterMap(t -> {
				if (t.isSuccess()) {
					return Optional.of(t.get());
				} else {
					t.getFailure().printStackTrace();
					return Optional.empty();
				}
			}).subscribe(syntaxHighlighter::applyAndSaveErrorHighlighting);

		// Update regex highlighting and edit status on key press
		codeArea.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			syntaxHighlighter.updateRegexHighlighting();

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

			String errorMessage = syntaxHighlighter.getErrorMessage(chIdx);
			if (errorMessage == null) return;

			tooltipMsg.setText(wrap(errorMessage, 45));
			tooltipPopup.show(codeArea, pos.getX(), pos.getY() + 10);
		});
		codeArea.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> tooltipPopup.hide());

		codeArea.setCursor(Cursor.TEXT);
		codeArea.replaceText("");
		codeArea.setWrapText(true);

		setTitle(TITLE + " - New File");
		getContentPane().getChildren().add(codeArea);
	}

	// -- Getters, setters, and public void methods

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

	public boolean getLineWrap() {
		return lineWrap;
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
				syntaxHighlighter.updateRegexHighlighting();
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
		codeArea.setWrapText(!lineWrap);
		lineWrap = !lineWrap;
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().clear(); // Should this be here?
		getStylesheets().add(theme.getStyleSheet("code.css"));
	}

	// -- Helper methods for syntax highlighting tokens

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

	// -- Updating syntax highlighting

	class LineArrowFactory implements IntFunction<Node> {
		public final ObservableValue<Integer> selectedLine;

		public LineArrowFactory(ObservableValue<Integer> selectedLine) {
			this.selectedLine = selectedLine;
		}

		@Override
		public Node apply(int lineNum) {
			Polygon triangle = new Polygon(0.0, 0.0, 10.0, 5.0, 0.0, 10.0);
			triangle.setFill(Color.GREEN);

			ObservableValue<Boolean> visible = Val.map(selectedLine, l -> l == lineNum);

			triangle.visibleProperty().bind(Val.flatMap(triangle.sceneProperty(), scene -> {
				if (scene != null) {
					return visible;
				} else {
					return Val.constant(false);
				}
			}));

			return triangle;
		}
	}

	public void highlightCurrentLine(int lineNum) {
		currentLine.set(lineNum);
	}

}
