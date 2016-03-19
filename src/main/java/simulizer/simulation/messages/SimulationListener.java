package simulizer.simulation.messages;

import java.util.concurrent.atomic.LongAdder;

/**
 * Receives messages about the internal workings of the simulation as it runs
 * @author mbway
 */
public abstract class SimulationListener {

    void delegateMessage(Message m) {
        processMessage(m);

        if (m instanceof AnnotationMessage) {
            processAnnotationMessage((AnnotationMessage) m);
        } else if (m instanceof DataMovementMessage) {
            processDataMovementMessage((DataMovementMessage) m);
        } else if (m instanceof InstructionTypeMessage) {
            processInstructionTypeMessage((InstructionTypeMessage) m);
        } else if (m instanceof PipelineHazardMessage) {
            processPipelineHazardMessage((PipelineHazardMessage) m);
        } else if (m instanceof PipelineStateMessage) {
            processPipelineStateMessage((PipelineStateMessage) m);
        } else if (m instanceof ProblemMessage) {
            processProblemMessage((ProblemMessage) m);
        } else if (m instanceof RegisterChangedMessage) {
            processRegisterChangedMessage((RegisterChangedMessage) m);
        } else if (m instanceof SimulationMessage) {
            processSimulationMessage((SimulationMessage) m);
        } else if (m instanceof StageEnterMessage) {
            processStageEnterMessage((StageEnterMessage) m);
        }
    }

    /**
     * Process any messages
     */
    public void processMessage(Message m) {}

    public void processAnnotationMessage(AnnotationMessage m) {}
    public void processDataMovementMessage(DataMovementMessage m) {}
    public void processInstructionTypeMessage(InstructionTypeMessage m) {}
    public void processPipelineHazardMessage(PipelineHazardMessage m) {}
    public void processProblemMessage(ProblemMessage m) {}
    public void processRegisterChangedMessage(RegisterChangedMessage m) {}
    public void processSimulationMessage(SimulationMessage m) {}
    public void processStageEnterMessage(StageEnterMessage m) {}
    public void processPipelineStateMessage(PipelineStateMessage m) {}
}
