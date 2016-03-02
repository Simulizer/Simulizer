package simulizer.annotations;

import java.util.concurrent.ExecutionException;

import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.utils.ThreadUtils;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	HighLevelVisualisation vis;

	public DataStructureVisualiser load(String visualisationName) {
		try {
			switch (visualisationName) {
				case "tower-of-hanoi":
					ThreadUtils.platformRunAndWait(vis::loadTowerOfHanoiVisualisation);
					return vis.getVisualiser();
				case "list":
					ThreadUtils.platformRunAndWait(vis::loadListVisualisation);
					return vis.getVisualiser();
				default:
					throw new IllegalArgumentException();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
}
