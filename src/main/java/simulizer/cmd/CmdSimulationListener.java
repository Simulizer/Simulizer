package simulizer.cmd;

import simulizer.annotations.AnnotationManager;
import simulizer.simulation.messages.AnnotationMessage;
import simulizer.simulation.messages.ProblemMessage;
import simulizer.simulation.messages.SimulationListener;

/**
 * Created by matthew on 06/09/16.
 */
public class CmdSimulationListener extends SimulationListener {
	private AnnotationManager a;

	int count = 0;

	public CmdSimulationListener(AnnotationManager a) {
		this.a = a;
	}

	@Override
	public void processAnnotationMessage(AnnotationMessage m) {
		// the annotations should all be completed before moving on to the next cycle
		if (a != null) {
			count++;
			a.processAnnotationMessage(m);
		}
	}

	@Override
	public void processProblemMessage(ProblemMessage m) {
		System.err.print("problem: " + m.e.toString());
	}

}
