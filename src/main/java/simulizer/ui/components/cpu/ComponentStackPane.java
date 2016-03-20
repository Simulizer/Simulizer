package simulizer.ui.components.cpu;

import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import simulizer.ui.windows.CPUVisualisation;

/**
 * Used to represents each component along with the label
 * @author Theo Styles
 */
public class ComponentStackPane extends StackPane {

    private Shape shape;
    private Text text;
    private double x;
    private double y;
    private double width;
    private double height;
    private CPUVisualisation vis;
    //boolean focused = false;

    /**
     * Sets the visualisation and sets up the label
     * @param vis The cpu visualisation
     * @param label The label for the component
     */
    public ComponentStackPane(CPUVisualisation vis, String label){
        this.vis = vis;
        this.text = new Text(label);
    }

    /**
     * Sets attributes on the shape and stack pane such as x and y coordinates.
     */
    public void setAttributes(){
        this.setPrefHeight(height);
        this.setPrefWidth(width);
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.text.setWrappingWidth(width * 0.9);
        this.shape.getStyleClass().addAll("cpu-component", this.getClass().getSimpleName());
        this.text.getStyleClass().addAll("cpu-component-label", this.getClass().getSimpleName());
        this.getStyleClass().addAll("cpu-container");
        getChildren().addAll(this.shape, text);
        setAlignment(this.shape, Pos.TOP_LEFT);
        setCache(true);
        setCacheShape(true);
        setCacheHint(CacheHint.SPEED);
        this.shape.setCache(true);
        this.shape.setCacheHint(CacheHint.SPEED);
    }

    /**
     * Gets the component shape
     * @return The component shape
     */
    public Shape getComponentShape(){
        return shape;
    }

    /**
     * Gets the component label
     * @return The component label
     */
    public Text getComponentLabel(){ return text; }

    /**
     * Gets the stack pane height
     * @return The stack pane height
     */
    public double getShapeHeight(){
        return height;
    }

    /**
     * Gets the stack pane width
     * @return The stack pane width
     */
    public double getShapeWidth(){
        return width;
    }

    /**
     * Gets the x coordinate of the stack pane
     * @return The x coordinate
     */
    public double getX(){ return x; }

    /**
     * Gets the y coordinate of the stack pane
     * @return The y coordinate
     */
    public double getY(){ return y; }

    /**
     * Sets the x coordinate for the stack pane
     * @param x The x coordinate
     */
    public void setX(double x){ this.x = x; }

    /**
     * Sets the y coordinate for the stack pane
     * @param y The y coordinate
     */
    public void setY(double y){ this.y = y; }

    /**
     * Sets the width of the shape
     * @param width The width of the shape
     */
    public void setShapeWidth(double width) {
        this.width = width;
    }

    /**
     * Sets the height of the shape
     * @param height The height of the shape
     */
    public void setShapeHeight(double height){
        this.height = height;
    }

    /**
     * Sets the component label
     * @param label The component label
     */
    public void setLabel(String label){
        Platform.runLater(() -> text.setText(label));
    }

    /**
     * Sets the shape
     * @param newShape The new shape to set
     */
    public void setComponentShape(Shape newShape){ this.shape = newShape; }

    /**
     * Draws a horizontal line to another component
     * @param shape The shape to connect to
     * @param right If the arrow starts on the right
     * @param arrowStart If the arrow should be going to this shape or the other shape
     * @param offset The offset of the line to multiply (0-1)
     * @return The connecting wire
     */
    public ConnectorWire horizontalLineTo(ComponentStackPane shape, boolean right, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.HORIZONTAL, right, arrowStart, offset);
    }

    /**
     * Draws a vertical line to another component
     * @param shape The shape to connect to
     * @param bottom If the arrow starts on the bottom
     * @param arrowStart If the arrow should be going to this shape or the other shape
     * @param offset The offset of the line to multiply (0-1)
     * @return The connecting wire
     */
    public ConnectorWire verticalLineTo(ComponentStackPane shape, boolean bottom, boolean arrowStart, double offset){
        return new ConnectorWire(this, shape, ConnectorWire.Type.VERTICAL, bottom, arrowStart, offset);
    }

    /**
     * Highlights the shape to a red colour and back
     */
    public void highlight(){
        Platform.runLater(() -> {
			FillTransition ft = new FillTransition(Duration.millis(100), shape, Color.valueOf("#1e3c72"), Color.RED);
			ft.setCycleCount(2);
			ft.setAutoReverse(true);
			ft.play();
		});
    }

    /**
     * Sets the tooltip for the component
     * @param text The tooltip to use
     */
    public void setTooltip(String text){
        final ComponentStackPane instance = this;
        final Tooltip tooltip = new Tooltip(text);
        tooltip.setAutoHide(true);
        tooltip.setWrapText(true);
        tooltip.setPrefWidth(vis.getWindowWidth() / 2);

        addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            double eventX = vis.getScene().getWindow().getX() + vis.getLayoutX() + getLayoutX() + event.getX() - (tooltip.getWidth() / 2.5);
			double eventY = vis.getScene().getWindow().getY() + vis.getLayoutY() + getLayoutY() + event.getY() - (tooltip.getHeight());
            tooltip.show(instance, eventX, eventY);
		});

        addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, event -> {
            tooltip.hide();
        });

    }

}