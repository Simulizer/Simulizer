package javafxprototype;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Pane canvas = new Pane();

        Scene scene = new Scene(canvas, 1600, 900);

        Window wv = new Window("Visualisation");
        wv.setLayoutX(450);
        wv.setLayoutY(10);
        wv.setPrefSize(600, 400);

        Window wc = new Window("Code View");
        wc.setLayoutX(10);
        wc.setLayoutY(10);
        wc.setPrefSize(400, 700);

        Window wr = new Window("Registers");
        wr.setLayoutX(450);
        wr.setLayoutY(450);
        wr.setPrefSize(600, 250);


        canvas.getChildren().addAll(wv, wc, wr);

        primaryStage.setTitle("JavaFX Prototype");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
