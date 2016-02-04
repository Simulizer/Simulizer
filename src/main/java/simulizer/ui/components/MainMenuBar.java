package simulizer.ui.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import simulizer.ui.WindowManager;
import simulizer.ui.windows.CPUVisualiser;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.Logger;
import simulizer.ui.windows.Registers;

public class MainMenuBar extends MenuBar {

	// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
	public MainMenuBar(WindowManager wm) {
		// | File
		Menu fileMenu = new Menu("File");
		MenuItem newItem = new MenuItem("Create New");
		newItem.setOnAction(e -> wm.newFile());

		MenuItem loadItem = new MenuItem("Load Program");
		loadItem.setOnAction(e -> wm.loadFile());

		MenuItem saveItem = new MenuItem("Save Program");
		saveItem.setOnAction(e -> wm.saveFile());
		fileMenu.getItems().addAll(newItem, loadItem, saveItem);

		// | View
		Menu viewMenu = new Menu("View");
		// |-- Layouts
		Menu layoutMenu = new Menu("Layouts");
		MenuItem defaultLayoutItem = new MenuItem("Default Layout");
		defaultLayoutItem.setOnAction(e -> wm.defaultLayout());

		MenuItem alternativeLayoutItem = new MenuItem("Alternative Layout");
		alternativeLayoutItem.setOnAction(e -> wm.alternativeLayout());
		layoutMenu.getItems().addAll(defaultLayoutItem, alternativeLayoutItem);
		viewMenu.getItems().add(layoutMenu);

		// |-- Themes
		Menu themeMenu = new Menu("Themes");
		MenuItem defaultThemeItem = new MenuItem("Default");
		defaultThemeItem.setOnAction(e -> wm.setTheme("my-theme"));
		MenuItem loadThemeItem = new MenuItem("Load Theme...");
		themeMenu.getItems().addAll(defaultThemeItem, loadThemeItem);
		viewMenu.getItems().add(themeMenu);

		// | Debug Menu
		Menu debugMenu = new Menu("Debug");
		MenuItem windowLocation = new MenuItem("Window Locations");
		windowLocation.setOnAction(e -> wm.printWindowLocations());
		debugMenu.getItems().addAll(windowLocation);

		// | Windows
		Menu windowsMenu = new Menu("Add Window");
		MenuItem codeEditorItem = new MenuItem("Code Editor");
		codeEditorItem.setOnAction(e -> {
			CodeEditor editor = new CodeEditor();
			editor.setBounds(20, 35, 400, 685);
			wm.addWindow(editor);
		});
		MenuItem cpuVisualiserItem = new MenuItem("CPU Visualiser");
		cpuVisualiserItem.setOnAction(e -> {
			CPUVisualiser cpu = new CPUVisualiser();
			cpu.setBounds(20, 35, 400, 685);
			wm.addWindow(cpu);
		});
		MenuItem loggerItem = new MenuItem("Logger");
		loggerItem.setOnAction(e -> {
			Logger logger = new Logger();
			logger.setBounds(20, 35, 400, 685);
			wm.addWindow(logger);
		});
		MenuItem registersItem = new MenuItem("Registers");
		registersItem.setOnAction(e -> {
			Registers registers = new Registers();
			registers.setBounds(20, 35, 400, 685);
			wm.addWindow(registers);
		});
		windowsMenu.getItems().addAll(codeEditorItem, cpuVisualiserItem, loggerItem, registersItem);

		getMenus().addAll(fileMenu, viewMenu, windowsMenu, debugMenu);
	}

}
