package simulizer.simulation.messages;

/**
 * A message detailing a problem with the simulation
 */
public class ProblemMessage extends Message {
    public String message;

    public ProblemMessage(String message) {
        this.message = message;
    }
}
