package javafxprototype;

import java.util.Collection;
import java.util.Collections;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import javafxprototype.parser.SmallMipsLexer;
import javafxprototype.parser.SmallMipsParser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;

// good JFX window tutorials here: https://github.com/miho/VFXWindows-Samples
// RichTextFX examples: https://github.com/TomasMikula/RichTextFX/tree/master/richtextfx-demos

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }


    private static final String THEME = "my-theme";

    private static final String DEFAULT_CODE = "\n" +
        "# this does some nonsense :)\n"            +
        "# try editing!\n"                          +
        "add $s0, $s1, $s2\n"                       +
        "li  $s1, 14\n"                             +
        "bne $s1, $s0 # @testAnnotation{arg}{arg2}\n";



    @Override
    public void start(Stage primaryStage) {
        Pane pane = new Pane();

        pane.getStyleClass().add("background");
        pane.getStylesheets().add(THEME + "/background.css");

        Scene scene = new Scene(pane, 1060, 740);



        Window wc = new Window("Code View");
        wc.getStylesheets().add(THEME + "/window.css");
        wc.setLayoutX(20);
        wc.setLayoutY(20);
        wc.setPrefSize(400, 700);

        CodeArea codeArea = new CodeArea();
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.richChanges()
                .subscribe(change -> codeArea.setStyleSpans(0, computeAntlrHighlighting(codeArea.getText())));
        codeArea.replaceText(0, 0, DEFAULT_CODE);

        wc.getStylesheets().add(THEME + "/code.css");
        wc.getContentPane().getChildren().add(codeArea);



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

    /**
     * http://www.programcreek.com/java-api-examples/index.php?api=org.fxmisc.richtext.StyleSpansBuilder
     *
     * Throws a big exception when no text is entered (but you can still write
     * in the editor fine, and syntax highlighting still applies).
     *
     * @param text the plaintext content of the code editor
     * @return the text, now split into sections with attached css classes for
     *         styling
     */
    private static StyleSpans<Collection<String>> computeAntlrHighlighting(String text) {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastTokenEnd         = 0;
        ANTLRInputStream input   = new ANTLRInputStream(text);
        SmallMipsLexer lexer     = new SmallMipsLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        for (int i = 0; i < tokens.size(); i++) {
            System.out.println(i);
            Token t = tokens.get(i);

            if (t.getType() == Token.EOF) {
                break;
            }

            // System.out.print(t.getText() + ": ");
            int type = t.getType();

            String styleClass;
            if (type == SmallMipsLexer.COMMENT) {
                styleClass = "comment";
                System.out.println("comment");
            } else if (type == SmallMipsLexer.REGISTER) {
                styleClass = "register";
                System.out.println("register");
            } else if (type == SmallMipsLexer.NUMBER) {
                styleClass = "constant";
                System.out.println("constant");
            } else if (isOpcode(t.getType())) {
                styleClass = "keyword";
                System.out.println("keyword");
            } else {
                styleClass = "plain";
                System.out.println("plain");
            }

            int spacing = t.getStartIndex() - lastTokenEnd;
            if (spacing > 0) {
                spansBuilder.add(Collections.emptyList(), spacing);
            }
            int stylesize = (t.getStopIndex() - t.getStartIndex()) + 1;
            spansBuilder.add(Collections.singleton(styleClass), stylesize);
            lastTokenEnd = t.getStopIndex() + 1;
        }

        return spansBuilder.create();
    }

    public static boolean isOpcode(int tokenType) {
        return tokenType == SmallMipsParser.OPCODE2  ||
               tokenType == SmallMipsParser.OPCODE3  ||
               tokenType == SmallMipsParser.OPCODE2V ||
               tokenType == SmallMipsParser.OPCODE3V;
    }

    private void drawVisualisation(GraphicsContext ctx) {
        Image proc = new Image("processor.png");
        ctx.drawImage(proc, 10, 0, 580, 350);
    }
}
