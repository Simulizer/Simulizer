package simulizer.annotations;

import java.util.concurrent.ExecutionException;

import simulizer.ui.WindowManager;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.utils.ThreadUtils;

/**
 * A collection of methods for controlling high level visualisations from annotations
 */
@SuppressWarnings("unused")
public class VisualisationBridge {
	// package-visible Attributes not visible from JavaScript
	// set package-visible attributes using BridgeFactory
	WindowManager wm;

	public DataStructureVisualiser load(String visualisationName) {
		return load(visualisationName, true);
	}
	public DataStructureVisualiser load(String visualisationName, boolean showNow) {
		try {
			HighLevelVisualisation vis = (HighLevelVisualisation) wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);

			switch (visualisationName) {
				case "tower-of-hanoi":
					ThreadUtils.platformRunAndWait(vis::loadTowerOfHanoiVisualisation);
					vis.setVisible(showNow);
					return vis.getVisualiser();
				case "list":
					ThreadUtils.platformRunAndWait(vis::loadListVisualisation);
					vis.setVisible(showNow);
					return vis.getVisualiser();
				case "frame":
					ThreadUtils.platformRunAndWait(vis::loadFrameVisualisation);
					vis.setVisible(showNow);
					return vis.getVisualiser();
				default:
					throw new IllegalArgumentException();
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void show() {
		HighLevelVisualisation vis = (HighLevelVisualisation) wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		vis.setVisible(true);
	}
	public void hide() {
		HighLevelVisualisation vis = (HighLevelVisualisation) wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		vis.setVisible(false);
	}
}
