package javafxprototype;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// good JFX window tutorials here: https://github.com/miho/VFXWindows-Samples
// RichTextFX examples: https://github.com/TomasMikula/RichTextFX/tree/master/richtextfx-demos

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final String THEME = "my-theme";

    private static final String[] KEYWORDS = new String[]{
        "add", "sub", "li", "bne"
    };
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final Pattern PATTERN = Pattern.compile(
        "(?<KEYWORD>" + KEYWORD_PATTERN + ")" +
        "|(?<CONSTANT>\\b[0-9]*\\b)" +
        "|(?<REGISTER>\\$[a-z][0-9])" +
        "|(?<COMMENT>#[^\n]*)"
    );

    private static final String code = "\n" +
        "# this does some nonsense :)\n" +
        "# try editing!\n"    +
        "add $s0, $s1, $s2\n" +
        "li  $s1, 14\n" +
        "bne $s1, $s0 # @test\n";


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

        codeArea.richChanges().subscribe(change ->
            codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));
        codeArea.replaceText(0, 0, code);

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

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastMatchEnd = 0;

        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while(matcher.find()) {
            String styleClass = "plain";

            if     (matcher.group("KEYWORD") != null)   styleClass = "keyword";
            else if(matcher.group("REGISTER") != null)  styleClass = "register";
            else if(matcher.group("CONSTANT") != null)  styleClass = "constant";
            else if(matcher.group("COMMENT") != null)   styleClass = "comment";

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastMatchEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());

            lastMatchEnd = matcher.end();
        }

        spansBuilder.add(Collections.emptyList(), text.length() - lastMatchEnd);

        return spansBuilder.create();
    }

    private void drawVisualisation(GraphicsContext ctx) {
        Image proc = new Image("processor.png");
        ctx.drawImage(proc, 10, 0, 580, 350);
    }
}
