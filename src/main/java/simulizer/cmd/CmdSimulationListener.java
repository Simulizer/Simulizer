package simulizer.cmd;

import simulizer.simulation.messages.ProblemMessage;
import simulizer.simulation.messages.SimulationListener;

/**
 * Created by matthew on 06/09/16.
 */
public class CmdSimulationListener extends SimulationListener {

    public void processProblemMessage(ProblemMessage m) {
        System.err.print("problem: " + m.e.toString());
    }

}
