package simulizer.annotations;

import simulizer.ui.WindowManager;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	WindowManager wm;

	public DataStructureVisualiser load(String visualisationName) {
		return load(visualisationName, true);
	}

	public DataStructureVisualiser load(String visualisationName, boolean showNow) {
		// TODO: ThreadUtil.runAndWait()
		HighLevelVisualisation vis = (HighLevelVisualisation) wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		DataStructureVisualiser output = vis.openVisualisation(visualisationName, showNow);
		return output;

	}
}
