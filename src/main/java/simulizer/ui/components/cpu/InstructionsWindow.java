package simulizer.ui.components.cpu;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.util.Callback;
import simulizer.simulation.messages.DataMovementMessage;
import simulizer.simulation.messages.InstructionTypeMessage;
import simulizer.simulation.messages.Message;
import simulizer.simulation.messages.StageEnterMessage;
import simulizer.ui.components.cpu.listeners.CPUListener;

public class InstructionsWindow extends StackPane {

    ObservableList<Message> instructions;
    ListView<Message> instructionsList;
    CPUListener cpuListener;

    static class ButtonCell extends ListCell<Message> {
        HBox hbox = new HBox();
        Label label = new Label("(empty)");
        Pane pane = new Pane();
        Button button = new Button("Replay");
        Message lastItem;
        CPUListener cpuListener;

        public ButtonCell(CPUListener cpuListener) {
            super();
            this.cpuListener = cpuListener;
            hbox.getChildren().addAll(label, pane, button);
            HBox.setHgrow(pane, Priority.ALWAYS);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    cpuListener.replayInstruction(lastItem);
                }
            });
        }

        @Override
        protected void updateItem(Message t, boolean empty) {
            super.updateItem(t, empty);
            setText(null);  // No text in label of super class
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = t;
                if (t != null) {
                    if (t instanceof DataMovementMessage){
                        label.setText(((DataMovementMessage) t).getInstruction().get().getInstruction().name());
                    } else if(t instanceof InstructionTypeMessage){
                        label.setText(((InstructionTypeMessage) t).getMode().name());
                    } else if(t instanceof StageEnterMessage){
                        label.setText(((StageEnterMessage) t).getStage().name());
                    }
                    label.setFont(new Font("Arial", 14));
                    button.setFont(new Font("Arial", 14));
                }
                hbox.setAlignment(Pos.CENTER);
                setGraphic(hbox);
            }
        }
    }

    public InstructionsWindow(){
        instructions = FXCollections.observableArrayList();
        instructionsList = new ListView<Message>(instructions);
        getChildren().add(instructionsList);

        instructionsList.setCellFactory(new Callback<ListView<Message>, ListCell<Message>>(){

            @Override
            public ListCell<Message> call(ListView<Message> p) {

                return new ButtonCell(cpuListener);
            }
        });

        instructionsList.setPlaceholder(new Label("No instructions to replay"));

    }

    public void attachListener(CPUListener cpuListener){
        this.cpuListener = cpuListener;
    }

    public void addInstruction(Message message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                instructions.add(0, message);

                if(instructions.size() > 10){
                    instructions.remove(instructions.size() - 1);
                }
            }
        });
    }

    public void removeInstruction(Message message){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                instructions.remove(message);
            }
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
