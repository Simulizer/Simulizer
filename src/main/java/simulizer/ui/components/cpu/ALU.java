package simulizer.ui.components.cpu;

import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Represents an ALU in the CPU visualisation
 */
public class ALU extends ComponentStackPane {

    /**
     * Draws the shape, sets attributes and sets the text alignment
     * @param vis The CPU visualisation
     * @param label The label to use
     */
    public ALU(CPUVisualisation vis, String label){
        super(vis, label);
        drawShape(new Polyline());
        setAttributes();
        text.setTextAlignment(TextAlignment.RIGHT);
    }

    /**
     * Draws the shape, uses a polyline along with the width and height to calculate a suitable shape
     * @param polyline The polyline to draw with
     */
    public void drawShape(Polyline polyline){
        double baseX = x;
        double baseY = y;

        double rightHeight = height * 0.6;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.3;
        double leftHeight = (height - gapWidth) / 2;

        polyline.getPoints().clear();
        polyline.getPoints().addAll(new Double[]{
                baseX, baseY,
                baseX + width, baseY + rightSmallHeight,
                baseX + width, baseY + rightHeight + rightSmallHeight,
                baseX, baseY + height,
                baseX, baseY + gapWidth + leftHeight,
                baseX + (width * 0.2), baseY + leftHeight + (gapWidth / 2),
                baseX, baseY + leftHeight,
                baseX, baseY
        });

        this.shape = polyline;
    }

    /**
     * Gets the shape height
     * @return The height of the shape
     */
    public double getShapeHeight(){
        double rightHeight = height * 0.7;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.1;
        double leftHeight = (height - gapWidth) / 2;

        return (height + (rightHeight + rightSmallHeight)) / 2;
    }

    /**
     * Sets the shape width
     * @param width The shape width
     */
    public void setShapeWidth(double width){
        this.width = width;
    }

    /**
     * Sets the shape height
     * @param height The shape height
     */
    public void setShapeHeight(double height){
        this.height = height;
    }

    /**
     * Sets attributes on the shape, useful when resizing
     * @param x The new x coordinate
     * @param y The new y coordinate
     * @param width The new width
     * @param height The new height
     */
    public void setAttrs(double x, double y, double width, double height){
        setLayoutX(x);
        shape.setLayoutX(x);
        setLayoutY(y);
        shape.setLayoutY(y);
        setX(x);
        setY(y);
        setShapeWidth(width);
        setPrefWidth(width);
        setShapeHeight(height);
        setPrefHeight(height);
        text.setWrappingWidth(width);
        drawShape(((Polyline) this.shape));
    }

}
