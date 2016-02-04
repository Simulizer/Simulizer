package simulizer.ui.components;

import java.io.File;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
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

	private Menu viewMenu() {
		// | View
		Menu viewMenu = new Menu("View");

		// | |-- Layouts
		Menu layoutMenu = new Menu("Layouts");

		// | | | -- Default Layout
		MenuItem defaultLayoutItem = new MenuItem("Default Layout");
		defaultLayoutItem.setOnAction(e -> wm.defaultLayout());

		// | | | -- Alternative Layout
		MenuItem alternativeLayoutItem = new MenuItem("Alternative Layout");
		alternativeLayoutItem.setOnAction(e -> wm.alternativeLayout());
		layoutMenu.getItems().addAll(defaultLayoutItem, alternativeLayoutItem);

		// | |-- Themes
		Menu themeMenu = new Menu("Themes");

		// | | | -- Default Theme
		MenuItem defaultThemeItem = new MenuItem("Default");
		defaultThemeItem.setOnAction(e -> wm.setTheme("my-theme"));

		// | | | -- Load Theme
		MenuItem loadThemeItem = new MenuItem("Load Theme...");
		themeMenu.getItems().addAll(defaultThemeItem, loadThemeItem);

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
				wm.addWindow(w);
			});
			windowsMenu.getItems().add(item);
		}
		return windowsMenu;
	}

	private Menu debugMenu() {
		Menu debugMenu = new Menu("Debug");
		MenuItem windowLocation = new MenuItem("Window Locations");
		windowLocation.setOnAction(e -> wm.printWindowLocations());
		debugMenu.getItems().addAll(windowLocation);
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
