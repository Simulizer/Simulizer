package simulizer.annotations;

import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.HLVisualManager;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	HLVisualManager visMan;

	public DataStructureModel load(String visualisationName) {
		return load(visualisationName, true);
	}

	public DataStructureModel load(String visualisationName, boolean showNow) {
		// TODO: ThreadUtil.runAndWait()
		DataStructureModel output = visMan.create(visualisationName, showNow);
		return output;

	}
	
}
