package simulizer.ui.components.cpu;

import javafx.beans.binding.DoubleBinding;
import javafx.scene.shape.Polyline;

/**
 * Represents a horizontal or vertical wire
 */
public class ConnectorWire extends Wire {

    ComponentStackPane from;
    ComponentStackPane to;
    double offset;
    boolean arrowStart;
    boolean rightOrBottom;
    Type type;

    /**
     * Sets up the connector wire
     * @param from The shape going from
     * @param to The shape going to
     * @param type The type of wire
     * @param rightOrBottom If starting from the right or bottom
     * @param arrowStart If the arrow should be on from or to
     * @param offset The offset of the wire to multiply (0-1)
     */
    public ConnectorWire(ComponentStackPane from, ComponentStackPane to, Type type, boolean rightOrBottom, boolean arrowStart, double offset){
        super(new Polyline(), new Polyline(), type);
        this.from = from;
        this.to = to;
        this.arrowStart = arrowStart;
        this.type = type;
        this.rightOrBottom = rightOrBottom;
        this.reverse = (rightOrBottom && arrowStart);

        this.offset = offset;
        if(type == Type.HORIZONTAL){
            drawHorizontalWire();
        } else {
            drawVerticalLine();
        }

        arrowHead.getStyleClass().add("cpu-arrowhead");
        line.getStyleClass().add("cpu-line");

        this.getChildren().addAll(line, arrowHead);

    }

    /**
     * Redraws the wire and reanimates the data if required
     */
    public void updateLine(){
        if(this.type == Type.HORIZONTAL){
            drawHorizontalWire();
        } else {
            drawVerticalLine();
        }
        reanimateData();
    }

    /**
     * Draws a horizontal wire connecting the two shapes
     */
    public void drawHorizontalWire(){
        line.getPoints().clear();
        arrowHead.getPoints().clear();

        DoubleBinding xStart = from.layoutXProperty().add(from.getShapeWidth());
        DoubleBinding yStart = from.layoutYProperty().add(from.getShapeHeight() / 2).add(from.getShapeHeight() * offset);
        DoubleBinding xEnd = to.layoutXProperty().add(0);
        DoubleBinding yEnd = from.layoutYProperty().add(from.getShapeHeight() / 2).add(from.getShapeHeight() * offset);

        if(!rightOrBottom){
            xStart = from.layoutXProperty().add(0);
            xEnd = to.layoutXProperty().add(to.getShapeWidth());

            if(arrowStart) {
                arrowHead.getPoints().addAll(new Double[]{
                        xStart.getValue(), yStart.getValue(),
                        xStart.add(5).getValue(), yStart.add(-5).getValue(),
                        xStart.add(5).getValue(), yStart.add(5).getValue(),
                        xStart.getValue(), yStart.getValue()
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        xEnd.getValue(), yEnd.getValue(),
                        xEnd.add(10).getValue(), yEnd.add(-10).getValue(),
                        xEnd.add(10).getValue(), yEnd.add(10).getValue(),
                        xEnd.getValue(), yEnd.getValue()
                });
            }
        } else {

            if(arrowStart){
                arrowHead.getPoints().addAll(new Double[]{
                        xStart.getValue(), yStart.getValue(),
                        xStart.add(10).getValue(), yStart.add(-10).getValue(),
                        xStart.add(10).getValue(), yStart.add(10).getValue(),
                        xStart.getValue(), yStart.getValue()
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        xEnd.getValue(), yEnd.getValue(),
                        xEnd.add(-10).getValue(), yEnd.add(-10).getValue(),
                        xEnd.add(-10).getValue(), yEnd.add(10).getValue(),
                        xEnd.getValue(), yEnd.getValue()
                });
            }

        }

        line.getPoints().addAll(new Double[]{
                xStart.getValue(), yStart.getValue(),
                xEnd.getValue(), yEnd.getValue()
        });
    }

    /**
     * Draws a vertical wire connecting the two shapes
     */
    public void drawVerticalLine(){
        line.getPoints().clear();
        arrowHead.getPoints().clear();
        DoubleBinding xStart = from.layoutXProperty().add(from.getShapeWidth() / 2).add(from.getShapeWidth() * offset);
        DoubleBinding yStart = from.layoutYProperty().add(from.getShapeHeight());
        DoubleBinding xEnd = from.layoutXProperty().add(from.getShapeWidth() / 2).add(from.getShapeWidth() * offset);
        DoubleBinding yEnd = to.layoutYProperty().add(0);


        if(!rightOrBottom){
            //Bottom to top
            yStart = from.layoutYProperty().add(0);
            yEnd = to.layoutYProperty().add(to.getShapeHeight());
        }

        line.getPoints().addAll(new Double[]{
                xStart.getValue(), yStart.getValue(),
                xEnd.getValue(), yEnd.getValue()
        });

        if(arrowStart){
            if(rightOrBottom){
                arrowHead.getPoints().addAll(new Double[]{
                        xStart.getValue(), yStart.getValue(),
                        xStart.add(10).getValue(), yStart.add(10).getValue(),
                        xStart.add(-10).getValue(), yStart.add(10).getValue(),
                        xStart.getValue(), yStart.getValue()
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        xStart.getValue(), yStart.getValue(),
                        xStart.add(-10).getValue(), yStart.add(-10).getValue(),
                        xStart.add(10).getValue(), yStart.add(-10).getValue(),
                        xStart.getValue(), yStart.getValue()
                });
            }

        } else {

            if(rightOrBottom){
                arrowHead.getPoints().addAll(new Double[]{
                        xEnd.getValue(), yEnd.getValue(),
                        xEnd.add(-10).getValue(), yEnd.add(-10).getValue(),
                        xEnd.add(10).getValue(), yEnd.add(-10).getValue(),
                        xEnd.getValue(), yEnd.getValue()
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        xEnd.getValue(), yEnd.getValue(),
                        xEnd.add(10).getValue(), yEnd.add(10).getValue(),
                        xEnd.add(-10).getValue(), yEnd.add(10).getValue(),
                        xEnd.getValue(), yEnd.getValue()
                });
            }

        }
    }

}