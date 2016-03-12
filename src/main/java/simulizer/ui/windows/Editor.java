package simulizer.ui.windows;

import java.io.File;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import javafx.application.Platform;
import org.reactfx.util.FxTimer;
import org.w3c.dom.Document;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.settings.Settings;
import simulizer.ui.WindowManager;
import simulizer.ui.components.TemporaryObserver;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.utils.DebugUtils;
import simulizer.utils.FileUtils;
import simulizer.utils.SafeJSObject;
import simulizer.utils.UIUtils;

/**
 * Embedded Ace editor (formerly Mozilla Bespin)
 *
 * @author mbway
 */
public class Editor extends InternalWindow {

	// TODO: refresh current file if not edited
	// TODO: update problems as the user types. maybe do this using ace's worker
	// TODO: handle more keyboard shortcuts: C-f for find

	private static boolean initialLoad = true; // whether this is the first time the editor has been opened
	private static File currentFile = null; // persists across instances of the window

	private boolean pageLoaded;
	private WebEngine engine;

	private boolean changedSinceLastSave;
	private boolean editedSinceLabelUpdate;

	// handle key combos for copy and paste
	final KeyCombination C_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
	final KeyCombination C_v = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
	final KeyCombination C_plus = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
	final KeyCombination C_minus = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);

	// references to javascript objects
	private SafeJSObject jsWindow;
	private SafeJSObject jsEditor;
	private SafeJSObject jsSession;

	public boolean hasLoaded() {
		return pageLoaded;
	}

	// execute mode = simulation running
	private enum Mode { EDIT_MODE, EXECUTE_MODE }
	private Mode mode;

	private Set<TemporaryObserver> observers = new HashSet<>();

	// to do with continuous assembling of
	private boolean continuousAssemblyEnabled;
	private int continuousAssemblyRefreshPeriod; // if changed, does not automatically change the running scheduler
	private boolean continuousAssemblyInProgress; // used to set the window title
	private final ScheduledExecutorService continuousAssembly;
	private ScheduledFuture<?> assembleTask;
	private int lastProgramHash; // used to avoid wasted effort if the file has not changed

	/**
	 * Communication between this class and the javascript running in the webview
	 */
	@SuppressWarnings("unused")
	public class Bridge {
		private Editor editor;
		public List<Problem> problems;

		public Bridge(Editor editor) {
			this.editor = editor;
		}

		public void onChange() {
			if (!editor.changedSinceLastSave) {
				editor.setEdited(true);
			}
		}
	}

	private Bridge bridge;


	public Editor() {
		WebView view = new WebView();
		pageLoaded = false;
		bridge = new Bridge(this);

		engine = view.getEngine();
		engine.setJavaScriptEnabled(true);

		// calling alert() from javascript outputs to the console
		engine.setOnAlert((event) -> System.out.println("javascript alert: " + event.getData()));

		// javascript does not have access to the outside clipboard
		view.setContextMenuEnabled(false);

		// handle copy and paste manually
		addEventFilter(KeyEvent.KEY_PRESSED, event -> {

			if (C_c.match(event)) {
				if(mode == Mode.EXECUTE_MODE)
					return;
				String clip = (String) jsEditor.call("getCopyText");

				Clipboard clipboard = Clipboard.getSystemClipboard();
				ClipboardContent content = new ClipboardContent();

				content.putString(clip);
				clipboard.setContent(content);

				event.consume();

			} else if (C_v.match(event)) {
				if(mode == Mode.EXECUTE_MODE)
					return;
				Clipboard clipboard = Clipboard.getSystemClipboard();
				String clip = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);

				if (clip != null) {
					jsEditor.call("insert", clip);
				}

				editedSinceLabelUpdate = true;
				event.consume();

			} else if(C_plus.match(event)) {
				jsWindow.call("changeFontSize", 1);
				event.consume();
			} else if(C_minus.match(event)) {
				jsWindow.call("changeFontSize", -1);
				event.consume();
			} else {
				if(mode == Mode.EXECUTE_MODE)
					return;
				editedSinceLabelUpdate = true;
			}
		});

		// other windows scan the contents of the editor continuously
		editedSinceLabelUpdate = false;
		FxTimer.runPeriodically(Duration.ofMillis(2000), () -> {
			if (editedSinceLabelUpdate) {
				editedSinceLabelUpdate = false;
				updateObservers();
			}
		});

		setOnClosedAction(e -> detachObservers());

		// continuous assembly
		continuousAssembly = Executors.newSingleThreadScheduledExecutor();
		continuousAssemblyInProgress = false;
		assembleTask = null;
		lastProgramHash = 0;

		getContentPane().getChildren().add(view);
	}

	private void startContinuousAssembly() {
		if(assembleTask != null) {
			assembleTask.cancel(false); // wait to finish
		}
		if(!continuousAssembly.isShutdown()) {
			assembleTask = continuousAssembly.scheduleAtFixedRate(() -> {

				try {
					FutureTask<String> text = new FutureTask<>(this::getText);
					Platform.runLater(text);

					String program = text.get();
					int thisProgramHash = program.hashCode();

					if (lastProgramHash != thisProgramHash) {
						continuousAssemblyInProgress = true;
						Platform.runLater(this::refreshTitle);

						final List<Problem> problems = Assembler.checkForProblems(program);
						Platform.runLater(() -> setProblems(problems));
						lastProgramHash = thisProgramHash;

						continuousAssemblyInProgress = false;
						Platform.runLater(this::refreshTitle);
					}
				} catch (Exception e) {
					UIUtils.showExceptionDialog(e);
					assembleTask.cancel(false);
					assembleTask = null;
				}
			}, 0, continuousAssemblyRefreshPeriod, TimeUnit.MILLISECONDS);
		}
	}

	/**
	 * called by loadPage once the document has loaded
	 */
	private void afterDocumentLoaded(Settings settings) {
		// setup the javascript --> java bridge
		jsWindow = new SafeJSObject((JSObject) engine.executeScript("window"));
		jsWindow.setMember("bridge", bridge);

		engine.executeScript(FileUtils.getResourceContent("/external/ace.js"));
		engine.executeScript(FileUtils.getResourceContent("/external/mode-javascript.js"));
		engine.executeScript(FileUtils.getResourceContent("/external/theme-ambiance.js"));
		engine.executeScript(FileUtils.getResourceContent("/external/theme-chaos.js"));
		engine.executeScript(FileUtils.getResourceContent("/external/theme-monokai.js"));
		engine.executeScript(FileUtils.getResourceContent("/external/theme-tomorrow_night_eighties.js"));
		initSyntaxHighlighter();

		jsWindow.call("init");
		jsEditor = new SafeJSObject((JSObject) engine.executeScript("editor")); // created by init() so must be set after
		jsSession = new SafeJSObject((JSObject) engine.executeScript("session"));

		//enableFirebug();

		// load settings
		setWrap((boolean) settings.get("editor.wrap"));
		String fontFamily = (String) settings.get("editor.font-family");
		int fontSize = (int) settings.get("editor.font-size");
		jsWindow.call("setFont", fontFamily, fontSize);

		jsEditor.call("setScrollSpeed", (double) settings.get("editor.scroll-speed"));
		engine.executeScript("session.setUseSoftTabs(" + settings.get("editor.soft-tabs") + ")");
		jsEditor.call("setTheme", (String) settings.get("editor.theme"));

		boolean vim = (boolean) settings.get("editor.vim-mode");
		if(vim) {
			engine.executeScript(FileUtils.getResourceContent("/external/keybinding-vim.js"));
			jsEditor.call("setKeyboardHandler", "ace/keyboard/vim");
		}

		boolean userInControl = (boolean) settings.get("editor.user-control-during-execution");
		jsWindow.setMember("userInControl", userInControl);

		continuousAssemblyEnabled = (boolean) settings.get("editor.continuous-assembly");
		continuousAssemblyRefreshPeriod = (int) settings.get("editor.continuous-assembly-refresh-period");

		if(initialLoad) {
			String initialFilename = (String) settings.get("editor.initial-file");
			if (initialFilename != null && !initialFilename.isEmpty()) {
				File f = new File(initialFilename);
				if (f.exists()) {
					loadFile(f);
				} else {
					UIUtils.showErrorDialog("Could Not Load", "Could not load file: \"" + initialFilename + "\"\nBecause it does not exist.");
					newFile();
				}
			} else {
				newFile();
			}
		} else {
			if(currentFile != null) {
				loadFile(currentFile);
			} else {
				newFile();
			}
		}


		if(getWindowManager().getCPU().isRunning())
			executeMode();

		// signals that all the editor methods are now safe to call
		pageLoaded = true;
		// signals that whenever the editor is opened again, it is not for the first time
		initialLoad = false;
	}

	private void loadPage(Settings settings) {
		UIUtils.assertFXThread();

		engine.loadContent(FileUtils.getResourceContent("/editor/editor.html"));

		// can only execute scripts once the page has loaded
		engine.documentProperty().addListener(new ChangeListener<Document>() {
			@Override
			public void changed(ObservableValue<? extends Document> observableValue, Document oldDoc, Document newDoc) {
				if (newDoc != null) {
					// loaded, run this once then remove as a listener
					afterDocumentLoaded(settings);

					engine.documentProperty().removeListener(this);
				}
			}
		});
	}

	@Override
	public void ready() {
		WindowManager wm = getWindowManager();

		loadPage(wm.getSettings());

		String[] observersToAdd = { Labels.class.getSimpleName() };

		for (String className : observersToAdd) {
			TemporaryObserver obs =
				(TemporaryObserver) wm.getWorkspace().findInternalWindow(WindowEnum.ofString(className));
			if (obs != null) addObserver(obs);
		}

		super.ready();
	}

	@Override
	public void close() {
		if(hasOutstandingChanges()) {
			ButtonType save = UIUtils.confirmYesNoCancel("Save changes to \"" + getBackingFilename() + "\"", "");

			if(save == ButtonType.YES) {
				saveFile();
			} else if(save == ButtonType.NO) {
				// fall through and call super.close()
			} else {
				// don't call super.close() => stays open
				return;
			}
		}

		// shutdown the continuous assembly thread
		if(assembleTask != null) assembleTask.cancel(true);
		continuousAssembly.shutdown();
		try {
			continuousAssembly.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			continuousAssembly.shutdownNow();
		}

		super.close();
	}

	private void enableFirebug() {
		// from: http://stackoverflow.com/a/9405733
		engine.executeScript(
			"if (!document.getElementById('FirebugLite')){E = document['createElement' + 'NS'] && document.documentElement.namespaceURI;E = E ? document['createElement' + 'NS'](E, 'script') : document['createElement']('script');E['setAttribute']('id', 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' + 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite', '4');(document['getElementsByTagName']('head')[0] || document['getElementsByTagName']('body')[0]).appendChild(E);E = new Image;E['setAttribute']('src', 'https://getfirebug.com/' + '#startOpened');}");
	}

	/**
	 * to avoid embedding the keywords and such into the javascript, the information is loaded
	 * from java and serialised
	 */
	private void initSyntaxHighlighter() {
		StringBuilder sb = new StringBuilder(1024); // preallocate for performance

		// generate the regular expression for keywords
		sb.append("var keywordRegex = '\\\\b(?:");
		for (Instruction i : Instruction.class.getEnumConstants()) {
			sb.append(i.name().toLowerCase());
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1); // remove the last pipe
		sb.append(")\\\\b';\n");

		// generate the regular expression for registers
		sb.append("var registerRegex = '(\\\\$)(");
		Register rs[] = Register.values();
		for (int i = 0; i < rs.length; i++) {
			sb.append(rs[i].name());
			sb.append('|');
			sb.append(i);
			sb.append('|');
		}
		sb.deleteCharAt(sb.length() - 1); // remove the last pipe
		sb.append(")\\\\b';\n");

		// generate the regular expression for directives
		sb.append("var directiveRegex = '(\\\\.)(");
		List<String> directives = Arrays.asList("data", "text", "globl", "ascii", "asciiz", "byte", "half", "word", "space", "align");
		sb.append(String.join("|", directives));
		sb.append(")\\\\b';\n");

		sb.append(FileUtils.getResourceContent("/editor/mode-simp.js"));

		engine.executeScript(sb.toString());
	}

	public void setProblems(List<Problem> problems) {
		bridge.problems = problems;
		jsWindow.call("refreshProblems");
	}

	/**
	 * Adds an observer of this class.
	 *
	 * @param obs the observer to add
	 */
	public void addObserver(TemporaryObserver obs) {
		observers.add(obs);
	}

	/**
	 * Calls the <code>update</code> method on all observers.
	 */
	private void updateObservers() {
		observers.forEach(TemporaryObserver::update);
	}

	/**
	 * Calls the <code>stopObserving</code> method on all observers and clears the list of observers.
	 */
	private void detachObservers() {
		observers.forEach(TemporaryObserver::stopObserving);
		observers.clear();
	}



	public String getText() {
		if (jsEditor != null)
			return (String) jsEditor.call("getValue");
		else
			return null;
	}

	public int getLine() {
		return (int) jsWindow.call("getLine");
	}

	public boolean hasBackingFile() {
		return currentFile != null;
	}
	public String getBackingFilename() {
		return currentFile == null ? "Untitled" : currentFile.getName();
	}

	/**
	 * @note does not perform the write if no changes have been made
	 */
	public void saveFile() {
		String currentText = getText();

		if(hasBackingFile() && changedSinceLastSave) {
			FileUtils.writeToFile(currentFile, currentText);
			setEdited(false);
			refreshTitle();
		}
	}

	public void saveAs(File file) {
		if(file == null) return;

		FileUtils.writeToFile(file, getText());

		currentFile = file;
		setEdited(false);
		refreshTitle();
	}

	public void loadFile(File file) {
		currentFile = file;
		jsWindow.call("loadText", FileUtils.getFileContent(file));
		setEdited(false);
		editedSinceLabelUpdate = false;

		updateObservers();

		if(continuousAssemblyEnabled)
			startContinuousAssembly();
	}

	public void newFile() {
		currentFile = null;
		jsWindow.call("loadText", "");
		setEdited(false);
		editedSinceLabelUpdate = false;

		updateObservers();

		if(continuousAssemblyEnabled)
			startContinuousAssembly();
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	private void setEdited(boolean edited) {
		changedSinceLastSave = edited;
		refreshTitle();
	}

	public boolean hasOutstandingChanges() {
		return changedSinceLastSave;
	}

	private void refreshTitle() {
		String modeString = mode == Mode.EXECUTE_MODE ? " (Read Only)" : "";
		String assembling = continuousAssemblyInProgress ? " ~ " : " - ";
		String editedSymbol = changedSinceLastSave ? " *" : "";
		setTitle(WindowEnum.getName(this) + modeString + assembling + getBackingFilename() + editedSymbol);
	}

	/**
	 * call when starting the simulation. Transitions into a read-only state
	 * @warning must be called from a JavaFX thread
	 */
	public void executeMode() {
		jsWindow.call("executeMode");
		mode = Mode.EXECUTE_MODE;
		refreshTitle();
	}

	/**
	 * call after the simulation has finished. Transitions into edit mode
	 * @warning must be called from a JavaFX thread
	 */
	public void editMode() {
		jsWindow.call("editMode");
		mode = Mode.EDIT_MODE;
		refreshTitle();
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findNext(String pattern) {
		jsWindow.call("find", pattern, false);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findPrevious(String pattern) {
		jsWindow.call("find", pattern, true);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findAll(String pattern) {
		jsWindow.call("findAll", pattern);
	}

	/**
	 * @param line starting from 0
	 * @warning must be called from a JavaFX thread
	 */
	public void gotoLine(int line) {
		jsWindow.call("goto", line);
	}

	/**
	 * @param size font size in px
	 */
	public void setFontSize(int size) {
		jsEditor.call("setFontSize", size);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void setWrap(boolean wrap) {
		jsSession.call("setUseWrapMode", wrap);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public boolean getWrap() {
		return (boolean) jsSession.call("getUseWrapMode");
	}

	/**
	 * lines start from 0
	 * @warning must be called from a JavaFX thread
	 */
	public void highlightPipeline(int fetchLine, int decodeLine, int executeLine) {
		jsWindow.call("highlightPipeline", fetchLine, decodeLine, executeLine);
	}
}
