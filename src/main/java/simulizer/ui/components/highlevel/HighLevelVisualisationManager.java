package simulizer.ui.components.highlevel;

import simulizer.annotations.*;
import simulizer.assembler.representation.Annotation;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Logger;

import javax.script.ScriptException;

/**
 * Holds data regarding the processing of annotations and display of visualisations
 */
public class HighLevelVisualisationManager {
	private WindowManager wm;
	private AnnotationExecutor ex;

	DebugBridge debugBridge;
	SimulationBridge simulationBridge;
	VisualisationBridge visualisationBridge;

	public HighLevelVisualisationManager(WindowManager wm) {
		this.wm = wm;

		debugBridge = new DebugBridge();
		simulationBridge = new SimulationBridge();
		visualisationBridge = new VisualisationBridge();

		ex = new AnnotationExecutor();

		Logger logger = (Logger) wm.getWorkspace().findInternalWindow(WindowEnum.LOGGER);
		DataSetter.setDebugIO(debugBridge, logger);

		ex.bindGlobal("debug", debugBridge);
		ex.bindGlobal("simulation", simulationBridge);
		ex.bindGlobal("visualisation", visualisationBridge);
	}

	public AnnotationExecutor getExecutor() {
		return ex;
	}

	public void processAnnotation(Annotation annotation) {
		try {
			ex.exec(annotation);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
