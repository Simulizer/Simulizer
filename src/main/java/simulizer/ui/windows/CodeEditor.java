package simulizer.ui.windows;

import java.util.Collection;
import java.util.Collections;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import simulizer.parser.SmallMipsLexer;
import simulizer.parser.SmallMipsParser;
import simulizer.ui.interfaces.InternalWindow;

public class CodeEditor extends InternalWindow {
	//@formatter:off
	private static final String DEFAULT_CODE = "\n" + 
								"# this does some nonsense :)\n" + 
								"# try editing!\n" + 
								"add $s0, $s1, $s2\n" + 
								"li  $s1, 14\n" + 
								"bne $s1, $s0 # @testAnnotation{arg}{arg2}\n";
	//@formatter:on

	public CodeEditor() {
		CodeArea codeArea = new CodeArea();
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.richChanges().subscribe(change -> codeArea.setStyleSpans(0, computeAntlrHighlighting(codeArea.getText())));
		codeArea.replaceText(0, 0, DEFAULT_CODE);
		getContentPane().getChildren().add(codeArea);
	}

	@Override
	public void setTheme(String theme) {
		super.setTheme(theme);
		getStylesheets().add(theme + "/code.css");
	}

	/** http://www.programcreek.com/java-api-examples/index.php?api=org.fxmisc.richtext.StyleSpansBuilder Throws a big exception when no text is entered (but you can still write in the editor fine, and syntax highlighting still applies)
	 * @param text the plaintext content of the code editor
	 * @return the text, now split into sections with attached css classes for styling */
	private StyleSpans<Collection<String>> computeAntlrHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		int lastTokenEnd = 0;
		ANTLRInputStream input = new ANTLRInputStream(text);
		SmallMipsLexer lexer = new SmallMipsLexer(input);
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
		return spansBuilder.create();
	}

	@Override
	public String getWindowTitle() {
		return "Code Editor";
	}

}
