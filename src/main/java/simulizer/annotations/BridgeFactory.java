package simulizer.annotations;

import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * To restrict access from JavaScript. This class is used to set package-visible attributes of the bridges.
 * Javascript does not have access to this class
 */
public class BridgeFactory {

	// DebugBridge
	public static void setDebugIO(DebugBridge db, IO io) {
		db.io = io;
	}

	// SimulationBridge
	public static void setSimulation(SimulationBridge sim, CPU cpu) {
		sim.cpu = cpu;
	}

	// VisualisationBridge
	public static void setVisualisation(VisualisationBridge visualisationBridge, HighLevelVisualisation vis) {
		visualisationBridge.vis = vis;
	}
}
