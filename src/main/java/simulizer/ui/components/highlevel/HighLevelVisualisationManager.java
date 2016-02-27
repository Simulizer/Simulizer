package simulizer.ui.components.highlevel;

import simulizer.annotations.AnnotationExecutor;
import simulizer.ui.WindowManager;


/**
 * Holds data regarding the processing of annotations and display of visualisations
 */
public class HighLevelVisualisationManager {
	private WindowManager wm;
	public AnnotationExecutor ex;

	public HighLevelVisualisationManager(WindowManager wm) {
		this.wm = wm;
		ex = new AnnotationExecutor();
	}

}
