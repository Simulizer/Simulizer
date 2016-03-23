package simulizer.simulation.messages;

/**
 * A message detailing a problem with the simulation
 * @author Charlie Street
 */
public class ProblemMessage extends Message {
    public Exception e;

    public ProblemMessage(Exception e) {
        this.e = e;
    }
}
