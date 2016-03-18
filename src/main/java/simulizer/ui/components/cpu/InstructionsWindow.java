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
import simulizer.simulation.messages.Message;

public class InstructionsWindow extends StackPane {

    ObservableList<PreviousAnimation> instructions;
    ListView<PreviousAnimation> instructionsList;
    AnimationProcessor animationProcessor;

    public class PreviousAnimation{

        public String name;
        public ArrayList<AnimationProcessor.Animation> animations;

        public PreviousAnimation(String name, ArrayList<AnimationProcessor.Animation> animations){
            this.name = name;
            this.animations = animations;
        }

    }

    static class ButtonCell extends ListCell<PreviousAnimation> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Replay");
        PreviousAnimation lastItem;
        AnimationProcessor animationProcessor;

        public ButtonCell(AnimationProcessor animationProcessor) {
            super();
            this.animationProcessor = animationProcessor;
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction((e) -> {
                animationProcessor.replayAnimations(lastItem.animations);
            });
        }

        @Override
        protected void updateItem(PreviousAnimation t, boolean empty) {
            super.updateItem(t, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = t;
                if (t != null) {
                    label.setText(t.name);
                    label.setFont(new Font("Arial", 14));
                    button.setFont(new Font("Arial", 14));
                }
                hbox.setAlignment(Pos.CENTER);
                setGraphic(hbox);
            }
        }
    }

    public InstructionsWindow(AnimationProcessor animationProcessor){
        instructions = FXCollections.observableArrayList();
        instructionsList = new ListView<PreviousAnimation>(instructions);
        getChildren().add(instructionsList);
        this.animationProcessor = animationProcessor;

        instructionsList.setCellFactory(e -> new ButtonCell(animationProcessor));
        instructionsList.setPlaceholder(new Label("No instructions to replay"));

    }

    public void addInstruction(String name, ArrayList<AnimationProcessor.Animation> animations){
        Platform.runLater(() -> {
            instructions.add(0, new PreviousAnimation(name, animations));
            if(instructions.size() > 10)
                instructions.remove(instructions.size() - 1);
        });
    }

    public void removeInstruction(Message message){
        Platform.runLater(() -> {
            instructions.remove(message);
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
