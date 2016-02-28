package simulizer.ui.components.cpu;

import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;
import simulizer.ui.windows.CPUVisualisation;

public class ALU extends ComponentStackPane {

    public ALU(CPUVisualisation vis, String label){
        super(vis, label);
        drawShape(new Polyline());
        setAttributes();
        text.setTextAlignment(TextAlignment.RIGHT);
    }

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

    public double getShapeHeight(){
        double rightHeight = height * 0.7;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.1;
        double leftHeight = (height - gapWidth) / 2;

        return (height + (rightHeight + rightSmallHeight)) / 2;
    }

    public void setShapeWidth(double width){
        this.width = width;
    }

    public void setShapeHeight(double height){
        this.height = height;
    }

    public void setAttrs(double x, double y, double width, double height){
        setLayoutX(x);
        shape.setLayoutX(x);
        setLayoutY(y);
        shape.setLayoutY(y);
        setShapeWidth(width);
        setPrefWidth(width);
        setShapeHeight(height);
        setPrefHeight(height);
        text.setWrappingWidth(width);
        drawShape(((Polyline) this.shape));
    }

}
