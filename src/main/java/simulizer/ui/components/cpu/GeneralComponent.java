package simulizer.ui.components.cpu;

import javafx.scene.shape.Rectangle;
import simulizer.ui.windows.CPUVisualisation;

public class GeneralComponent extends ComponentStackPane {

    public GeneralComponent(CPUVisualisation vis, String label){
        super(vis, label);
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
        shape.setLayoutX(x);
        shape.layoutXProperty().set(x);
        setLayoutY(y);
        shape.setLayoutY(y);
        shape.layoutYProperty().set(y);
        setShapeWidth(width);
        setPrefWidth(width);
        setShapeHeight(height);
        setPrefHeight(height);
    }


}
