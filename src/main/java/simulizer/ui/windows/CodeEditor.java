package simulizer.ui.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import javafx.scene.layout.Border;
import simulizer.parser.SmallMipsLexer;
import simulizer.parser.SmallMipsParser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.theme.Theme;

public class CodeEditor extends InternalWindow {

	private CodeArea codeArea;
	private File currentFile = null;
	private boolean fileEdited = false;
	private final String TITLE = WindowEnum.toEnum(this).toString();

	public CodeEditor() {
		codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.richChanges().subscribe(change -> codeArea.setStyleSpans(0, computeAntlrHighlighting(codeArea.getText())));
		codeArea.setWrapText(true);
		setTitle(TITLE + " - New File");
		getContentPane().getChildren().add(codeArea);
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

	/** http://www.programcreek.com/java-api-examples/index.php?api=org.fxmisc.richtext.StyleSpansBuilder Throws a big exception when no text is entered (but you can still write in the editor fine, and syntax highlighting still applies)
	 * @param text the plaintext content of the code editor
	 * @return the text, now split into sections with attached css classes for styling */
	private StyleSpans<Collection<String>> computeAntlrHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		int lastTokenEnd = 0;
		ANTLRInputStream input = new ANTLRInputStream(text);
		SmallMipsLexer lexer = new SmallMipsLexer(input);
		lexer.removeErrorListeners();
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		// For each token (up to the EOF token)
		Token t;
		for (int i = 0; i < tokens.size() && (t = tokens.get(i)).getType() != Token.EOF; i++) {
			// Find the styleClass
			String styleClass;
			switch (t.getType()) {
				// Comment
				case SmallMipsLexer.COMMENT:
					styleClass = "comment";
					break;

				// Register
				case SmallMipsLexer.REGISTER:
					styleClass = "register";
					break;

				// Number
				case SmallMipsLexer.NUMBER:
					styleClass = "constant";
					break;

				// OP Code
				case SmallMipsParser.OPCODE2:
				case SmallMipsParser.OPCODE3:
				case SmallMipsParser.OPCODE2V:
				case SmallMipsParser.OPCODE3V:
					styleClass = "keyword";
					break;

				// Plain text
				default:
					styleClass = "plain";
					break;
			}

			// Set the styleClass to the text
			int spacing = t.getStartIndex() - lastTokenEnd;
			if (spacing > 0) spansBuilder.add(Collections.emptyList(), spacing);
			int stylesize = (t.getStopIndex() - t.getStartIndex()) + 1;
			spansBuilder.add(Collections.singleton(styleClass), stylesize);
			lastTokenEnd = t.getStopIndex() + 1;
		}

		// Make sure there is at least one style added
		spansBuilder.add(Collections.emptyList(), 0);

		return spansBuilder.create();
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
}
