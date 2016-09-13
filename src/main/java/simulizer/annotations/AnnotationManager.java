package simulizer.annotations;

import javax.script.ScriptException;

import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.simulation.messages.AnnotationMessage;
import simulizer.ui.WindowManager;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.utils.UIUtils;

/**
 * Holds data regarding the processing of annotations and display of visualisations
 *
 * @author mbway
 */
@SuppressWarnings("WeakerAccess") // because bridges accessed from javascript
public class AnnotationManager {
	private WindowManager wm;
	private AnnotationExecutor ex;
	private static final boolean giveDetailedInfo = true;

	DebugBridge debugBridge;
	SimulationBridge simulationBridge;
	VisualisationBridge visualisationBridge;

	public AnnotationManager(WindowManager wm) {
		this.wm = wm;
		ex = null;

		debugBridge = new DebugBridge();
		simulationBridge = new SimulationBridge();
		visualisationBridge = new VisualisationBridge();
	}

	/**
	 * get the current executor (script engine)
	 */
	public AnnotationExecutor getExecutor() {
		return ex;
	}

	/**
	 * refreshes the executor when a new CPU is created
	 * @param cpu the new CPU object
	 */
	public synchronized void onNewProgram(CPU cpu) {
		// refresh for each new program
		newExecutor();

		simulationBridge.cpu = cpu;
	}

	/**
	 * create the bridge objects that let the annotation executor interface with the rest of the system
	 */
	private synchronized void setupBridges() {
		// set up access between the bridges and the components they talk to on the Java side
		debugBridge.wm = wm;
		debugBridge.io = wm.getIO();

		simulationBridge.cpu = null;
		
		visualisationBridge.wm = wm;
		wm.getHLVisualManager().removeAll();
	}

	/**
	 * handle the end of the program by disabling some access of the bridges
	 */
	public void onEndProgram() {
		simulationBridge.cpu = null;
	}

	/**
	 * create a new annotation executor (with a fresh state)
	 */
	public synchronized void newExecutor() {
		ex = new AnnotationExecutor();

		ex.bindGlobal("debug", debugBridge);

		ex.bindGlobal("simulation", simulationBridge);
		ex.bindGlobal("sim", simulationBridge);

		ex.bindGlobal("visualisation", visualisationBridge);
		ex.bindGlobal("vis", visualisationBridge);

		setupBridges();
	}

	private String getAnnotationLineString(AnnotationMessage msg) {
		if(msg.boundAddress != null) {
			int lineNum = wm.getCPU().getProgram().lineNumbers.get(msg.boundAddress);
			return "the annotation bound to line: " + (lineNum+1) + ".";
		} else {
			return "the initial annotation.";
		}
	}

	/**
	 * extract the annotation from the message and send it to the executor to be executed
	 * @param msg the message containing the annotation to run
	 */
	public synchronized void processAnnotationMessage(AnnotationMessage msg) {
		try {
			ex.exec(msg.annotation);
		} catch(AnnotationEarlyReturn ignored) {
		} catch(AssertionError e) {
			IO io = wm.getIO();
			io.printString(IOStream.ERROR, "Assertion error:\n");
			io.printString(IOStream.ERROR, "  From " + getAnnotationLineString(msg) + "\n");
			io.printString(IOStream.ERROR, "  With the code: \"" + e.getMessage().trim() + "\"\n");
		} catch (ScriptException e) {
			IO io = wm.getIO();
			io.printString(IOStream.ERROR, "Annotation error: " + e.getMessage() + "\n");
			io.printString(IOStream.ERROR, "  From " + getAnnotationLineString(msg) + "\n");
			if(giveDetailedInfo)
				UIUtils.showExceptionDialog(e);
		}
	}
}
