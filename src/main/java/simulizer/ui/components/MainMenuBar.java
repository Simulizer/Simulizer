package simulizer.ui.components;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import simulizer.assembler.Assembler;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.ProgramStringBuilder;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.Clock;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.theme.Theme;
import simulizer.ui.windows.Editor;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;
import simulizer.utils.runner.SpimRunner;

// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
/**
 * This class holds all the Menu Items that are stored in the Bar at the top of the window.
 *
 * @author Michael
 */
public class MainMenuBar extends MenuBar {

	private final WindowManager wm;
	private final MenuBarControls controls;


	/**
	 * Creates a new MainMenuBar
	 *
	 * @param wm
	 *            The WindowManager instance to attach to
	 */
	public MainMenuBar(WindowManager wm) {
		this.wm = wm;

		// Standard
		getMenus().addAll(fileMenu(), editMenu(), simulationMenu(), windowsMenu(), layoutsMenu(), helpMenu());

		// Debug
		if ((boolean) wm.getSettings().get("debug"))
			getMenus().add(debugMenu());

		// Extra controls
		controls = new MenuBarControls(this, wm);
	}

	/**
	 * @return the file menu
	 */
	private Menu fileMenu() {
		Menu fileMenu = new Menu("File");
		fileMenu.setOnShowing(e -> fileMenuHelper(fileMenu, true));
		fileMenu.setOnHidden(e -> fileMenuHelper(fileMenu, false));
		fileMenuHelper(fileMenu, false);
		return fileMenu;
	}

	private void fileMenuHelper(Menu fileMenu, boolean allowDisabling) {
		// | File
		fileMenu.getItems().clear();

		// | |-- New
		MenuItem newItem = new MenuItem("New");
		newItem.setDisable(allowDisabling && wm.getCPU().isRunning());
		newItem.setOnAction(e -> {
			if (!wm.getCPU().isRunning())
				wm.getWorkspace().openEditorWithCallback(Editor::newFile);
		});
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

		// | |-- Open
		MenuItem loadItem = new MenuItem("Open");
		loadItem.setDisable(allowDisabling && wm.getCPU().isRunning());
		loadItem.setOnAction(e -> {
			if (!wm.getCPU().isRunning()) {
				File f = UIUtils.openFileSelector("Open an assembly file", wm.getPrimaryStage(), new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
				if (f != null)
					wm.getWorkspace().openEditorWithCallback((ed) -> ed.loadFile(f));
			}
		});
		loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setDisable(allowDisabling && wm.getCPU().isRunning());
		saveItem.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) -> {
			if (!wm.getCPU().isRunning()) {
				if (Editor.hasBackingFile())
					ed.saveFile();
				else
					UIUtils.promptSaveAs(wm.getPrimaryStage(), ed::saveAs);
			}
		}));
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setDisable(allowDisabling && wm.getCPU().isRunning());
		saveAsItem.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) -> UIUtils.promptSaveAs(wm.getPrimaryStage(), ed::saveAs)));

		// | |-- Re-load
		MenuItem reloadItem = new MenuItem("Reload");
		reloadItem.setDisable(allowDisabling && wm.getCPU().isRunning());
		reloadItem.setOnAction(e -> {
			if(!wm.getCPU().isRunning()) {
                wm.getWorkspace().openEditorWithCallback(Editor::reloadFile);
			}
		});

		// | |-- Options
		MenuItem optionsItem = new MenuItem("Options");
		optionsItem.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.OPTIONS));

		// | |-- Exit
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> wm.shutdown());

		fileMenu.getItems().addAll(newItem, loadItem, saveItem, saveAsItem, reloadItem, optionsItem, exitItem);
	}


	private Menu editMenu() {
		Menu editMenu = new Menu("Edit");
		editMenu.setOnShowing((e) -> editMenuHelper(editMenu, true));
		editMenuHelper(editMenu, false);
		return editMenu;
	}


	private void passToEditor(KeyCode kc) {
		Editor e = (Editor) wm.getWorkspace().findInternalWindow(WindowEnum.EDITOR);
		if(e != null) {
			e.handleKeyEvent(new KeyEvent(
				KeyEvent.KEY_PRESSED, "", "", kc, false, true /*ctrl*/, false, false));
		}
	}
	/**
	 * Generates the edit menu
	 */
	private void editMenuHelper(Menu editMenu, boolean allowDisabling) {
		// NOTE: these are handled inside the editor code instead (see Editor::Editor())
		editMenu.getItems().clear();

		Editor e = (Editor) wm.getWorkspace().findInternalWindow(WindowEnum.EDITOR);

		MenuItem cut = new MenuItem("Cut");
		cut.setDisable(allowDisabling && (e == null || e.getMode() == Editor.Mode.EXECUTE_MODE));
		cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.CONTROL_DOWN));
		cut.setOnAction((a) -> passToEditor(KeyCode.X));

		MenuItem copy = new MenuItem("Copy");
		copy.setDisable(allowDisabling && (e == null || e.getMode() == Editor.Mode.EXECUTE_MODE));
		copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN));
		copy.setOnAction((a) -> passToEditor(KeyCode.C));


		MenuItem paste = new MenuItem("Paste");
		paste.setDisable(allowDisabling && (e == null || e.getMode() == Editor.Mode.EXECUTE_MODE));
		paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.CONTROL_DOWN));
		paste.setOnAction((a) -> passToEditor(KeyCode.V));

		MenuItem find = new MenuItem("Find");
		find.setAccelerator(new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN));
		find.setOnAction((a) -> passToEditor(KeyCode.F));

		MenuItem gotoL = new MenuItem("Go To Line");
		gotoL.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyCombination.CONTROL_DOWN));
		gotoL.setOnAction((a) -> passToEditor(KeyCode.G));

		MenuItem insertBreakpoint = new MenuItem("Insert Breakpoint");
		insertBreakpoint.setDisable(allowDisabling && (e == null || e.getMode() == Editor.Mode.EXECUTE_MODE));
		insertBreakpoint.setAccelerator(new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN));
		insertBreakpoint.setOnAction((a) -> passToEditor(KeyCode.B));

		MenuItem fontInc = new MenuItem("Increase Font Size");
		fontInc.setAccelerator(new KeyCodeCombination(KeyCode.PLUS, KeyCombination.CONTROL_DOWN));
		fontInc.setOnAction((a) -> passToEditor(KeyCode.PLUS));

		MenuItem fontDec = new MenuItem("Decrease Font Size");
		fontDec.setAccelerator(new KeyCodeCombination(KeyCode.MINUS, KeyCombination.CONTROL_DOWN));
		fontDec.setOnAction((a) -> passToEditor(KeyCode.MINUS));

		CheckMenuItem wordWrap = new CheckMenuItem("Toggle Word Wrap");
		wordWrap.setSelected(e!= null && e.getWrap());
		wordWrap.setOnAction((a) -> {
			if(e != null) e.setWrap(!e.getWrap());
		});


		editMenu.getItems().addAll(cut, copy, paste, find, gotoL, insertBreakpoint, fontInc, fontDec, wordWrap);
	}

	/**
	 * Generates the layouts menu
	 *
	 * @return the layouts menu
	 */
	private Menu layoutsMenu() {
		// | |-- Layouts
		Menu layoutMenu = new Menu("Layouts");
		layoutMenu(layoutMenu);
		return layoutMenu;
	}

	/**
	 * A separate helper function to generate the layouts menu. Used to dynamically refresh the loaded layouts
	 *
	 * @param menu
	 *            The layout menu
	 */
	private void layoutMenu(Menu menu) {
		menu.getItems().clear();

		for (Layout l : wm.getLayouts()) {
			String name = l.getName();
			MenuItem item = new MenuItem(name.endsWith(".json") ? name.substring(0, name.length() - 5) : name);
			item.setOnAction(e -> wm.getLayouts().setLayout(l));
			menu.getItems().add(item);
		}

		// | | | -- Save Layout
		MenuItem saveLayoutItem = new MenuItem("Save Current Layout");
		saveLayoutItem.setOnAction(e -> {
			File saveFile = UIUtils.saveFileSelector("Save layout", wm.getPrimaryStage(), new File("layouts"), new ExtensionFilter("JSON Files *.json", "*.json"));
			if (saveFile != null) {
				if (!saveFile.getName().endsWith(".json"))
					saveFile = new File(saveFile.getAbsolutePath() + ".json");

				wm.getLayouts().saveLayout(saveFile);
				wm.getLayouts().reload(false);
				layoutMenu(menu);
			}
		});

		// | | | -- Refresh Layouts
		MenuItem reloadLayoutItem = new MenuItem("Refresh Layouts");
		reloadLayoutItem.setOnAction(e -> {
			wm.getLayouts().reload(false);
			layoutMenu(menu);
		});

		// | | | -- Toggle Fullscreen
		CheckMenuItem fullscreen = new CheckMenuItem("Fullscreen");
		wm.getPrimaryStage().setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		fullscreen.setAccelerator(new KeyCodeCombination(KeyCode.F11));
		fullscreen.setSelected(wm.getPrimaryStage().isFullScreen());
		fullscreen.setOnAction(e -> {
			Stage wnd = wm.getPrimaryStage();
			wnd.setFullScreen(!wnd.isFullScreen());
		});

		menu.getItems().addAll(new SeparatorMenuItem(), saveLayoutItem, reloadLayoutItem, fullscreen);
	}

	/**
	 * Dynamically generates the Theme menu.
	 *
	 * @param menu
	 *            The menu item to attach to
	 * @return The completed menu
	 */
	private Menu themeMenu(Menu menu) {
		menu.getItems().clear();

		for (Theme t : wm.getThemes()) {
			MenuItem item = new MenuItem(t.getName());
			item.setOnAction(e -> wm.getThemes().setTheme(t));
			menu.getItems().add(item);
		}

		MenuItem reloadThemeItem = new MenuItem("Refresh Themes");
		reloadThemeItem.setOnAction(e -> {
			wm.getThemes().reload();
			themeMenu(menu);
			wm.getThemes().setTheme(wm.getThemes().getTheme());
		});

		menu.getItems().addAll(new SeparatorMenuItem(), reloadThemeItem);
		return menu;
	}

	/**
	 * The simulation menu
	 *
	 * @return the simulation menu
	 */
	private Menu simulationMenu() {
		Menu runMenu = new Menu("Simulation");
		runMenu.setOnShowing(e -> simControlsMenu(runMenu, true));
		runMenu.setOnHidden(e -> simControlsMenu(runMenu, false));
		simControlsMenu(runMenu, false);
		return runMenu;
	}

	/**
	 * Dynamically generates the simulation menu items
	 *
	 * @param runMenu
	 *            The simulation menu to add the items to
	 * @param allowDisabling
	 *            Force all the options to be enabled
	 */
	private void simControlsMenu(Menu runMenu, boolean allowDisabling) {
		runMenu.getItems().clear();

		final CPU cpu = wm.getCPU();
		final Clock clock = cpu.getClock();

		MenuItem assembleAndRun = new MenuItem("Assemble and Run");
		assembleAndRun.setAccelerator(new KeyCodeCombination(KeyCode.F5));
		assembleAndRun.setDisable(allowDisabling && cpu.isRunning());
		assembleAndRun.setOnAction(e -> {
			if (!cpu.isRunning()) {
				AssemblingDialog.showAssemblingDialog(wm);
				wm.assembleAndRun();
			}
		});

		MenuItem pauseResume;
		if (!clock.isRunning()) {
			pauseResume = new MenuItem("Resume Simulation");
			pauseResume.setDisable(allowDisabling && (clock.isRunning() || !cpu.isRunning()));
		} else {
			pauseResume = new MenuItem("Pause Simulation");
			pauseResume.setDisable(allowDisabling && (!clock.isRunning() || !cpu.isRunning()));
		}
		pauseResume.setAccelerator(new KeyCodeCombination(KeyCode.F6));
		pauseResume.setOnAction(e -> {
			if (!clock.isRunning() && cpu.isRunning())
				cpu.resume();
			else if (clock.isRunning() && cpu.isRunning())
				cpu.pause();
		});

		MenuItem singleStep = new MenuItem("Single Step");
		singleStep.setAccelerator(new KeyCodeCombination(KeyCode.F7));
		singleStep.setDisable(allowDisabling && (clock.isRunning() || !cpu.isRunning()));
		singleStep.setOnAction(e -> {
			if (cpu.isPaused()) {
				cpu.resumeForOneCycle();
			}
		});

		MenuItem stop = new MenuItem("End Simulation");
		stop.setAccelerator(new KeyCodeCombination(KeyCode.F8));
		stop.setDisable(allowDisabling && !cpu.isRunning());
		stop.setOnAction(e -> {
			if (cpu.isRunning())
				wm.stopSimulation();
		});

		CheckMenuItem togglePipeline = new CheckMenuItem("Toggle CPU Pipelining");
		togglePipeline.setDisable(cpu.isRunning());
		togglePipeline.setSelected(cpu.isPipelined());
		togglePipeline.setOnAction(e -> wm.newCPU(togglePipeline.isSelected()));

		MenuItem setClockSpeed = new MenuItem("Set Clock Speed");
		setClockSpeed.setOnAction(e -> {
			double currentRounded = Double.parseDouble(String.format("%.5f", cpu.getCycleFreq()));
			UIUtils.openDoubleInputDialog("Clock Speed", "Set Clock Speed:", "Cycles per second (Hz)", currentRounded, (val) -> {
				if (val >= 0) {
					cpu.setCycleFreq(val);
				} else {
					UIUtils.showErrorDialog("Value out of range", "The clock speed must be a positive value\n(can be fractional)");
				}
			});
		});

		runMenu.getItems().addAll(assembleAndRun, pauseResume, singleStep, stop, togglePipeline, setClockSpeed);
	}

	/**
	 * The window menu
	 *
	 * @return the window menu
	 */
	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Windows");
		windowsMenu.setOnShowing(e -> windowsMenuHelper(windowsMenu));
		windowsMenuHelper(windowsMenu);
		return windowsMenu;
	}

	/**
	 * Dynamically generates the windows menu items
	 *
	 * @param windowsMenu
	 *            The windows menu to add the items to
	 */
	private void windowsMenuHelper(Menu windowsMenu) {
		windowsMenu.getItems().clear();
		for (WindowEnum wenum : WindowEnum.values()) {
			if (wenum.showInWindowsMenu()) {
				CheckMenuItem item = new CheckMenuItem(wenum.toString());
				item.setSelected(wm.getWorkspace().windowIsOpen(wenum));
				item.setOnAction(e -> {
					InternalWindow window = wm.getWorkspace().findInternalWindow(wenum);
					if (window == null)
						wm.getWorkspace().openInternalWindow(wenum);
					else
						window.close();
				});
				windowsMenu.getItems().add(item);
			}
		}

		MenuItem delWindows = new MenuItem("Close All");
		delWindows.setOnAction(e -> wm.getWorkspace().closeAll());
		windowsMenu.getItems().addAll(new SeparatorMenuItem(), delWindows);
	}

	/**
	 * The help menu
	 *
	 * @return the help menu
	 */
	private Menu helpMenu() {
		Menu helpMenu = new Menu("Help");

		MenuItem guide = new MenuItem("Guide");
		guide.setOnAction(e -> FileUtils.openFile("guide.pdf"));

		MenuItem syscall = new MenuItem("Syscall Reference");
		syscall.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.SYSCALL_REFERENCE));

		MenuItem instruction = new MenuItem("Instruction Reference");
		instruction.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.INSTRUCTION_REFERENCE));

		MenuItem register = new MenuItem("Register Reference");
		register.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.REGISTER_REFERENCE));

		MenuItem keyBinds = new MenuItem("Editor Shortcuts");
		keyBinds.setOnAction(e -> {
			String url = "https://github.com/ajaxorg/ace/wiki/Default-Keyboard-Shortcuts";
			if (!UIUtils.openURL(url)) {
				UIUtils.showErrorDialog("Could not open", "Could not open the url: " + url);
			}
		});

		helpMenu.getItems().addAll(guide, syscall, instruction, register, new SeparatorMenuItem(), keyBinds);

		return helpMenu;
	}

	/**
	 * The debug menu
	 *
	 * @return the debug menu
	 */
	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");

		MenuItem dumpProgram = new MenuItem("Dump Assembled Program");
		dumpProgram.setOnAction(e -> {
			Program p = Assembler.assemble(Editor.getText(), null, false);
			String outputFilename = "program-dump.txt";
			if (p == null) {
				try (PrintWriter out = new PrintWriter(outputFilename)) {
					out.println("null");
				} catch (IOException e1) {
					UIUtils.showExceptionDialog(e1);
				}
			} else {
				ProgramStringBuilder.dumpToFile(p, outputFilename);
			}
			System.out.println("Program dumped to: \"" + outputFilename + "\"");
		});

		MenuItem runSpim = new MenuItem("Run in SPIM");
		runSpim.setOnAction(e -> {
			String program = Editor.getText();
			SpimRunner.runQtSpim(program);
		});

		MenuItem jsREPL = new MenuItem("Start javascript REPL");
		jsREPL.setOnAction(e -> {
			Thread replThread = new Thread(() -> {
				// if there is an executor (eg simulation running) then use that
				if (wm.getAnnotationManager().getExecutor() == null) {
					// this does not bridge with the visualisations or simulation
					wm.getAnnotationManager().newExecutor();
				}
				wm.getAnnotationManager().getExecutor().debugREPL(wm.getIO());
			} , "JS-REPL-Thread");
			replThread.setDaemon(true);
			replThread.start();
		});

		Menu themes = new Menu("Themes");
		themeMenu(themes);

		debugMenu.getItems().addAll(dumpProgram, runSpim, jsREPL, themes);
		return debugMenu;
	}

	public MenuBarControls getControls() {
		return controls;
	}
}
