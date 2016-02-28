package simulizer.ui.components.highlevel;

import simulizer.annotations.*;
import simulizer.assembler.representation.Annotation;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Editor;
import simulizer.ui.windows.HighLevelVisualisation;
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

	HighLevelVisualisation vis;

	public HighLevelVisualisationManager(WindowManager wm) {
		this.wm = wm;
		ex = null;

		debugBridge = new DebugBridge();
		simulationBridge = new SimulationBridge();
		visualisationBridge = new VisualisationBridge();

		vis = null;
	}

	public AnnotationExecutor getExecutor() {
		return ex;
	}

	public void onStartProgram(CPU cpu) {
		// refresh for each new program
		newExecutor();

		// set up access between the bridges and the components they talk to on the Java side
		Logger logger = (Logger) wm.getWorkspace().findInternalWindow(WindowEnum.LOGGER);
		BridgeFactory.setDebugIO(debugBridge, logger);

		BridgeFactory.setSimulation(simulationBridge, cpu);

		vis = (HighLevelVisualisation) wm.getWorkspace().openInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
		BridgeFactory.setVisualisation(visualisationBridge, vis);
	}
	public void newExecutor() {
		ex = new AnnotationExecutor();

		ex.bindGlobal("debug", debugBridge);
		ex.bindGlobal("simulation", simulationBridge);
		ex.bindGlobal("visualisation", visualisationBridge);
	}

	public void processAnnotation(Annotation annotation) {
		try {
			ex.exec(annotation);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
