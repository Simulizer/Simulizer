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

    public void highlight(int n){
        FillTransition ft = new FillTransition(Duration.millis(300), shape, Color.valueOf("#1e3c72"), Color.RED);
        ft.setCycleCount(n);
        ft.setAutoReverse(true);
        ft.play();
    }

    public void setTooltip(String text){
        final ComponentStackPane instance = this;
        final Tooltip tooltip = new Tooltip(text);
        Tooltip.install(instance, tooltip);
        tooltip.setAutoHide(true);
        tooltip.setWrapText(true);

        vis.addEventFilter(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double eventX = event.getX();
                double eventY = event.getY();
                double xMax = getLayoutX() + width;
                double yMax = getLayoutY() + height;

                if( eventX > getLayoutX() && eventX < xMax && eventY > getLayoutY() && eventY < yMax){
                    tooltip.setMaxWidth(vis.getWidth() - 40);
                    tooltip.setMaxHeight(vis.getHeight());
                    tooltip.show(instance, vis.getLayoutX() + eventX, vis.getLayoutY() + eventY);
                } else {
                    tooltip.hide();
                }

            }
        });

    }

}
