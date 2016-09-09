package simulizer.ui.components.cpu;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

/**
 * Represents the window showing the list of previous instructions
 * @author Theo Styles
 */
public class InstructionsWindow extends StackPane {

    private ObservableList<PreviousAnimation> instructions;

    /**
     * Represents a previous animation, contains the name and list of animations
     * @author Theo Styles
     */
    static class PreviousAnimation{

        public String name;
        ArrayList<AnimationProcessor.Animation> animations;

        PreviousAnimation(String name, ArrayList<AnimationProcessor.Animation> animations){
            this.name = name;
            this.animations = animations;
        }

    }

    /**
     * Represents a list cell containing a button
     * @author Theo Styles
     */
    private static class ButtonCell extends ListCell<PreviousAnimation> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Replay");
        PreviousAnimation lastItem;
        AnimationProcessor animationProcessor;

        /**
         * Sets up the layout and the button action
         * @param animationProcessor The animation processor to replay the animations on
         */
        ButtonCell(AnimationProcessor animationProcessor) {
            super();
            this.animationProcessor = animationProcessor;
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction((e) -> {
                animationProcessor.replayAnimations(lastItem.animations);
            });
        }

        /**
         * Sets up the cell label and button label
         * @param animation The animation for that cell
         * @param empty If the cell is empty or not
         */
        @Override
        protected void updateItem(PreviousAnimation animation, boolean empty) {
            super.updateItem(animation, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = animation;
                if (animation != null) {
                    label.setText(animation.name);
                    label.setFont(new Font("Arial", 14));
                    button.setFont(new Font("Arial", 14));
                }
                hbox.setAlignment(Pos.CENTER);
                setGraphic(hbox);
            }
        }
    }

    /**
     * Sets up the cell factory for the button and attaches an observable list to the list view
     * @param animationProcessor The animation processor to use for the button
     */
    public InstructionsWindow(AnimationProcessor animationProcessor){
        instructions = FXCollections.observableArrayList();
        ListView<PreviousAnimation> instructionsList = new ListView<>(instructions);
        getChildren().add(instructionsList);
        instructionsList.setCellFactory(e -> new ButtonCell(animationProcessor));
        instructionsList.setPlaceholder(new Label("No instructions to replay"));
    }

    /**
     * Adds an instruction to the list
     * @param name The instruction name to use for the label
     * @param animations The animations for the cell
     */
    void addInstruction(String name, ArrayList<AnimationProcessor.Animation> animations){
        Platform.runLater(() -> {
            instructions.add(0, new PreviousAnimation(name, animations));
            if(instructions.size() > 10)
                instructions.remove(instructions.size() - 1);
        });
    }

    /**
     * Removes an instruction from the list
     * @param animation The animation to remove
     */
    public void removeInstruction(PreviousAnimation animation){
        Platform.runLater(() -> {
            instructions.remove(animation);
        });
    }

    /**
     * Sets attributes for the shape, used when resizing
     * @param x The new x coordinate
     * @param y The new y coordinate
     * @param width The new width
     * @param height The new height
     */
    public void setAttrs(double x, double y, double width, double height) {
        setLayoutX(x);
        setLayoutY(y);
        setPrefWidth(width);
        setPrefHeight(height);
    }

}
