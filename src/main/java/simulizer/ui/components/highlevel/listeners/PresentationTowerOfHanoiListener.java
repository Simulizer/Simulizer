package simulizer.ui.components.highlevel.listeners;

import simulizer.simulation.listeners.AnnotationMessage;
import simulizer.simulation.listeners.SimulationListener;
import simulizer.ui.components.highlevel.PresentationTowerOfHanoiVisualiser;

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
