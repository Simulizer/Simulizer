package simulizer.ui.components.cpu;

import javafx.scene.shape.Rectangle;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Represents a general rectangular component
 */
public class GeneralComponent extends ComponentStackPane {

    /**
     * Sets up a general component
     * @param vis The visualisation to use
     * @param label The label for the component
     */
    public GeneralComponent(CPUVisualisation vis, String label){
        super(vis, label);
        this.shape = new Rectangle(x, y, width, height);
        setAttributes();
    }

    /**
     * Sets the shape width and redraws it
     * @param width The new shape width
     */
    public void setShapeWidthAndDraw(double width){
        ((Rectangle) shape).setWidth(width);
        text.setWrappingWidth(width * 0.9);
        super.setShapeWidth(width);
    }

    /**
     * Sets the shape height and redraws it
     * @param height The new shape height
     */
    public void setShapeHeightAndDraw(double height){
        ((Rectangle) shape).setHeight(height);
        super.setShapeHeight(height);
    }

    /**
     * Sets attributes for the shape, used when resizing
     * @param x The new x coordinate
     * @param y The new y coordinate
     * @param width The new width
     * @param height The new height
     */
    public void setAttrs(double x, double y, double width, double height){
        setLayoutX(x);
        shape.setLayoutX(x);
        shape.layoutXProperty().set(x);
        setLayoutY(y);
        setX(x);
        setY(y);
        shape.setLayoutY(y);
        shape.layoutYProperty().set(y);
        setShapeWidthAndDraw(width);
        setPrefWidth(width);
        setShapeHeightAndDraw(height);
        setPrefHeight(height);
    }

}