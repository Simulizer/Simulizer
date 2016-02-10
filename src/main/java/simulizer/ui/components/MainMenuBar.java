package simulizer.ui.components;

import java.io.File;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.Main;
import simulizer.highlevel.visualisation.TowerOfHanoiVisualiser;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layouts;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.HighLevelVisualisation;

// Thanks: http://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm
public class MainMenuBar extends MenuBar {

	private WindowManager wm;

	public MainMenuBar(WindowManager wm) {
		this.wm = wm;
		getMenus().addAll(fileMenu(), viewMenu(), windowsMenu(), debugMenu());
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
			File f = openFileSelector();
			((CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR)).loadFile(f);
		});

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			if (editor.getCurrentFile() == null) {
				editor.setCurrentFile(saveFileSelector());
			}
			editor.saveFile();
		});

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			File saveFile = saveFileSelector();
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

		// | | | -- Default Layout
		MenuItem defaultLayoutItem = new MenuItem("Default Layout");
		defaultLayoutItem.setOnAction(e -> wm.setLayout(Layouts.original()));

		// | | | -- Alternative Layout
		MenuItem alternativeLayoutItem = new MenuItem("Alternative Layout");
		alternativeLayoutItem.setOnAction(e -> wm.setLayout(Layouts.alternative()));

		// | | | -- High Level Only Layout
		MenuItem highLevelLayoutItem = new MenuItem("High Level Test");
		highLevelLayoutItem.setOnAction(e -> {
			wm.setLayout(Layouts.onlyHighLevel());
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
		});

		layoutMenu.getItems().addAll(defaultLayoutItem, alternativeLayoutItem, highLevelLayoutItem);

		// | |-- Themes
		Menu themeMenu = new Menu("Themes");
		File folder = new File(Main.RESOURCES + "/themes/");
		// Check all folders in the theme folder
		for (File themeFolder : folder.listFiles()) {
			if (themeFolder.isDirectory()) {
				// Check for a theme.json file
				File[] themeJSONs = themeFolder.listFiles((e) -> e.getName().toLowerCase().equals("theme.json"));
				if (themeJSONs.length == 1) {
					// TODO: Parse the JSON file
					MenuItem folderThemeItem = new MenuItem(themeFolder.getName());
					folderThemeItem.setOnAction(e -> wm.setTheme("themes/" + themeFolder.getName()));
					themeMenu.getItems().addAll(folderThemeItem);
				}
			}
		}

		// | | | -- Load Theme
		MenuItem loadThemeItem = new MenuItem("Load Theme...");
		themeMenu.getItems().addAll(loadThemeItem);

		viewMenu.getItems().addAll(layoutMenu, themeMenu);
		return viewMenu;
	}

	private Menu windowsMenu() {
		Menu windowsMenu = new Menu("Add Window");
		for (WindowEnum wenum : WindowEnum.values()) {
			MenuItem item = new MenuItem(wenum.toString());
			item.setOnAction(e -> {
				InternalWindow w = wenum.createNewWindow();
				w.setBounds(20, 35, 400, 685);
				wm.addWindows(w);
			});
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
		debugMenu.getItems().addAll(windowLocation, delWindows);
		return debugMenu;
	}

	private File saveFileSelector() {
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Save an assembly file");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));
		return fc.showSaveDialog(wm.getPrimaryStage());
	}

	private File openFileSelector() {
		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Open an assembly file");
		fc.getExtensionFilters().addAll(new ExtensionFilter("Assembly files *.s", "*.s"));
		return fc.showOpenDialog(wm.getPrimaryStage());

	}

}
