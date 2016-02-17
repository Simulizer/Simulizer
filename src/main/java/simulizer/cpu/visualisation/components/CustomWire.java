package simulizer.cpu.visualisation.components;

import javafx.scene.Group;
import javafx.scene.shape.Polyline;

public class CustomWire extends Group {

    Polyline line;
    Polyline arrowHead;

    public CustomWire(double xStart, double yStart, CustomLine... customLines){

        line = new Polyline();
        arrowHead = new Polyline();

        drawLine(xStart, yStart, customLines);

        arrowHead.getStyleClass().add("cpu-arrowhead");
        line.getStyleClass().add("cpu-line");

        getChildren().addAll(line, arrowHead);
    }

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
                arrowHead.getPoints().addAll(new Double[]{
                        xStart, yStart,
                        xStart + 10, yStart + 10,
                        xStart - 10, yStart + 10,
                        xStart, yStart,
                });
                break;
            case DOWN:
                arrowHead.getPoints().addAll(new Double[]{
                        xStart, yStart,
                        xStart - 10, yStart - 10,
                        xStart + 10, yStart - 10,
                        xStart, yStart,
                });
                break;
            case RIGHT:
                arrowHead.getPoints().addAll(new Double[]{
                        xStart, yStart,
                        xStart - 10, yStart - 10,
                        xStart - 10, yStart + 10,
                        xStart, yStart,
                });
                break;
            case LEFT:
                arrowHead.getPoints().addAll(new Double[]{
                        xStart, yStart,
                        xStart + 10, yStart - 10,
                        xStart + 10, yStart + 10,
                        xStart, yStart,
                });
                break;
        }
    }

}
