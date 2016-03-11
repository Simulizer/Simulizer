package simulizer.ui.components;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.scene.control.ButtonType;
import java.net.URI;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.assembler.Assembler;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.ProgramStringBuilder;
import simulizer.simulation.cpu.components.CPU;
import simulizer.simulation.cpu.components.Clock;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.theme.Theme;
import simulizer.ui.windows.Editor;
import simulizer.utils.SpimRunner;
import simulizer.utils.UIUtils;

// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
public class MainMenuBar extends MenuBar {

	private WindowManager wm;

	public MainMenuBar(WindowManager wm) {
		this.wm = wm;
		getMenus().addAll(fileMenu(), simulationMenu(), windowsMenu(), layoutsMenu(), helpMenu(), debugMenu());
	}

	private Menu fileMenu() {
		// | File
		Menu fileMenu = new Menu("File");

		// | |-- New
		MenuItem newItem = new MenuItem("New");
		newItem.setDisable(wm.getCPU().isRunning());
		newItem.setOnAction(e -> wm.getWorkspace().openEditorWithCallback(Editor::newFile));
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

		// | |-- Open
		MenuItem loadItem = new MenuItem("Open");
		loadItem.setDisable(wm.getCPU().isRunning());
		loadItem.setOnAction(e -> {
			File f = UIUtils.openFileSelector("Open an assembly file", wm.getPrimaryStage(), new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			if (f != null) {
				wm.getWorkspace().openEditorWithCallback((ed) -> ed.loadFile(f));
			}
		});
		loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setDisable(wm.getCPU().isRunning());
		saveItem.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) -> {
			if (ed.getCurrentFile() == null) {
				UIUtils.promptSaveAs(wm.getPrimaryStage(), ed::saveAs);
			} else {
				ed.saveFile();
			}
		}));
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setDisable(wm.getCPU().isRunning());
		saveAsItem.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) ->
				UIUtils.promptSaveAs(wm.getPrimaryStage(), ed::saveAs)));

		// | |-- Exit
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> wm.shutdown());
		
		fileMenu.getItems().addAll(newItem, loadItem, saveItem, saveAsItem, exitItem);
		return fileMenu;
	}

	private Menu layoutsMenu() {
		// | |-- Layouts
		Menu layoutMenu = new Menu("Layouts");
		layoutMenu(layoutMenu);
		return layoutMenu;
	}

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
				if (!saveFile.getName().endsWith(".json")) saveFile = new File(saveFile.getAbsolutePath() + ".json");

				wm.getLayouts().saveLayout(saveFile);
				wm.getLayouts().reload(false);
				layoutMenu(menu);
			}
		});

		// | | | -- Reload Layouts
		MenuItem reloadLayoutItem = new MenuItem("Refresh Layouts");
		reloadLayoutItem.setOnAction(e -> {
			wm.getLayouts().reload(false);
			layoutMenu(menu);
		});
		menu.getItems().addAll(new SeparatorMenuItem(), saveLayoutItem, reloadLayoutItem);
	}

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

	private Menu simulationMenu() {
		Menu runMenu = new Menu("Simulation");
		runMenu.setOnShowing(e -> simControlsMenu(runMenu));
		simControlsMenu(runMenu);
		return runMenu;
	}

	private void simControlsMenu(Menu runMenu) {
		runMenu.getItems().clear();

		final CPU cpu = wm.getCPU();
		final Clock clock = cpu.getClock();

		MenuItem assembleAndRun = new MenuItem("Assemble and Run");
		assembleAndRun.setDisable(cpu.isRunning());
		assembleAndRun.setOnAction(e -> {
			UIUtils.showAssemblingDialog(cpu);
			wm.assembleAndRun();
		});

		MenuItem pause = new MenuItem("Pause Simulation");
		pause.setDisable(!clock.isRunning() || !cpu.isRunning());
		pause.setOnAction(e -> cpu.pause());

		MenuItem resume = new MenuItem("Resume Simulation");
		resume.setDisable(clock.isRunning() || !cpu.isRunning());
		resume.setOnAction(e -> wm.getCPU().resume());

		MenuItem singleStep = new MenuItem("Single Step");
		singleStep.setDisable(clock.isRunning() || !cpu.isRunning());
		singleStep.setOnAction(e -> {
			try {
				cpu.runSingleCycle();
			} catch (Exception ex) {
				// TODO: Handle Exception properly
				UIUtils.showExceptionDialog(ex);
			}
		});

		MenuItem stop = new MenuItem("End Simulation");
		stop.setDisable(!cpu.isRunning());
		stop.setOnAction(e -> wm.stopSimulation());

		CheckMenuItem togglePipeline = new CheckMenuItem("Toggle CPU Pipelining");
		togglePipeline.setDisable(cpu.isRunning());
		togglePipeline.setSelected((boolean) wm.getSettings().get("simulation.pipelined"));
		togglePipeline.setOnAction(e -> wm.newCPU(togglePipeline.isSelected()));

		MenuItem setClockSpeed = new MenuItem("Set Clock Speed");
		setClockSpeed.setOnAction(e -> {
			TextInputDialog clockSpeed = new TextInputDialog();
			clockSpeed.setTitle("Clock Speed");
			clockSpeed.setContentText("Enter Clock Speed:\n(cycles per second (Hz))");
			clockSpeed.showAndWait().ifPresent(input -> {
				double speed = -1;
				try {
					speed = Double.parseDouble(input);
				} catch(NumberFormatException ignored) { /* speed == -1 */ }

				if(speed >= 0) {
					cpu.setCycleFreq(speed);
				} else {
					UIUtils.showErrorDialog("Value out of range", "The clock speed must be a positive value\n(can be fractional)");
				}
			});
		});

		runMenu.getItems().addAll(assembleAndRun, pause, resume, singleStep, stop, togglePipeline, setClockSpeed);
	}

	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Windows");
		for (WindowEnum wenum : WindowEnum.values()) {
			if (wenum.showInWindowsMenu()) {
				MenuItem item = new MenuItem(wenum.toString());
				item.setOnAction(e -> wm.getWorkspace().openInternalWindow(wenum));
				windowsMenu.getItems().add(item);
			}
		}

		MenuItem delWindows = new MenuItem("Close All");
		delWindows.setOnAction(e -> wm.getWorkspace().closeAll());
		windowsMenu.getItems().addAll(new SeparatorMenuItem(), delWindows);

		return windowsMenu;
	}

	private Menu helpMenu() {
		Menu helpMenu = new Menu("Help");

		MenuItem guide = new MenuItem("Guide");
		guide.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.GUIDE));

		MenuItem syscall = new MenuItem("Syscall Reference");
		syscall.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.SYSCALL_REFERENCE));

		MenuItem instruction = new MenuItem("Instruction Reference");
		instruction.setOnAction(e -> wm.getWorkspace().openInternalWindow(WindowEnum.INSTRUCTION_REFERENCE));

		MenuItem keyBinds = new MenuItem("Editor Shortcuts");
		keyBinds.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI("https://github.com/ajaxorg/ace/wiki/Default-Keyboard-Shortcuts"));
			} catch (Exception ex) {
				UIUtils.showExceptionDialog(ex);
			}
		});

		helpMenu.getItems().addAll(guide, syscall, instruction, new SeparatorMenuItem(), keyBinds);

		return helpMenu;
	}

	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");

		MenuItem dumpProgram = new MenuItem("Dump Assembled Program");
		dumpProgram.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) -> {
			Program p = Assembler.assemble(ed.getText(), null);
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
		}));

		MenuItem runSpim = new MenuItem("Run in SPIM");
		runSpim.setOnAction(e -> wm.getWorkspace().openEditorWithCallback((ed) -> {
			String program = ed.getText();
			SpimRunner.runQtSpim(program);
		}));

		MenuItem jsREPL = new MenuItem("Start javascript REPL");
		jsREPL.setOnAction(e -> {
			Thread replThread = new Thread(() -> {
				// if there is an executor (eg simulation running) then use that
				if (wm.getAnnotationManager().getExecutor() == null) {
					// this does not bridge with the visualisations or simulation
					wm.getAnnotationManager().newExecutor();
				}
				wm.getAnnotationManager().getExecutor().debugREPL(wm.getIO());
			}, "JS-REPL-Thread");
			replThread.setDaemon(true);
			replThread.start();
		});

		Menu themes = new Menu("Themes");
		themeMenu(themes);

		debugMenu.getItems().addAll(dumpProgram, runSpim, jsREPL, themes);
		return debugMenu;
	}

}
