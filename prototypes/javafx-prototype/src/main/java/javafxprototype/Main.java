package javafxprototype;

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
import org.fxmisc.richtext.StyleSpan;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafxprototype.parser.SmallMipsLexer;
import javafxprototype.parser.SmallMipsParser;
import jfxtras.labs.scene.control.window.Window;

// good JFX window tutorials here: https://github.com/miho/VFXWindows-Samples
// RichTextFX examples: https://github.com/TomasMikula/RichTextFX/tree/master/richtextfx-demos

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private static final String THEME = "my-theme";

	private static File currentFile = null;
	private static boolean fileEdited = false;

	@Override
	public void start(Stage primaryStage) {
		Pane pane = new Pane();

		pane.getStyleClass().add("background");
		pane.getStylesheets().add(THEME + "/background.css");

		Scene scene = new Scene(pane, 1060, 740);

		BorderPane codePane = new BorderPane();

		Window wc = new Window("Code View - New File");
		wc.setContentPane(codePane);
		wc.setLayoutX(20);
		wc.setLayoutY(20);
		wc.setPrefSize(400, 700);
		wc.getStylesheets().add(THEME + "/window.css");
		wc.getStylesheets().add(THEME + "/code.css");

		final CodeArea codeArea = new CodeArea();
		codeArea.setWrapText(true);
		codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
		codeArea.richChanges().subscribe(change -> codeArea.setStyleSpans(0, computeAntlrHighlighting(codeArea.getText())));
		codeArea.setOnKeyTyped(e -> {
			fileEdited = true;
			updateTitleEditStatus(wc, fileEdited);
		});

		// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
		MenuBar menuBar = new MenuBar();
		// --- Menu File
		Menu fileMenu = new Menu("File");

		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			// If no file has been opened (or if the user is working on a new
			// file)
			// then ask the user to select a destination
			if (currentFile == null) {
				final FileChooser fc = new FileChooser();
				fc.setInitialDirectory(new File(System.getProperty("user.dir")));
				fc.setTitle("Save an assembly file");
				fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));

				currentFile = fc.showSaveDialog(primaryStage);
			}

			// If the destination is specified
			if (currentFile != null) {
				try (PrintWriter writer = new PrintWriter(currentFile);) {
					writer.print(codeArea.getText());
					
					wc.setTitle("Code View - " + currentFile.getName());
					fileEdited = false;
					updateTitleEditStatus(wc, fileEdited);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		MenuItem newItem = new MenuItem("New");
		newItem.setOnAction(e -> {
			currentFile = null;
			fileEdited = false;
			wc.setTitle("Code View - New File");
			codeArea.replaceText("");
		});

		MenuItem loadItem = new MenuItem("Load");
		loadItem.setOnAction(e -> {
			// Set the file chooser to open at the user's last directory
			final FileChooser fc = new FileChooser();
			fc.setInitialDirectory(new File(System.getProperty("user.dir")));
			fc.setTitle("Open an assembly file");
			fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));

			File selectedFile = fc.showOpenDialog(primaryStage);

			// If the user actually selected some files
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
					currentFile = selectedFile;

					// Show the code in the editor
					codeArea.replaceText(codeIn);
					fileEdited = false;
					wc.setTitle("Code View - " + selectedFile.getName());
					updateTitleEditStatus(wc, fileEdited);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		});

		fileMenu.getItems().addAll(newItem, saveItem, loadItem);

		menuBar.getMenus().addAll(fileMenu);
		codePane.setTop(menuBar);

		codePane.setCenter(codeArea);

		Window wv = new Window("Visualisation");
		wv.getStylesheets().add(THEME + "/window.css");
		wv.setLayoutX(440);
		wv.setLayoutY(20);
		wv.setPrefSize(600, 400);

		Canvas canvas = new Canvas(600, 350);
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		drawVisualisation(ctx);
		wv.getContentPane().getChildren().add(canvas);

		Window wr = new Window("Registers");
		wr.getStylesheets().add(THEME + "/window.css");
		wr.setLayoutX(440);
		wr.setLayoutY(440);
		wr.setPrefSize(600, 280);

		pane.getChildren().addAll(wv, wc, wr);

		primaryStage.setTitle("JavaFX Prototype");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	private static void updateTitleEditStatus(Window wc, boolean fileEdited) {
		String title = wc.getTitle();
		char lastChar = title.charAt(title.length() - 1);

		title = title.substring(0, lastChar=='*' ? title.length() - 1 : title.length());
		wc.setTitle(title + (fileEdited ? "*" : ""));
	}

	/**
	 * http://www.programcreek.com/java-api-examples/index.php?api=org.fxmisc.
	 * richtext.StyleSpansBuilder
	 *
	 * Throws a big exception when no text is entered (but you can still write
	 * in the editor fine, and syntax highlighting still applies).
	 *
	 * @param text
	 *            the plaintext content of the code editor
	 * @return the text, now split into sections with attached css classes for
	 *         styling
	 */
	private static StyleSpans<Collection<String>> computeAntlrHighlighting(String text) {
		StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

		int lastTokenEnd = 0;
		ANTLRInputStream input = new ANTLRInputStream(text);
		SmallMipsLexer lexer = new SmallMipsLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		tokens.fill();

		for (int i = 0; i < tokens.size(); i++) {
			Token t = tokens.get(i);

			if (t.getType() == Token.EOF) {
				break;
			}

			// System.out.print(t.getText() + ": ");
			int type = t.getType();

			String styleClass;
			if (type == SmallMipsLexer.COMMENT) {
				styleClass = "comment";
			} else if (type == SmallMipsLexer.REGISTER) {
				styleClass = "register";
			} else if (type == SmallMipsLexer.NUMBER) {
				styleClass = "constant";
			} else if (isOpcode(t.getType())) {
				styleClass = "keyword";
			} else {
				styleClass = "plain";
			}

			int spacing = t.getStartIndex() - lastTokenEnd;
			if (spacing > 0) {
				spansBuilder.add(Collections.emptyList(), spacing);
			}
			int stylesize = (t.getStopIndex() - t.getStartIndex()) + 1;
			spansBuilder.add(Collections.singleton(styleClass), stylesize);
			lastTokenEnd = t.getStopIndex() + 1;
		}

		spansBuilder.add(Collections.emptyList(), 0);

		return spansBuilder.create();
	}

	public static boolean isOpcode(int tokenType) {
		return tokenType == SmallMipsParser.OPCODE2 || tokenType == SmallMipsParser.OPCODE3 || tokenType == SmallMipsParser.OPCODE2V
				|| tokenType == SmallMipsParser.OPCODE3V;
	}

	private void drawVisualisation(GraphicsContext ctx) {
		Image proc = new Image("processor.png");
		ctx.drawImage(proc, 10, 0, 580, 350);
	}
}
