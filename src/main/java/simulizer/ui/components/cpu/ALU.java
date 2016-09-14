package simulizer.ui.components.cpu;

import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Represents an ALU in the CPU visualisation
 * @author Theo Styles
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
        getComponentLabel().setTextAlignment(TextAlignment.RIGHT);
    }

    /**
     * Draws the shape, uses a polyline along with the width and height to calculate a suitable shape
     * @param polyline The polyline to draw with
     */
    public void drawShape(Polyline polyline){
        double baseX = getX();
        double baseY = getY();

        double rightHeight = super.getShapeHeight() * 0.6;
        double rightSmallHeight = (super.getShapeHeight() - rightHeight) / 2;

        double gapWidth = super.getShapeHeight() * 0.3;
        double leftHeight = (super.getShapeHeight() - gapWidth) / 2;

        polyline.getPoints().clear();
        polyline.getPoints().addAll(
                baseX, baseY,
                baseX + getShapeWidth(), baseY + rightSmallHeight,
                baseX + getShapeWidth(), baseY + rightHeight + rightSmallHeight,
                baseX, baseY + super.getShapeHeight(),
                baseX, baseY + gapWidth + leftHeight,
                baseX + (getShapeWidth() * 0.2), baseY + leftHeight + (gapWidth / 2),
                baseX, baseY + leftHeight,
                baseX, baseY);

        setComponentShape(polyline);
    }

    /**
     * Gets the shape height
     * @return The height of the shape
     */
    @Override
	public double getShapeHeight(){
        double rightHeight = super.getShapeHeight() * 0.7;
        double rightSmallHeight = (super.getShapeHeight() - rightHeight) / 2;
        return (super.getShapeHeight() + (rightHeight + rightSmallHeight)) / 2;
    }

    /**
     * Sets attributes on the shape, useful when resizing
     * @param x The new x coordinate
     * @param y The new y coordinate
     * @param width The new width
     * @param height The new height
     */
    public void setAttrs(double x, double y, double width, double height){
        Shape shape = getComponentShape();
        Text text = getComponentLabel();
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
        drawShape(((Polyline) shape));
    }

}
