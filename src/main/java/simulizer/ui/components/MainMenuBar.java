package simulizer.ui.components;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.assembler.Assembler;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.ProgramStringBuilder;
import simulizer.simulation.cpu.components.CPU;
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
		getMenus().addAll(fileMenu(), simulationMenu(), windowsMenu(), layoutsMenu(), debugMenu());
	}

	private Editor getEditor() {
		return (Editor) wm.getWorkspace().openInternalWindow(WindowEnum.EDITOR);
	}

	private Menu fileMenu() {
		// | File
		Menu fileMenu = new Menu("File");

		// | |-- New
		MenuItem newItem = new MenuItem("New");
		newItem.setOnAction(e -> getEditor().newFile());
		newItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));

		// | |-- Open
		MenuItem loadItem = new MenuItem("Open");
		loadItem.setOnAction(e -> {
			File f = UIUtils.openFileSelector("Open an assembly file", wm.getPrimaryStage(), new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			if(f != null) {
				getEditor().loadFile(f);
			}
		});
		loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			if(getEditor().getCurrentFile() == null) {
				Editor editor = getEditor();
				UIUtils.promptSaveAs(wm.getPrimaryStage(), editor::saveAs);
			} else {
				getEditor().saveFile();
			}
		});
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setOnAction(e -> {
			Editor editor = getEditor();
			UIUtils.promptSaveAs(wm.getPrimaryStage(), editor::saveAs);
		});

		// | |-- Exit
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> {
			Editor editor = getEditor();
			if(editor.hasOutstandingChanges()) {
				ButtonType save = UIUtils.confirmYesNoCancel("Save changes to \"" + editor.getCurrentFile().getName() + "\"", "");

				if(save == ButtonType.YES) {
					editor.saveFile();
					System.exit(0);
				} else if(save == ButtonType.NO) {
					System.exit(0);
				} else {
					// do nothing (cancel)
				}
			}
		});

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
			File saveFile = UIUtils.saveFileSelector("Save layout", wm.getPrimaryStage(), new File("layouts"),
					new ExtensionFilter("JSON Files *.json", "*.json"));
			if (saveFile != null) {
				if (!saveFile.getName().endsWith(".json"))
					saveFile = new File(saveFile.getAbsolutePath() + ".json");

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
		// To be moved to separate buttons later
		Menu runMenu = new Menu("Simulation");

		MenuItem runPause = new MenuItem("Run/Pause");
		runPause.setOnAction(e -> wm.assembleAndRun());

		MenuItem singleStep = new MenuItem("Next Step");
		singleStep.setDisable(true);

		MenuItem stop = new MenuItem("Stop");
		stop.setOnAction(e -> wm.stopSimulation());

		CheckMenuItem simplePipeline = new CheckMenuItem("Pipelined CPU");
		simplePipeline.setSelected((boolean)wm.getSettings().get("simulation.pipelined"));
		simplePipeline.setOnAction(e -> wm.setPipelined(simplePipeline.isSelected()));

		MenuItem setClockSpeed = new MenuItem("Set Clock Speed");
		setClockSpeed.setOnAction(e -> {
			CPU cpu = wm.getCPU();
			if (cpu != null) {
				TextInputDialog clockSpeed = new TextInputDialog();
				clockSpeed.setTitle("Clock Speed");
				clockSpeed.setContentText("Enter Clock Speed: ");
				clockSpeed.showAndWait().ifPresent(speed -> cpu.setClockSpeed(Integer.parseInt(speed)));
			}
		});

		runMenu.getItems().addAll(runPause, singleStep, stop, simplePipeline, setClockSpeed);
		return runMenu;
	}

	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Windows");
		for (WindowEnum wenum : WindowEnum.values()) {
			MenuItem item = new MenuItem(wenum.toString());
			item.setOnAction(e -> wm.getWorkspace().openInternalWindow(wenum));
			windowsMenu.getItems().add(item);
		}

		MenuItem delWindows = new MenuItem("Close All");
		delWindows.setOnAction(e -> wm.getWorkspace().closeAll());
		windowsMenu.getItems().addAll(new SeparatorMenuItem(), delWindows);

		return windowsMenu;
	}

	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");


		MenuItem dumpProgram = new MenuItem("Dump Assembled Program");
		dumpProgram.setOnAction(e -> {
			Program p = Assembler.assemble(getEditor().getText(), null);
			String outputFilename = "program-dump.txt";
			if (p == null) {
				try (PrintWriter out = new PrintWriter(outputFilename)) {
					out.println("null");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else {
				ProgramStringBuilder.dumpToFile(p, outputFilename);
			}
			System.out.println("Program dumped to: \"" + outputFilename + "\"");
		});

		MenuItem runSpim = new MenuItem("Run in SPIM");
		runSpim.setOnAction(e -> {
			String program = getEditor().getText();
			SpimRunner.runQtSpim(program);
		});

		MenuItem jsREPL = new MenuItem("Start javascript REPL");
		jsREPL.setOnAction(e -> {
			new Thread(() -> {
				// if there is an executor (eg simulation running) then use that
				if (wm.getAnnotationManager().getExecutor() == null) {
					// this does not bridge with the visualisations or simulation
					wm.getAnnotationManager().newExecutor();
				}
				wm.getAnnotationManager().getExecutor().debugREPL(wm.getIO());
			}).start();
		});

		Menu themes = new Menu("Themes");
		themeMenu(themes);

		debugMenu.getItems().addAll(dumpProgram, runSpim, jsREPL, themes);
		return debugMenu;
	}


}
