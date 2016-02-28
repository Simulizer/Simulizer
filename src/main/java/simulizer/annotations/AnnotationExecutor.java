package simulizer.annotations;

import jdk.nashorn.api.scripting.ClassFilter;
import jdk.nashorn.api.scripting.NashornScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import simulizer.assembler.representation.Annotation;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.utils.FileUtils;

import javax.script.*;

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
		@Override public boolean exposeToScripts(String s) {
			throw new SecurityException("Access to Java objects from annotations (other than designated bridges) is disabled");
		}
	}

	public AnnotationExecutor() {
		NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
		engine = (NashornScriptEngine) factory.getScriptEngine(new AnnotationClassFilter());

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

			exec(FileUtils.getResourceContent("/annotations/load-api.js"));

		} catch (ScriptException e) {
			e.printStackTrace();
		}
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

	public void exec(Annotation annotation) throws ScriptException, SecurityException {
		engine.eval(annotation.code);
		promoteToGlobal();
	}

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
			io.printString("REPL start (call exit() to finish)\n");
			bindGlobal("io", io);
			exec("print = function(s){io.printString(''+s+'\\n');};");
			exec("exit = function(){stop = true;}; stop = false;");

			String line;
			while(!getGlobal("stop", Boolean.class)) {
				io.printString("js> ");
				line = io.readString();
				exec(new Annotation(line));
			}
			io.printString("REPL stopped\n");
		} catch (RuntimeException | ScriptException e) {
			e.printStackTrace();
			io.printString("REPL stopped due to exception\n");
		}
	}
}
