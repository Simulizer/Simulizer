package simulizer.ui.components;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.assembler.Assembler;
import simulizer.assembler.extractor.problem.StoreProblemLogger;
import simulizer.assembler.representation.Program;
import simulizer.assembler.representation.ProgramStringBuilder;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.theme.Theme;
import simulizer.ui.windows.AceEditor;
import simulizer.ui.windows.Labels;
import simulizer.ui.windows.Registers;

// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
public class MainMenuBar extends MenuBar {

	private WindowManager wm;

	public MainMenuBar(WindowManager wm) {
		this.wm = wm;
		getMenus().addAll(fileMenu(), viewMenu(), runMenu(), windowsMenu(), debugMenu());
	}

	private AceEditor getEditor() {
		return (AceEditor) wm.getWorkspace().openInternalWindow(WindowEnum.ACE_EDITOR);
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
			File f = openFileSelector("Open an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			if(f != null) {
				getEditor().loadFile(f);
			}
		});
		loadItem.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			if(getEditor().getCurrentFile() == null) {
				File f = saveFileSelector("Save an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
				if(f != null) {
					getEditor().saveAs(f);
				}
			}
			getEditor().saveFile();
		});
		saveItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setOnAction(e -> {
			File f = saveFileSelector("Save an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			if(f != null) {
				getEditor().saveAs(f);
			}
		});

		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> System.exit(0));

		fileMenu.getItems().addAll(newItem, loadItem, saveItem, saveAsItem, exitItem);
		return fileMenu;
	}

	private Menu viewMenu() {
		// | View
		Menu viewMenu = new Menu("View");

		// | |-- Layouts
		Menu layoutMenu = new Menu("Layouts");
		layoutMenu(layoutMenu);

		// | |-- Themes
		Menu themeMenu = new Menu("Themes");
		themeMenu(themeMenu);

		viewMenu.getItems().addAll(layoutMenu, themeMenu);
		return viewMenu;
	}

	private void layoutMenu(Menu menu) {
		menu.getItems().clear();

		for (Layout l : wm.getLayouts()) {
			String name = l.getName();
			MenuItem item = new MenuItem(name.endsWith(".json") ? name.substring(0,name.length()-5) : name);
			item.setOnAction(e -> wm.getLayouts().setLayout(l));
			menu.getItems().add(item);
		}

		// | | | -- Save Layout
		MenuItem saveLayoutItem = new MenuItem("Save Current Layout");
		saveLayoutItem.setOnAction(e -> {
			File saveFile = saveFileSelector("Save layout", new File("layouts"), new ExtensionFilter("JSON Files *.json", "*.json"));
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

	private Menu runMenu() {
		// To be moved to separate buttons later
		Menu runMenu = new Menu("Run Code");

		MenuItem runProgram = new MenuItem("Run Program");
		runProgram.setOnAction(e -> {
			StoreProblemLogger log = new StoreProblemLogger();
			Assembler a = new Assembler();
			Program p = a.assemble(getEditor().getText(), log);
			if(p != null) {
				wm.runProgram(p);
			} else {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Could Not Run");
				int size = log.getProblems().size();
				if(size == 1) {
					alert.setHeaderText("The Program Contains An Error!");
				} else {
					alert.setHeaderText("The Program Contains " + size + " Errors!");
				}
				alert.setContentText("You must fix them before you can\nexecute the program.");
				alert.show();

				getEditor().setProblems(log.getProblems());
			}
		});

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

		MenuItem singleStep = new MenuItem("Single Step");
		singleStep.setDisable(true);

		MenuItem simplePipeline = new MenuItem("Single Step (pipeline)");
		simplePipeline.setDisable(true);

		MenuItem stop = new MenuItem("Stop Simulation");
		stop.setOnAction(e -> wm.stopCPU());

		runMenu.getItems().addAll(runProgram, setClockSpeed, singleStep, simplePipeline, stop);
		return runMenu;
	}

	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Add Window");
		for (WindowEnum wenum : WindowEnum.values()) {
			MenuItem item = new MenuItem(wenum.toString());
			item.setOnAction(e -> wm.getWorkspace().openInternalWindow(wenum));
			windowsMenu.getItems().add(item);
		}
		return windowsMenu;
	}

	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");

		MenuItem delWindows = new MenuItem("Close All Windows");
		delWindows.setOnAction(e -> wm.getWorkspace().closeAll());

		MenuItem emphWindow = new MenuItem("Refresh Registers");
		emphWindow.setOnAction(e -> {
			Registers reg = (Registers) wm.getWorkspace().openInternalWindow(WindowEnum.REGISTERS);
			reg.refreshData();
		});

		// Labels
		MenuItem labelRefresh = new MenuItem("Refresh Labels");
		labelRefresh.setOnAction(e -> {
			Labels labels = (Labels) wm.getWorkspace().findInternalWindow(WindowEnum.LABELS);
			labels.refreshData();
		});
		// End labels

		CheckMenuItem lineWrap = new CheckMenuItem("Line Wrap");
		//TODO: extract this information from settings. Cannot get from editor until editor
		// loaded so getting from settings would be the sensible alternative
		lineWrap.setSelected(false);
		lineWrap.setOnAction(e -> getEditor().setWrap(!getEditor().getWrap()));

		MenuItem dumpProgram = new MenuItem("Dump Assembled Program");
		dumpProgram.setOnAction(e -> {
			Assembler a = new Assembler();
			Program p = a.assemble(getEditor().getText(), null);
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

		debugMenu.getItems().addAll(delWindows, emphWindow, lineWrap, dumpProgram, labelRefresh);
		return debugMenu;
	}

	private File saveFileSelector(String title, File folder, ExtensionFilter... filter) {
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(folder);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showSaveDialog(wm.getPrimaryStage());
	}

	private File openFileSelector(String title, File folder, ExtensionFilter... filter) {
		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(folder);
		fc.setTitle(title);
		fc.getExtensionFilters().addAll(filter);
		return fc.showOpenDialog(wm.getPrimaryStage());
	}

}
