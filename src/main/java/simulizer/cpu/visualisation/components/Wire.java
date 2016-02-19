package simulizer.cpu.visualisation.components;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.Timer;
import java.util.TimerTask;

public class Wire extends Group {

    public enum Type{
        HORIZONTAL,
        VERTICAL,
        CUSTOM
    }

    Polyline line;
    Polyline arrowHead;
    Type type;
    Circle data;
    PathTransition pathTransition;
    Path path;
    int time;
    double progressed;
    boolean from;
    boolean animating;
    Timer progressing;

    public Wire(Polyline line, Polyline arrowHead, Type type){
        this.line = line;
        this.arrowHead = arrowHead;
        this.type = type;
        this.animating = false;
        this.progressing = new Timer();
    }

    public void reanimateData(){
        if(!animating) return;
        progressing.cancel();
        pathTransition.stop();

        setUpAnimationPath();

        pathTransition.playFrom(new Duration(progressed));

        progressAnimation();
    }

    public void setUpAnimationPath(){
        path.getElements().clear();

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
        pathTransition.setPath(path);
    }

    public void animateData(int time, boolean from){
        this.animating = true;
        path = new Path();
        pathTransition = new PathTransition();
        this.time = time;
        this.from = from;

        data = new Circle(0, 0, 5);
        data.getStyleClass().addAll("cpu-data");



        setUpAnimationPath();

        pathTransition.setDuration(Duration.millis(time * 1000));
        pathTransition.setNode(data);
        pathTransition.setPath(path);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(1);
        pathTransition.setAutoReverse(false);

        this.getChildren().addAll(data);
        pathTransition.play();

        pathTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FadeTransition ft = new FadeTransition(Duration.millis(300), data);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
                animating = false;
                progressed = 0;

                ft.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        getChildren().remove(data);
                    }
                });

            }
        });

        progressAnimation();

    }

    public void progressAnimation(){
        this.progressing = new Timer();
        progressing.schedule(new TimerTask() {
            @Override
            public void run() {
                progressed++;
            }
        }, 0, 1);
    }
}
