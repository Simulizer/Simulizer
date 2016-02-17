package simulizer.cpu.visualisation.components;

import javafx.animation.FillTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class ComponentStackPane extends StackPane {

    Shape shape;
    Text text;
    double x;
    double y;
    double width;
    double height;

    public ComponentStackPane(double x, double y, double width, double height, String label){
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
        this.shape.getStyleClass().addAll("cpu-component");
        this.text.getStyleClass().addAll("cpu-component-label");
        this.getStyleClass().addAll("cpu-container");
        getChildren().addAll(shape, text);
        setAlignment(shape, Pos.TOP_LEFT);

    }

    public double getShapeHeight(){
        return height;
    }

    public double getShapeWidth(){
        return width;
    }

    public void setShapeWidth(double width) {
        this.width = width;
    }

    public void setShapeHeight(double height){
        this.height = height;
    }

    public Wire horizontalLineTo(ComponentStackPane shape, boolean right, boolean arrowStart, double offset){
        return new Wire(this, shape, Wire.Type.HORIZONTAL, right, arrowStart, offset);
    }

    public Wire vericalLineTo(ComponentStackPane shape, boolean bottom, boolean arrowStart, double offset){
        return new Wire(this, shape, Wire.Type.VERTICAL, bottom, arrowStart, offset);
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
