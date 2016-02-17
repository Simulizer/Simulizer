package simulizer.cpu.visualisation.components;

import javafx.scene.shape.Rectangle;

import java.util.Observable;
import java.util.Observer;

public class GeneralComponent extends ComponentStackPane {

    Rectangle shapeCache;

    public GeneralComponent(double x, double y, double width, double height, String label){
        super(x, y, width, height, label);
        this.shape = new Rectangle(x, y, width, height);
        setAttributes();
    }

    public void setShapeWidth(double width){
        ((Rectangle) shape).setWidth(width);
        text.setWrappingWidth(width * 0.9);
        super.setShapeWidth(width);
    }

    public void setShapeHeight(double height){
        ((Rectangle) shape).setHeight(height);
        super.setShapeHeight(height);
    }

    public void setAttrs(double x, double y, double width, double height){
        setLayoutX(x);
        layoutXProperty().set(x);
        shape.setLayoutX(x);
        setLayoutY(y);
        layoutYProperty().set(y);
        shape.setLayoutY(y);
        setShapeWidth(width);
        setPrefWidth(width);
        setShapeHeight(height);
        setPrefHeight(height);
    }


}
