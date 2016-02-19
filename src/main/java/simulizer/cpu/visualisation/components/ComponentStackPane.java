package simulizer.cpu.visualisation.components;

import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.RadialGradient;
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
        this.shape.getStyleClass().addAll("cpu-component", this.getClass().getSimpleName());
        this.text.getStyleClass().addAll("cpu-component-label", this.getClass().getSimpleName());
        this.getStyleClass().addAll("cpu-container");
        getChildren().addAll(shape, text);
        setAlignment(shape, Pos.TOP_LEFT);

    }

    public Shape getComponentShape(){
        return shape;
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

    public ConnectorWire horizontalLineTo(ComponentStackPane shape, boolean right, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.HORIZONTAL, right, arrowStart, offset);
    }

    public ConnectorWire verticalLineTo(ComponentStackPane shape, boolean bottom, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.VERTICAL, bottom, arrowStart, offset);
    }

    public void highlight(int n){
        FillTransition ft = new FillTransition(Duration.millis(300), shape, Color.valueOf("#1e3c72"), Color.RED);
        ft.setCycleCount(n);
        ft.setAutoReverse(true);
        ft.play();
    }

}
