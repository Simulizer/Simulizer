package simulizer.annotations;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;
import javax.script.SimpleScriptContext;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.internal.runtime.ECMAException;
import simulizer.assembler.representation.Annotation;
import simulizer.assembler.representation.Register;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

/**
 * A JavaScript interpreter for executing annotations and coordinating the high level visualisations.
 *
 * Nashorn reference: https://docs.oracle.com/javase/8/docs/technotes/guides/scripting/nashorn/toc.html
 *
 * @author mbway
 */
public class AnnotationExecutor {

	private final NashornScriptEngine engine;
	private final Bindings globals;
	/**
	 * Nashorn globals object. flushed every time eval is called.
	 * elements are manually promoted to globals (which is persistent).
	 */
	private ScriptObjectMirror nhGlobals;


	private class AnnotationClassFilter implements ClassFilter {
		boolean apiLoaded = false;

		@Override public boolean exposeToScripts(String s) {
			if(apiLoaded) {
				throw new SecurityException("Access to Java objects from annotations (other than designated bridges) is disabled");
			} else {
				return true; // allow anything during the API load
			}
		}
	}

	/**
	 * create a new executor
	 */
	public AnnotationExecutor() {
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		AnnotationClassFilter filter = new AnnotationClassFilter();
		engine = (NashornScriptEngine) factory.getScriptEngine(filter);

		// the context to run in, defines the global and engine scopes
		ScriptContext context = new SimpleScriptContext();

		context.setReader(null); // prevent access to stdin
		context.setWriter(null); // prevent access to stdout
		context.setErrorWriter(null); // prevent access to stdout

		globals = new SimpleBindings();
		context.setBindings(globals, ScriptContext.GLOBAL_SCOPE);

		engine.setContext(context);

		Bindings engineLocals = context.getBindings(ScriptContext.ENGINE_SCOPE);

		try {
			engine.eval(""); // force the creation of NASHORN_GLOBAL
			nhGlobals = (ScriptObjectMirror) engineLocals.get(NashornScriptEngine.NASHORN_GLOBAL);

			loadAPI();
			filter.apiLoaded = true; // from now on restrict access to Java classes

		} catch (ScriptException e) {
			UIUtils.showExceptionDialog(e);
		}
	}

	private void loadAPI() throws ScriptException {
		exec(FileUtils.getResourceContent("/annotations/load-api.js"));

		// bind Register.xx to $xx with a get method to get the current value from the simulator
		StringBuilder registerGlobals = new StringBuilder();
		for(Register r : Register.values()) {
			String name = r.getName();
			registerGlobals.append('$').append(name).append("={id:Register.")
					.append(name)
					.append(",getS:function(){return simulation.getRegisterS(this.id);}")
					.append(",getU:function(){return simulation.getRegisterU(this.id);}")
					.append(",setS:function(val){simulation.setRegisterS(this.id, val);}")
					.append(",setU:function(val){simulation.setRegisterU(this.id, val);}")
					.append(",get:function(){return this.getS();}")
					.append(",set:function(val){this.setS(val);}")
				.append("};");
		}
		exec(registerGlobals.toString());
	}

	/**
	 * bind an object to a name accessible from JavaScript
	 * @param name the name to bind the object to
	 * @param obj the object to bind
	 */
	public void bindGlobal(String name, Object obj) {
		globals.put(name, obj);
	}

	/**
	 * retrieve a global object
	 * @param name the name the object is bound to
	 * @param tClass the class of the object eg Boolean.class
	 * @return the object, casted to the correct class
	 */
	public <T> T getGlobal(String name, Class<T> tClass) {
		return tClass.cast(globals.get(name));
	}

	/**
	 * Execute the javascript code found inside the annotation
	 * @param annotation the annotation to execute
	 * @return the result of evaulating the script (ie the script `var x = 14; x;` returns `Integer(14)`)
	 * @throws ScriptException
	 * @throws SecurityException
	 * @throws AnnotationEarlyReturn
	 * @throws AssertionError
	 */
	public Object exec(Annotation annotation) throws ScriptException, SecurityException, AnnotationEarlyReturn, AssertionError {
		@SuppressWarnings("UnusedAssignment") // this is actually necessary
		Object res = null;

		// exceptions thrown from inside the script are wrapped in a ScriptException
		// exceptions thrown from java executed from a script are not wrapped

		try {
			res = engine.eval(annotation.code);
		} catch(ScriptException e) {
			// exceptions thrown from inside the script are wrapped in a ScriptException
			if (e.getCause() instanceof ECMAException) {
				Object cause = ((ECMAException) e.getCause()).thrown;

				if (cause instanceof AnnotationEarlyReturn) {
					promoteToGlobal();
					throw (AnnotationEarlyReturn) cause;
				} else {
					throw e;
				}
			} else {
				throw e;
			}
		} catch(AssertionError e) {
			promoteToGlobal();
			throw new AssertionError(annotation.code); // exception message = code that caused it
		} catch(Exception e) { // propagate the exception
			throw new ScriptException(e);
		}
		promoteToGlobal();
		return res;
	}

	/**
	 * Execute some javascript code
	 * @param script the script to execute
	 * @throws ScriptException
	 * @throws SecurityException
	 */
	private void exec(String script) throws ScriptException, SecurityException {
		engine.eval(script);
		promoteToGlobal();
	}

	/**
	 * set all variables local to a script become global
	 */
	private void promoteToGlobal() {
		globals.putAll(nhGlobals);
		nhGlobals.clear();
	}

	/**
	 * use stdout and stdin as a javascript REPL
	 */
	public void debugREPL(IO io) {
		try {
			io.printString(IOStream.DEBUG, "REPL start (call exit() to finish)\n");

			SimulationBridge sim = (SimulationBridge) globals.get("simulation");
			if(sim == null || sim.cpu == null) {
				io.printString(IOStream.DEBUG, "Simulation not running, REPL running in its own engine\n");
			} else {
				io.printString(IOStream.DEBUG, "Simulation running, REPL has the same access as annotations\n");
			}

			bindGlobal("io", io);
			exec("print = function(s){io.printString(''+s+'\\n');};");
			exec("exit = function(){stop = true;}; stop = false;");

			String line;
			while(!getGlobal("stop", Boolean.class)) {
				io.printString(IOStream.DEBUG, "js> ");
				line = io.readString(IOStream.DEBUG);
				Object res = null;
				try {
					res = exec(new Annotation(line));
				} catch(AssertionError | AnnotationEarlyReturn e) {
					io.printString(IOStream.DEBUG, e.getClass().getName());
				}
				if(res != null) {
					io.printString(IOStream.DEBUG, res.toString() + "\n");
				}
			}
			io.printString(IOStream.DEBUG, "REPL stopped\n");
		} catch (RuntimeException | ScriptException e) {
			UIUtils.showExceptionDialog(e);
			io.printString(IOStream.DEBUG, "REPL stopped due to exception\n");
		}
	}
}
