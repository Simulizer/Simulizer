package simulizer.annotations;

import simulizer.highlevel.models.DataStructureModel;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;

/**
 * A collection of methods for controlling high level visualisations from annotations
 *
 * @author mbway
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	WindowManager wm;

	public DataStructureModel load(String visualisationName) {
		DataStructureModel m = wm.getHLVisualManager().create(visualisationName);
		m.show();
		return m;
	}

	public DataStructureModel loadHidden(String visualisationName) {
		DataStructureModel m = wm.getHLVisualManager().create(visualisationName);
		m.hide();
		return m;
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
