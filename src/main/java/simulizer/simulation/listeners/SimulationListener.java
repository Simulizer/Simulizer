package simulizer.simulation.listeners;

/**
 * Receives messages about the internal workings of the simulation as it runs
 * @author mbway
 */
public abstract class SimulationListener {
    /**
     * Process all messages
     * @param m a message
     */
    public void processMessage(Message m) {}

    public void processAnnotationMessage(AnnotationMessage m) {}
    public void processDataMovementMessage(DataMovementMessage m) {}
    public void processExecuteStatementMessage(ExecuteStatementMessage m) {}
    public void processInstructionTypeMessage(InstructionTypeMessage m) {}
    public void processPipelineHazardMessage(PipelineHazardMessage m) {}
    public void processProblemMessage(ProblemMessage m) {}
    public void processRegisterChangedMessage(RegisterChangedMessage m) {}
    public void processSimulationMessage(SimulationMessage m) {}
    public void processStageEnterMessage(StageEnterMessage m) {}
    public void processPipelineStateMessage(PipelineStateMessage m) {}
}
