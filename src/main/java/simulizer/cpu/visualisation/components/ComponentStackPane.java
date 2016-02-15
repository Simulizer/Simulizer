package simulizer.cpu.visualisation.components;

import javafx.animation.FillTransition;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableNumberValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
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

    public Wire horizontalLineTo(ComponentStackPane shape, boolean left, boolean arrowStart, int offset){
        Polyline line = new Polyline();
        Polyline arrowHead = new Polyline();

        DoubleBinding xStart = this.layoutXProperty().add(getShapeWidth());
        DoubleBinding yStart = this.layoutYProperty().add(height / 2).add(offset);
        DoubleBinding xEnd = shape.layoutXProperty().add(0);
        DoubleBinding yEnd = this.layoutYProperty().add(height / 2).add(offset);

        if(!left){
            // Goes from right to left
            xStart = this.layoutXProperty().add(0);
            xEnd = shape.layoutXProperty().add(shape.getShapeWidth());

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

        arrowHead.getStyleClass().add("cpu-arrowhead");
        line.getStyleClass().add("cpu-line");

        line.getPoints().addAll(new Double[]{
                xStart.getValue(), yStart.getValue(),
                xEnd.getValue(), yEnd.getValue()
        });

        return new Wire(line, arrowHead);
    }

    public Wire verticalLineTo(ComponentStackPane shape, boolean top, boolean arrowStart, int offset){
        Polyline line = new Polyline();
        Polyline arrowHead = new Polyline();

        DoubleBinding xStart = this.layoutXProperty().add(getShapeWidth() / 2).add(offset);
        DoubleBinding yStart = this.layoutYProperty().add(getShapeHeight());
        DoubleBinding xEnd = this.layoutXProperty().add(getShapeWidth() / 2).add(offset);
        DoubleBinding yEnd = shape.layoutYProperty().add(0);


        if(!top){
            //Bottom to top
            yStart = this.layoutXProperty().add(0);
            yEnd = shape.layoutYProperty().add(shape.getShapeHeight());
        }

        line.getPoints().addAll(new Double[]{
                xStart.getValue(), yStart.getValue(),
                xEnd.getValue(), yEnd.getValue()
        });

        if(arrowStart){
            if(top){
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

            if(top){
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

        arrowHead.getStyleClass().add("cpu-arrowhead");
        line.getStyleClass().add("cpu-line");
        return new Wire(line, arrowHead);
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
