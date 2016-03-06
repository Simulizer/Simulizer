package simulizer.ui.components.cpu;

import javafx.animation.FillTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulizer.ui.windows.CPUVisualisation;


public class ComponentStackPane extends StackPane {

    Shape shape;
    Text text;
    double x;
    double y;
    double width;
    double height;
    CPUVisualisation vis;
    boolean focused = false;

    public ComponentStackPane(CPUVisualisation vis, String label){
        this.vis = vis;
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
        this.getStyleClass().addAll("cpu-container");
        getChildren().addAll(shape, text);
        setAlignment(shape, Pos.TOP_LEFT);

    }

    public Shape getComponentShape(){
        return shape;
    }

    public double getShapeHeight(){
        return height;
    }

    public double getShapeWidth(){
        return width;
    }

    public double getX(){ return x; }

    public double getY(){ return y; }

    public void setX(double x){ this.x = x; }

    public void setY(double y){ this.y = y; }

    public void setShapeWidth(double width) {
        this.width = width;
    }

    public void setShapeHeight(double height){
        this.height = height;
    }

    public ConnectorWire horizontalLineTo(ComponentStackPane shape, boolean right, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.HORIZONTAL, right, arrowStart, offset);
    }

    public ConnectorWire verticalLineTo(ComponentStackPane shape, boolean bottom, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.VERTICAL, bottom, arrowStart, offset);
    }

    public void highlight(){
        FillTransition ft = new FillTransition(Duration.millis(100), shape, Color.valueOf("#1e3c72"), Color.RED);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();
    }

    public void setTooltip(String text){
        final ComponentStackPane instance = this;
        final Tooltip tooltip = new Tooltip(text);
        Tooltip.install(instance, tooltip);
        tooltip.setAutoHide(true);
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(350);

        vis.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double eventX = event.getX();
                double eventY = event.getY();
                double xMax = getX() + getShapeWidth();
                double yMax = getY() + getShapeHeight();

                if(!vis.getMainWindowManager().getCPU().isRunning() && eventX > getX() && eventX < xMax && eventY > getY() && eventY < yMax){
                    tooltip.setMaxWidth(vis.getWidth() - 40);
                    tooltip.setMaxHeight(vis.getHeight());
                    double x = vis.getScene().getWindow().getX() + vis.getLayoutX() + eventX - getShapeWidth() / 2;
                    double y = vis.getScene().getWindow().getY() + vis.getLayoutY() + eventY - getShapeHeight()/2 - 20;
                    tooltip.show(instance, x, y);
                } else {
                    tooltip.hide();
                }

            }
        });

    }

}
