package simulizer.cpu.visualisation.components;

import javafx.animation.FillTransition;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.awt.*;

public class ComponentStackPane extends StackPane {

    Shape shape;
    Text text;
    int x;
    int y;
    int width;
    int height;

    public ComponentStackPane(int x, int y, int width, int height, String label){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = new Text(label);
    }

    public void setAttributes(){
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.text.setWrappingWidth(width * 0.9);
        this.shape.getStyleClass().addAll("cpu-component", this.getClass().getSimpleName());
        this.text.getStyleClass().addAll("cpu-component-label", this.getClass().getSimpleName());
        getChildren().addAll(shape, text);
        setAlignment(shape, Pos.TOP_LEFT);
    }

    public double getShapeHeight(){
        return height;
    }

    public double getShapeWidth(){
        return width;
    }

    public Group connect(ComponentStackPane shape, boolean top, boolean arrowStart){
        return connect(shape, top, arrowStart, 0);
    }

    public Group connect(ComponentStackPane shape, boolean top, boolean arrowStart, int offset){
        Group lineAndArrow = new Group();
        Line line = new Line();
        line.startXProperty().bind(this.layoutXProperty().add(width / 2).add(offset));

        Polyline arrowHead = new Polyline();
        double originX = this.getLayoutX() + (width / 2) + offset;
        double originY;
        double destY;
        if(top){
            originY = this.getLayoutY() + getShapeHeight();
            destY = shape.getLayoutY();
        } else {
            originY = this.getLayoutY() + getShapeHeight();
            destY = shape.getLayoutY() + shape.getShapeHeight();
        }

        if(arrowStart){
            if(top){
                arrowHead.getPoints().addAll(new Double[]{
                        originX, originY,
                        originX + 10, originY + 10,
                        originX - 10, originY + 10,
                        originX, originY
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        originX, originY,
                        originX - 10, originY - 10,
                        originX + 10, originY - 10,
                        originX, originY
                });
            }

        } else {

            if(top){
                arrowHead.getPoints().addAll(new Double[]{
                        originX, destY,
                        originX - 10, destY - 10,
                        originX + 10, destY - 10,
                        originX, destY
                });
            } else {
                arrowHead.getPoints().addAll(new Double[]{
                        originX, destY,
                        originX + 10, destY + 10,
                        originX - 10, destY + 10,
                        originX, destY
                });
            }

        }



        line.endXProperty().bind(this.layoutXProperty().add(width / 2).add(offset));


        if(top){
            line.startYProperty().bind(this.layoutYProperty().add(getShapeHeight()));
            line.endYProperty().bind(shape.layoutYProperty());
        } else {
            line.startYProperty().bind(this.layoutYProperty());
            line.endYProperty().bind(shape.layoutYProperty().add(shape.getShapeHeight()));
        }

        lineAndArrow.getChildren().addAll(line, arrowHead);
        arrowHead.setFill(javafx.scene.paint.Paint.valueOf("#000"));
        return lineAndArrow;
    }

    public void highlight(int n){
        FillTransition ft = new FillTransition(Duration.millis(300), shape, Color.WHITE, Color.RED);
        FillTransition tt = new FillTransition(Duration.millis(300), text, Color.BLACK, Color.WHITE);
        ft.setCycleCount(n);
        ft.setAutoReverse(true);
        ft.play();

        tt.setCycleCount(n);
        tt.setAutoReverse(true);
        tt.play();
    }

}
