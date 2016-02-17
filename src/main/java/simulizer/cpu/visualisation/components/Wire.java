package simulizer.cpu.visualisation.components;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.DoubleBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Observable;
import java.util.Observer;

public class Wire extends Group {

    public enum Type{
        HORIZONTAL,
        VERTICAL
    }

    ComponentStackPane from;
    ComponentStackPane to;
    Polyline line;
    Polyline arrowHead;
    double offset;
    boolean rightOrBottom;
    boolean arrowStart;
    Type type;

    public Wire(ComponentStackPane from, ComponentStackPane to, Type type, boolean rightOrBottom, boolean arrowStart, double offset){
        this.from = from;
        this.to = to;
        this.line = new Polyline();
        this.arrowHead = new Polyline();
        this.arrowStart = arrowStart;
        this.type = type;

        this.rightOrBottom = rightOrBottom;
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

    public void updateLine(){
        if(this.type == Type.HORIZONTAL){
            drawHorizontalWire();
        } else {
            drawVerticalLine();
        }
    }

    public void drawHorizontalWire(){
        line.getPoints().clear();
        arrowHead.getPoints().clear();

        DoubleBinding xStart = from.layoutXProperty().add(from.getShapeWidth());
        DoubleBinding yStart = from.layoutYProperty().add(from.getShapeHeight() / 2).add(from.getShapeHeight() * offset);
        DoubleBinding xEnd = to.layoutXProperty().add(0);
        DoubleBinding yEnd = from.layoutYProperty().add(from.getShapeHeight() / 2).add(from.getShapeHeight() * offset);

        if(!rightOrBottom){
            // Goes from right to left
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

    public void drawVerticalLine(){
        line.getPoints().clear();
        arrowHead.getPoints().clear();
        DoubleBinding xStart = from.layoutXProperty().add(from.getShapeWidth() / 2).add(from.getShapeWidth() * offset);
        DoubleBinding yStart = from.layoutYProperty().add(from.getShapeHeight());
        DoubleBinding xEnd = from.layoutXProperty().add(from.getShapeWidth() / 2).add(from.getShapeWidth() * offset);
        DoubleBinding yEnd = to.layoutYProperty().add(0);


        if(!rightOrBottom){
            //Bottom to top
            yStart = from.layoutXProperty().add(0);
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

    public void animateData(int time, boolean from){

        Path path = new Path();
        PathTransition pathTransition = new PathTransition();

        Circle circle = new Circle(0, 0, 5);
        circle.getStyleClass().addAll("cpu-data");

        path.getElements().add(new MoveTo(line.getPoints().get(0), line.getPoints().get(1)));

        for(int i = 2; i < line.getPoints().size(); i += 2){
            double x = line.getPoints().get(i);
            double y = line.getPoints().get(i+1);
            path.getElements().add (new LineTo(x, y));
        }

        if(from){
            path.getElements().clear();

            path.getElements().add(new MoveTo(line.getPoints().get(line.getPoints().size() - 2), line.getPoints().get(line.getPoints().size() - 1)));

            for(int i = line.getPoints().size() - 3; i > 0; i -= 2){
                double y = line.getPoints().get(i);
                double x = line.getPoints().get(i-1);
                path.getElements().add (new LineTo(x, y));
            }

        }

        pathTransition.setDuration(Duration.millis(time * 1000));
        pathTransition.setNode(circle);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        this.getChildren().addAll(circle);
        pathTransition.play();

        Wire outer = this;

        pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FadeTransition ft = new FadeTransition(Duration.millis(300), circle);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);

                ft.play();

                ft.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        outer.getChildren().remove(circle);
                    }
                });

            }
        });


    }

}
