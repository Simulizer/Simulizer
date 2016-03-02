package simulizer.ui.components.cpu;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polyline;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import simulizer.ui.windows.CPUVisualisation;

public class ALU extends ComponentStackPane {


    public ALU(CPUVisualisation vis, String label){
        super(vis, label);
        drawShape(new Polyline());
        setAttributes();
        text.setTextAlignment(TextAlignment.RIGHT);
    }

    public void drawShape(Polyline polyline){
        double baseX = x;
        double baseY = y;

        double rightHeight = height * 0.6;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.3;
        double leftHeight = (height - gapWidth) / 2;

        polyline.getPoints().clear();
        polyline.getPoints().addAll(new Double[]{
                baseX, baseY,
                baseX + width, baseY + rightSmallHeight,
                baseX + width, baseY + rightHeight + rightSmallHeight,
                baseX, baseY + height,
                baseX, baseY + gapWidth + leftHeight,
                baseX + (width * 0.2), baseY + leftHeight + (gapWidth / 2),
                baseX, baseY + leftHeight,
                baseX, baseY
        });

        this.shape = polyline;
    }

    public double getShapeHeight(){
        double rightHeight = height * 0.7;
        double rightSmallHeight = (height - rightHeight) / 2;

        double gapWidth = height * 0.1;
        double leftHeight = (height - gapWidth) / 2;

        return (height + (rightHeight + rightSmallHeight)) / 2;
    }

    public void setShapeWidth(double width){
        this.width = width;
    }

    public void setShapeHeight(double height){
        this.height = height;
    }

    public void setAttrs(double x, double y, double width, double height){
        setLayoutX(x);
        shape.setLayoutX(x);
        setLayoutY(y);
        shape.setLayoutY(y);
        setX(x);
        setY(y);
        setShapeWidth(width);
        setPrefWidth(width);
        setShapeHeight(height);
        setPrefHeight(height);
        text.setWrappingWidth(width);
        drawShape(((Polyline) this.shape));
    }

    public void zoomOnHover(){

        ComponentStackPane instance = this;

//        vis.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//
//                double eventX = event.getX();
//                double eventY = event.getY();
//                double xMax = getX() + getShapeWidth();
//                double yMax = getY() + getShapeHeight();
//
//                if( eventX > getX() && eventX < xMax && eventY > getY() && eventY < yMax){
//                    focused = !focused;
//                    System.out.println("FOCUSED " + focused);
//                    double scaleX = 2;
//                    double scaleY = 2;
//                    double translateX = 0 - (getLayoutX());
//                    double translateY = 0 - (getLayoutY() - (getShapeHeight() / 2));
//
//                    if(!focused){
//                        // Should zoom out
//                        scaleX = 0 - scaleX;
//                        scaleY = 0 - scaleY;
//                        translateX = 0;
//                        translateY = 0;
//                    }
//
//                    System.out.println("LAYOUT X BEFORE " + getX());
//
//                    System.out.println("SHAPE WIDTH " + getShapeWidth());
//
//                    System.out.println("TRANSLTED BY X " + Math.abs(translateX));
//
//                    System.out.println("NEEDED 300");
//
//
//                    ScaleTransition st = new ScaleTransition(Duration.millis(2000), vis.getCpu().allItems);
//                    st.setByX(scaleX);
//                    st.setByY(scaleY);
//                    st.setCycleCount(1);
//
//                    st.play();
//
//                    TranslateTransition tt = new TranslateTransition(Duration.millis(2000), vis.getCpu().allItems);
//                    tt.setToX(translateX);
//                    tt.setToY(translateY);
//                    tt.setCycleCount(1);
//
//                    tt.play();
//
//                    System.out.println("Layout x before is " + getLayoutX());
//
//                    st.setOnFinished(new EventHandler<ActionEvent>() {
//                        @Override
//                        public void handle(ActionEvent event) {
//                            System.out.println("Layout x is " + getLayoutX());
//                        }
//                    });
//                }
//            }
//        });
    }

}
