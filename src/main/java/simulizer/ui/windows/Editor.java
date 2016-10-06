package simulizer.ui.windows;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.concurrent.Task;
import org.w3c.dom.Document;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.CacheHint;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import simulizer.assembler.extractor.problem.Problem;
import simulizer.assembler.representation.Instruction;
import simulizer.assembler.representation.Register;
import simulizer.settings.Settings;
import simulizer.simulation.cpu.components.Breakpoints;
import simulizer.ui.WindowManager;
import simulizer.ui.components.CurrentFile;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.theme.Theme;
import simulizer.utils.FileUtils;
import simulizer.utils.SafeJSObject;
import simulizer.utils.UIUtils;

/**
 * Embedded Ace editor (formerly Mozilla Bespin)
 *
 * @author mbway
 */
@SuppressWarnings("WeakerAccess")
public class Editor extends InternalWindow {

	private static volatile Editor editor; // only one instance

	private volatile boolean pageLoaded;
	private final WebEngine engine;

	private volatile boolean contentIsModified; // changes have been made in the editor since loading

	// handle key combos for copy and paste
	final static private KeyCombination C_c = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_x = new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_v = new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_b = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_g = new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_f = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);

	// for Ctrl plus with each different plus key on the keyboard
	final static private KeyCombination C_add = new KeyCodeCombination(KeyCode.ADD, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_plus = new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN); // Caps lock + =
	final static private KeyCombination C_eq = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_S_eq = new KeyCodeCombination(KeyCode.EQUALS, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN);
	// for Ctrl minus with each different minus key on the keyboard
	final static private KeyCombination C_minus = new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN);
	final static private KeyCombination C_subtract = new KeyCodeCombination(KeyCode.SUBTRACT, KeyCombination.CONTROL_DOWN);

	// references to javascript objects
	private SafeJSObject jsWindow;
	private SafeJSObject jsEditor;
	private SafeJSObject jsSession;

	public boolean hasLoaded() {
		return pageLoaded;
	}

	// execute mode = simulation running
	public enum Mode { EDIT_MODE, EXECUTE_MODE }
	private Mode mode;


	/**
	 * Communication between this class and the javascript running in the webview
	 */
	@SuppressWarnings({"WeakerAccess", "unused"})
	public static class Bridge {
		private Editor editor;
		private boolean hasBreakpointsSinceLastEdit;
		public List<Problem> problems;

		public Bridge(Editor editor) {
			this.editor = editor;
			this.hasBreakpointsSinceLastEdit = false;
			this.problems = new LinkedList<>();
		}

		public void onChange() {
			if(!editor.contentIsModified) {
				editor.setEdited(true);
			}
			if(hasBreakpointsSinceLastEdit) {
				editor.clearBreakpoints();
				hasBreakpointsSinceLastEdit = false;
			}
		}
		public void onBreakpoint(int line, boolean set) {
            if(set) {
				Breakpoints.addBreakpointLine(line);
                hasBreakpointsSinceLastEdit = true;
			} else {
				Breakpoints.removeBreakpointLine(line);
			}
		}
	}

	private Bridge bridge;

	/**
	 * @return the editor instance (or null if not open)
	 */
	public static Editor getEditor() {
		return editor;
	}

	public Editor() {
		WebView view = new WebView();
		view.setFontSmoothingType(FontSmoothingType.GRAY); // looks better than colored (LCD) blurring IMO

		// caching with SPEED hint is awful on Linux and or low power machines (see issue #24)
		// caching with QUALITY hint is indistinguishable from no caching as far as I can tell
		// to to remove potential problems it's probably not worth enabling caching at all
		view.setCache(false);

		pageLoaded = false;
		bridge = new Bridge(this);

		engine = view.getEngine();
		engine.setJavaScriptEnabled(true);

		// making it so calling alert() from javascript outputs to the console
		engine.setOnAlert((event) -> System.out.println("javascript alert: " + event.getData()));

		mode = Mode.EDIT_MODE;

		// javascript does not have access to the outside clipboard
		view.setContextMenuEnabled(false);

		// handle copy and paste and other key-combinations manually
		getEventManager().addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyEvent);

		getContentPane().getChildren().add(view);
	}

	@Override
	public void setToDefaultDimensions() {
		setNormalisedDimentions(0.0, 0.0, 0.5, 1.0);
	}

	@Override
	public void setTheme(Theme theme) {
		getStyleClass().add("editor");
		super.setTheme(theme);
		getStylesheets().add(theme.getStyleSheet("editor.css"));
	}

	public void handleKeyEvent(KeyEvent event) {
		// TODO: refactor to check for fewer key combos during execute mode (if(execute mode) on the outside
		if (C_c.match(event)) {
			if (mode == Mode.EXECUTE_MODE)
				return;

			copy();
			event.consume();

		} else if (C_x.match(event)) {
			if (mode == Mode.EXECUTE_MODE)
				return;

			cut();
			event.consume();
		} else if (C_v.match(event)) {
			if(mode == Mode.EXECUTE_MODE)
				return;

			paste();
			event.consume();

		} else if (C_b.match(event)) {
			if(mode == Mode.EXECUTE_MODE)
				return;

			insertBreakpoint();
			event.consume();
		} else if (C_g.match(event)) {
			UIUtils.openIntInputDialog("Go To Line", "Go To Line",
					"Enter a line number:", 1, (l) -> gotoLine(l-1));
			event.consume();
		} else if (C_f.match(event)) {
			UIUtils.openTextInputDialog("Find", "Find",
					"Please enter your search query:", "", this::findNext);
			event.consume();
		} else if(C_plus.match(event) || C_add.match(event) || C_eq.match(event) || C_S_eq.match(event)) {
			changeFontSize(1);
			event.consume();
		} else if(C_minus.match(event) || C_subtract.match(event)) {
			changeFontSize(-1);
			event.consume();
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

		// only load the javascript for the theme that the user selected
		// this is fine since the settings can't change at runtime (the app must be closed)
		String userTheme = (String) settings.get("editor.theme");
		userTheme = userTheme.substring(userTheme.lastIndexOf("/")+1); // eg "/ace/theme/THEME" --> "THEME"
		List<String> availableThemes = Arrays.asList(
			"/editor/theme-high-viz.js",
			"/external/theme-ambiance.js",
			"/external/theme-chaos.js",
			"/external/theme-monokai.js",
			"/external/theme-tomorrow_night_eighties.js",
            "/external/theme-predawn.js",
            "/external/theme-flatland.js"
		);
		for(String theme : availableThemes) {
			// eg "/external/theme-monokai.js" contains the substring "monokai"
			if(theme.contains(userTheme)) {
				engine.executeScript(FileUtils.getResourceContent(theme));
			}
		}

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

		jsEditor.call("setScrollSpeed", (Double) settings.get("editor.scroll-speed"));
		engine.executeScript("session.setUseSoftTabs(" + settings.get("editor.soft-tabs") + ")");
		jsEditor.call("setTheme", (String) settings.get("editor.theme"));

		boolean vim = (boolean) settings.get("editor.vim-mode");
		if(vim) {
			engine.executeScript(FileUtils.getResourceContent("/external/keybinding-vim.js"));
			jsEditor.call("setKeyboardHandler", "ace/keyboard/vim");
		}

		boolean userInControl = (boolean) settings.get("editor.user-control-during-execution");
		jsWindow.setMember("userInControl", userInControl);

		if(getWindowManager().getCPU().isRunning())
			executeMode();

		loadCurrentFile();

        //enableFirebug();

		// signals that all the editor methods are now safe to call
		pageLoaded = true;
        editor = this; // only set once ready to be used
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

		super.ready();
	}

	@Override
	public void close() {
		boolean cancelled = CurrentFile.promptToSaveIfNecessary();

		if(!cancelled) {
			editor = null;

			super.close();
		}
	}

	@Override
	public boolean canClose() {
		return !CurrentFile.promptToSaveIfNecessary();
	}

	@SuppressWarnings("unused")
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
	 * _Discard_ changes made in the editor and load from disk
	 */
	public void loadCurrentFile() {
		contentIsModified = false; // to ensure getCurrentText doesn't take the editor's content
		jsWindow.call("loadText", CurrentFile.getCurrentText());
        refreshTitle();
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public String getEditorText() {
		if (editor != null && editor.jsEditor != null)
			return (String) editor.jsEditor.call("getValue");
		else
			return "";
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public int getLine() {
		return (int) jsWindow.call("getLine");
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void setEdited(boolean edited) {
		contentIsModified = edited;
		refreshTitle();
	}

	public boolean hasOutstandingChanges() {
		return contentIsModified;
	}

	public void refreshTitle() {
		// a hack to make absolutely sure that the editor doesn't stay read only
		// even if the simulation crashes.
		// note: refreshTitle is called by continuous checking every few seconds (if enabled)
		if(mode == Mode.EXECUTE_MODE && !getWindowManager().getCPU().isRunning())
			editMode();

		String modeString = mode == Mode.EXECUTE_MODE ? " (Read Only)" : "";
		String assembling = CurrentFile.checkIsInProgress() ? " ~ " : " - ";
		String editedSymbol = contentIsModified ? " *" : "";
		setWindowTitle(WindowEnum.getName(this) + modeString + assembling + CurrentFile.getBackingFilename() + editedSymbol);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	private void clearBreakpoints() {
		jsSession.call("clearBreakpoints");
		Breakpoints.clearBreakpoints();
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

	public Mode getMode() {
		return mode;
	}


	public void copy() {
		if(mode == Mode.EDIT_MODE) {
			String clip = (String) jsEditor.call("getCopyText");

			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(clip);
			clipboard.setContent(content);
		}
	}
	public void cut() {
		if(mode == Mode.EDIT_MODE) {
			String clip = (String) jsWindow.call("cut");

			Clipboard clipboard = Clipboard.getSystemClipboard();
			ClipboardContent content = new ClipboardContent();

			content.putString(clip);
			clipboard.setContent(content);
		}
	}
	public void paste() {
		if(mode == Mode.EDIT_MODE) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			String clip = (String) clipboard.getContent(DataFormat.PLAIN_TEXT);

			if (clip != null) {
				jsEditor.call("insert", clip);
			}
            setEdited(true);
		}
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findNext(String pattern) {
		jsWindow.call("find", pattern, false, false);
	}

	public void findNextRegex(String regex) {
		jsWindow.call("find", regex, false, true);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findPrevious(String pattern) {
		jsWindow.call("find", pattern, true, false);
	}

	public void findPreviousRegex(String regex) {
		jsWindow.call("find", regex, true, true);
	}

	/**
	 * @warning must be called from a JavaFX thread
	 */
	public void findAll(String pattern) {
		jsWindow.call("findAll", pattern, false);
	}

	public void findAllRegex(String regex) {
		jsWindow.call("findAll", regex, true);
	}

	/**
	 * @param line starting from 0
	 * @warning must be called from a JavaFX thread
	 */
	public void gotoLine(int line) {
		jsWindow.call("goto", line);
	}

	public void insertBreakpoint() {
		if(mode == Mode.EDIT_MODE) {
			jsWindow.call("insertBreakpoint");
            setEdited(true);
		}
	}

	/**
	 * @param size font size in px
	 */
	public void setFontSize(int size) {
		jsEditor.call("setFontSize", size);
	}

	/**
	 * @param relativeChange the number of px to add or subtract
	 */
	public void changeFontSize(int relativeChange) {
		jsWindow.call("changeFontSize", relativeChange);
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
