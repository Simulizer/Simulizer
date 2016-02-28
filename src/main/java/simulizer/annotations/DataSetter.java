package simulizer.annotations;

import simulizer.simulation.cpu.user_interaction.IO;

/**
 * To restrict access from JavaScript. This class is used to set package-visible attributes of the bridges.
 * Javascript does not have access to this class
 */
public class DataSetter {
	public static void setDebugIO(DebugBridge db, IO io) {
		db.io = io;
	}
}
