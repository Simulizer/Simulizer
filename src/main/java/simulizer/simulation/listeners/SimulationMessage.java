package simulizer.simulation.listeners;

/**
 * A message regarding the simulation state
 */
public class SimulationMessage extends Message {
	public enum Detail {
		SIMULATION_STARTED,
		SIMULATION_STOPPED
	}

	public Detail detail;

	public SimulationMessage(Detail detail) {
		this.detail = detail;
	}
}
