package simulizer.simulation.messages;

/**
 * A message which is sent when the simulation enters a pipeline stage
 */
public class StageEnterMessage extends Message {
    public enum Stage {
        Fetch,
        Decode,
        Execute
    }

    Stage stage;

    public Stage getStage(){ return stage; }

    public StageEnterMessage(Stage stage) {
        this.stage = stage;
    }
}
