package simulizer.simulation.messages;

/**
 * A message detailing a problem with the simulation
 * @author charlie street
 */
public class ProblemMessage extends Message {
    public Exception e;

    public ProblemMessage(Exception e) {
        this.e = e;
    }
}
