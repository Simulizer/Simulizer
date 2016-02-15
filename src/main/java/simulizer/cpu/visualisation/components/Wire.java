package simulizer.cpu.visualisation.components;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Observable;
import java.util.Observer;

public class Wire extends Group implements Observer {

    Polyline line;
    Polyline arrowHead;

    public Wire(Polyline line, Polyline arrowHead){
        this.line = line;
        this.arrowHead = arrowHead;
        this.getChildren().addAll(line, arrowHead);
    }

    public void setLine(Polyline line){
        this.line = line;
    }

    public void setArrowHead(Polyline arrowHead){
        this.arrowHead = arrowHead;
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

    public void update(Observable obs, Object obj){

    }

}
