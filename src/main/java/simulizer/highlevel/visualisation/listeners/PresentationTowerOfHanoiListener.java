package simulizer.highlevel.visualisation.listeners;

import simulizer.highlevel.visualisation.PresentationTowerOfHanoiVisualiser;
import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.SimulationListener;

public class PresentationTowerOfHanoiListener extends SimulationListener {
	private PresentationTowerOfHanoiVisualiser tv;

	public PresentationTowerOfHanoiListener(PresentationTowerOfHanoiVisualiser tv) {
		this.tv = tv;
	}

	@Override
	public void processAnnotationMessage(AnnotationMessage m) {
		tv.nextMove();
	}
}
