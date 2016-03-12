package simulizer.ui.components.cpu;

import javafx.scene.shape.Polyline;

/**
 * Represents a custom wire which travels in multiple directions
 */
public class CustomWire extends Wire {

    /**
     * Sets up a new custom wire
     * @param xStart The starting x position
     * @param yStart The starting y position
     * @param customLines The custom lines for the wire
     */
    public CustomWire(double xStart, double yStart, CustomLine... customLines){
        super(new Polyline(), new Polyline(), Type.CUSTOM);

        drawLine(xStart, yStart, customLines);

        arrowHead.getStyleClass().add("cpu-arrowhead");
        line.getStyleClass().add("cpu-line");

        getChildren().addAll(line, arrowHead);
    }

    /**
     * Draws a line according to the custom lines
     * @param xStart The starting x position
     * @param yStart The starting y position
     * @param customLines The custom lines to draw
     */
    public void drawLine(double xStart, double yStart, CustomLine... customLines){
        line.getPoints().clear();
        arrowHead.getPoints().clear();
        line.getPoints().add(xStart);
        line.getPoints().add(yStart);

        CustomLine.Direction finalDirection = CustomLine.Direction.UP;

        for (CustomLine p : customLines){

            switch (p.getDirection()){
                case UP:
                    yStart -= p.getDistance();
                    break;
                case DOWN:
                    yStart += p.getDistance();
                    break;
                case RIGHT:
                    xStart += p.getDistance();
                    break;
                case LEFT:
                    xStart -= p.getDistance();
            }

            line.getPoints().add(xStart);
            line.getPoints().add(yStart);

            finalDirection = p.getDirection();
        }

        switch (finalDirection){
            case UP:
                arrowHead.getPoints().addAll(
                        xStart, yStart,
                        xStart + 10, yStart + 10,
                        xStart - 10, yStart + 10,
                        xStart, yStart);
                break;
            case DOWN:
                arrowHead.getPoints().addAll(
                        xStart, yStart,
                        xStart - 10, yStart - 10,
                        xStart + 10, yStart - 10,
                        xStart, yStart);
                break;
            case RIGHT:
                arrowHead.getPoints().addAll(
                        xStart, yStart,
                        xStart - 10, yStart - 10,
                        xStart - 10, yStart + 10,
                        xStart, yStart);
                break;
            case LEFT:
                arrowHead.getPoints().addAll(
                        xStart, yStart,
                        xStart + 10, yStart - 10,
                        xStart + 10, yStart + 10,
                        xStart, yStart);
                break;
        }
        reanimateData();
    }

}