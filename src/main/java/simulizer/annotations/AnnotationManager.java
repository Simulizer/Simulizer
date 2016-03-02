package simulizer.annotations;

import javax.script.ScriptException;

import simulizer.assembler.representation.Annotation;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.WindowManager;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * Holds data regarding the processing of annotations and display of visualisations
 */
public class AnnotationManager {
	private WindowManager wm;
	private AnnotationExecutor ex;

	DebugBridge debugBridge;
	SimulationBridge simulationBridge;
	VisualisationBridge visualisationBridge;

	HighLevelVisualisation vis;

	public AnnotationManager(WindowManager wm) {
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
		debugBridge.wm = wm;
		debugBridge.io = wm.getIO();

		simulationBridge.cpu = cpu;

		visualisationBridge.wm = wm;
	}
	public void onEndProgram() {
		simulationBridge.cpu = null;
	}

	public void newExecutor() {
		ex = new AnnotationExecutor();

		ex.bindGlobal("debug", debugBridge);

		ex.bindGlobal("simulation", simulationBridge);
		ex.bindGlobal("sim", simulationBridge);

		ex.bindGlobal("visualisation", visualisationBridge);
		ex.bindGlobal("vis", visualisationBridge);
	}

	public void processAnnotation(Annotation annotation) {
		try {
			ex.exec(annotation);
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
