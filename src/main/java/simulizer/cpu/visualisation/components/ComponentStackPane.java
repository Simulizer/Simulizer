package simulizer.cpu.visualisation.components;

import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ComponentStackPane extends StackPane {

    Shape shape;
    Text text;
    int x;
    int y;
    int width;
    int height;

    public ComponentStackPane(int x, int y, int width, int height, String label){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = new Text(label);
    }

    public void setAttributes(){
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.text.setWrappingWidth(width * 0.9);
        this.shape.getStyleClass().addAll("cpu-component", this.getClass().getSimpleName());
        this.text.getStyleClass().addAll("cpu-component-label", this.getClass().getSimpleName());
        getChildren().addAll(shape, text);
        setAlignment(shape, Pos.TOP_LEFT);
    }

    public void highlight(int n){
        FillTransition ft = new FillTransition(Duration.millis(300), shape, Color.WHITE, Color.RED);
        FillTransition tt = new FillTransition(Duration.millis(300), text, Color.BLACK, Color.WHITE);
        ft.setCycleCount(n);
        ft.setAutoReverse(true);
        ft.play();

        tt.setCycleCount(n);
        tt.setAutoReverse(true);
        tt.play();
    }

}
