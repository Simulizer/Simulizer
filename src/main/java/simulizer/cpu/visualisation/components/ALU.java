package simulizer.cpu.visualisation.components;

import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;

import java.util.Observable;
import java.util.Observer;

public class ALU extends ComponentStackPane implements Observer {

    public ALU(int x, int y, int width, int height, String label){
        super(x, y, width, height, label);

        double baseX = x;
        double baseY = y;

        double rightHeight = height * 0.4;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.4;
        double leftHeight = (height - gapWidth) / 2;

        Polyline polyline = new Polyline();
        polyline.getPoints().addAll(new Double[]{
                baseX,  baseY,
                baseX + width, baseY + rightSmallHeight,
                baseX + width, baseY + rightHeight + rightSmallHeight,
                baseX, baseY + height,
                baseX, baseY + gapWidth + leftHeight,
                baseX + (width * 0.6), baseY + leftHeight + (gapWidth / 2),
                baseX, baseY + leftHeight,
                baseX,  baseY
        });

        this.shape = polyline;

        setAttributes();
        text.setTextAlignment(TextAlignment.RIGHT);

    }

    public double getShapeHeight(){
        double rightHeight = height * 0.4;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.4;
        double leftHeight = (height - gapWidth) / 2;

        return (height + (rightHeight + rightSmallHeight)) / 2;
    }

    public void update(Observable obs, Object obj){

    }

}
