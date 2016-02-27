package simulizer.annotations;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import simulizer.assembler.representation.Annotation;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.utils.FileUtils;

import javax.script.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * A JavaScript interpreter for executing annotations and coordinating the high level visualisations.
 *
 * Nashorn reference: https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/toc.html
 *
 * @author mbway
 */
public class AnnotationExecutor {

	private ScriptEngine engine;
	private ScriptContext context; // the scope to run in
	private Bindings globals;

	DebugBridge debugBridge;
	SimulationBridge simulationBridge;
	VisualisationBridge visualisationBridge;

	private class AnnotationClassFilter implements ClassFilter {
		@Override public boolean exposeToScripts(String s) {
			throw new SecurityException("Access to Java objects is restricted from annotations");
		}
	}

	public AnnotationExecutor() {
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		engine = factory.getScriptEngine(new AnnotationClassFilter());
		context = new SimpleScriptContext();

		debugBridge = new DebugBridge();
		simulationBridge = new SimulationBridge();
		visualisationBridge = new VisualisationBridge();

		globals = new SimpleBindings();

		globals.put("debug", debugBridge);
		globals.put("simulation", simulationBridge);
		globals.put("visualisation", visualisationBridge);
		context.setBindings(globals, ScriptContext.GLOBAL_SCOPE);

		engine.setContext(context);

		try {
			engine.eval(FileUtils.getResourceContent("/annotations/load-api.js"));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}

	public void exec(Annotation annotation) throws ScriptException, SecurityException {
		engine.eval(annotation.code);
		promoteToGlobal();
	}

	/**
	 * set all variables local to a script become global
	 */
	private void promoteToGlobal() {
		Bindings e = context.getBindings(ScriptContext.ENGINE_SCOPE);
		globals.putAll((ScriptObjectMirror)e.get("nashorn.global"));
		e.clear();
	}

	/**
	 * use stdout and stdin as a javascript REPL
	 */
	public void debugREPL(IO io) {
		try {
			io.printString("REPL start\n");
			globals.put("io", io);
			engine.eval("print = function(o) {io.printString('' + o + '\\n');};");
			engine.eval("exit = function() {stop = true;}; stop = false;");
			promoteToGlobal();

			String line;
			while(!(Boolean)globals.get("stop")) {
				io.printString("js> ");
				line = io.readString();
				exec(new Annotation(line));
			}
			io.printString("REPL stop\n");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
}
