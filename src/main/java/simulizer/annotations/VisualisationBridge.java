package simulizer.annotations;

import simulizer.highlevel.models.DataStructureModel;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	WindowManager wm;

	public DataStructureModel load(String visualisationName) {
		return load(visualisationName, true);
	}

	public DataStructureModel load(String visualisationName, boolean showNow) {
		// TODO: ThreadUtil.runAndWait()
		if (showNow) show();

		DataStructureModel output = wm.getHLVisualManager().create(visualisationName, showNow);

		return output;

	}

	public void show() {
		wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
	}

	public void hide() {
		InternalWindow window = wm.getWorkspace().findInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		if (window != null)
			window.close();
	}
}
