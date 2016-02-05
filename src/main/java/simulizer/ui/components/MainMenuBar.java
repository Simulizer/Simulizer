package simulizer.ui.components;

import java.io.File;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.layout.Layout;
import simulizer.ui.theme.Theme;
import simulizer.ui.windows.CodeEditor;

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
			File f = openFileSelector(new ExtensionFilter("Assembly files *.s", "*.s"));
			((CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR)).loadFile(f);
		});

		// | |-- Save
		MenuItem saveItem = new MenuItem("Save");
		saveItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			if (editor.getCurrentFile() == null) {
				editor.setCurrentFile(saveFileSelector(new ExtensionFilter("Assembly files *.s", "*.s")));
			}
			editor.saveFile();
		});

		// | |-- Save As
		MenuItem saveAsItem = new MenuItem("Save As...");
		saveAsItem.setOnAction(e -> {
			CodeEditor editor = (CodeEditor) wm.findInternalWindow(WindowEnum.CODE_EDITOR);
			File saveFile = saveFileSelector(new ExtensionFilter("Assembly files *.s", "*.s"));
			if (saveFile != null) {
				editor.setCurrentFile(saveFile);
				editor.saveFile();
			}
		});
		fileMenu.getItems().addAll(newItem, loadItem, saveItem, saveAsItem);
		return fileMenu;
	}

	private Menu viewMenu() {
		// | View
		Menu viewMenu = new Menu("View");

		// | |-- Layouts
		Menu layoutMenu = new Menu("Layouts");
		for (Layout l : wm.getLayouts()) {
			MenuItem item = new MenuItem(l.getName());
			item.setOnAction(e -> wm.setLayout(l));
			layoutMenu.getItems().add(item);
		}

		// | | | -- Save Layout
		MenuItem saveLayoutItem = new MenuItem("Save Current Layout");
		saveLayoutItem.setOnAction(e -> {
			// TODO: Restrict user from changing folder
			File saveFile = saveFileSelector(new ExtensionFilter("JSON Files *.json", "*.json"));
			if (saveFile != null) wm.getLayouts().saveLayout(saveFile);
		});

		// | | | -- Reload Layouts
		MenuItem reloadLayoutItem = new MenuItem("Refresh Layouts");
		reloadLayoutItem.setOnAction(e -> wm.getLayouts().reload());
		layoutMenu.getItems().addAll(new SeparatorMenuItem(), saveLayoutItem, reloadLayoutItem);

		// | |-- Themes
		Menu themeMenu = new Menu("Themes");
		for (Theme t : wm.getThemes()) {
			MenuItem item = new MenuItem(t.getName());
			item.setOnAction(e -> wm.setTheme(t));
			themeMenu.getItems().add(item);
		}

		// | | | -- Reload Theme
		MenuItem reloadThemeItem = new MenuItem("Refresh Themes");
		reloadThemeItem.setOnAction(e -> {
			wm.getThemes().reload();
			wm.setTheme(wm.getThemes().getTheme());
		});
		themeMenu.getItems().addAll(new SeparatorMenuItem(), reloadThemeItem);

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

	private File saveFileSelector(ExtensionFilter... filter) {
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Save an assembly file");
		fc.getExtensionFilters().addAll(filter);
		return fc.showSaveDialog(wm.getPrimaryStage());
	}

	private File openFileSelector(ExtensionFilter... filter) {
		// Set the file chooser to open at the user's last directory
		final FileChooser fc = new FileChooser();
		fc.setInitialDirectory(new File(System.getProperty("user.dir")));
		fc.setTitle("Open an assembly file");
		fc.getExtensionFilters().addAll(filter);
		return fc.showOpenDialog(wm.getPrimaryStage());

	}

}
