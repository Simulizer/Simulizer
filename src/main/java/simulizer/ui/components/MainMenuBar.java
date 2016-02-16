package simulizer.ui.components;

import java.io.File;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.highlevel.visualisation.TowerOfHanoiVisualiser;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.theme.Theme;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.HighLevelVisualisation;

// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
public class MainMenuBar extends MenuBar {

	private WindowManager wm;

	public MainMenuBar(WindowManager wm) {
		this.wm = wm;
		getMenus().addAll(fileMenu(), viewMenu(), runMenu(), windowsMenu(), debugMenu());
	}

	private Menu fileMenu() {
		// | File
		Menu fileMenu = new Menu("File");

		// | |-- New
		MenuItem newItem = new MenuItem("New");
		newItem.setOnAction(e -> ((CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR)).newFile());

		// | |-- Open
		MenuItem loadItem = new MenuItem("Open");
		loadItem.setOnAction(e -> {
			File f = openFileSelector("Open an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			((CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR)).loadFile(f);
		});

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			if (editor.getCurrentFile() == null) {
				editor.setCurrentFile(saveFileSelector("Save an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s")));
			}
			editor.saveFile();
		});

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			File saveFile = saveFileSelector("Save an assembly file", new File("code"), new ExtensionFilter("Assembly files *.s", "*.s"));
			if (saveFile != null) {
				editor.setCurrentFile(saveFile);
				editor.saveFile();
			}
		});
		fileMenu.getItems().addAll(newItem, loadItem, saveItem, saveAsItem);
		return fileMenu;
	}

	private class PegWrapper {
		public int a;
		public int b;

		public PegWrapper(int a, int b) {
			this.a = a;
			this.b = b;
		}
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
			MenuItem item = new MenuItem(l.getName());
			item.setOnAction(e -> wm.setLayout(l));
			menu.getItems().add(item);
		}

		// | | | -- High Level Only Layout
		MenuItem highLevelLayoutItem = new MenuItem("High Level Test");
		highLevelLayoutItem.setOnAction(e -> {
			HighLevelVisualisation hv = (HighLevelVisualisation) wm.findInternalWindow(WindowEnum.HIGH_LEVEL_VISUALISATION);
			// ListVisualiser<Integer> lv = new
			// ListVisualiser<>(hv.getDrawingPane(), 1000, 400, Arrays.asList(3,
			// 1, 4, 1, 5));
			// hv.addEventHandler(KeyEvent.KEY_TYPED, f -> {
			// int n = Integer.valueOf(f.getCharacter());
			// lv.swap(n % 5, (n+1) % 5);
			// lv.commit();
			// });

			PegWrapper p = new PegWrapper(-1,-1);
			TowerOfHanoiVisualiser tv = new TowerOfHanoiVisualiser(hv.getDrawingPane(), 1000, 400, 0, 4);
			hv.addEventHandler(KeyEvent.KEY_TYPED, f -> {
				int val = Integer.valueOf(f.getCharacter());

				if (p.a == -1) p.a = val;
				else {
					p.b = val;
					
					tv.move(p.a - 1, p.b - 1);
					tv.commit();
					
					p.a = -1;
					p.b = -1;
				}
				
			});
			tv.setRate(2000);
		});

		// | | | -- Save Layout
		MenuItem saveLayoutItem = new MenuItem("Save Current Layout");
		saveLayoutItem.setOnAction(e -> {
			File saveFile = saveFileSelector("Save layout", new File("layouts"), new ExtensionFilter("JSON Files *.json", "*.json"));
			if (saveFile != null) {
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
			item.setOnAction(e -> wm.setTheme(t));
			menu.getItems().add(item);
		}

		MenuItem reloadThemeItem = new MenuItem("Refresh Themes");
		reloadThemeItem.setOnAction(e -> {
			wm.getThemes().reload();
			themeMenu(menu);
			wm.setTheme(wm.getThemes().getTheme());
		});

		menu.getItems().addAll(new SeparatorMenuItem(), reloadThemeItem);
		return menu;
	}

	private Menu runMenu() {
		// To be moved to separate buttons later
		Menu runMenu = new Menu("Run Code");
		MenuItem runProgram = new MenuItem("Run Program");
		runProgram.setOnAction(e -> System.out.println("Does nothing yet"));
		MenuItem singleStep = new MenuItem("Single Step");
		singleStep.setOnAction(e -> System.out.println("Does nothing yet"));
		MenuItem simplePipeline = new MenuItem("Single Step (pipeline)");
		simplePipeline.setOnAction(e -> System.out.println("Does nothing yet"));
		runMenu.getItems().addAll(runProgram, singleStep, simplePipeline);
		return runMenu;
	}

	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Add Window");
		for (WindowEnum wenum : WindowEnum.values()) {
			MenuItem item = new MenuItem(wenum.toString());
			item.setOnAction(e -> wm.findInternalWindow(wenum));
			windowsMenu.getItems().add(item);
		}
		return windowsMenu;
	}

	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");
		MenuItem windowLocation = new MenuItem("Window Locations");
		windowLocation.setOnAction(e -> wm.printWindowLocations());
		MenuItem delWindows = new MenuItem("Close All Windows");
		delWindows.setOnAction(e -> wm.closeAll());
		MenuItem emphWindow = new MenuItem("Emphisise Window");
		emphWindow.setOnAction(e -> wm.findInternalWindow(WindowEnum.REGISTERS).emphasise());
		debugMenu.getItems().addAll(windowLocation, delWindows, emphWindow);
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
