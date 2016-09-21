package simulizer.ui.windows;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;
import simulizer.settings.types.BooleanSetting;
import simulizer.settings.types.DoubleSetting;
import simulizer.settings.types.IntegerSetting;
import simulizer.settings.types.ObjectSetting;
import simulizer.settings.types.StringSetting;
import simulizer.ui.components.settings.BooleanControl;
import simulizer.ui.components.settings.DoubleControl;
import simulizer.ui.components.settings.IntegerControl;
import simulizer.ui.components.settings.StringControl;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;
import simulizer.utils.UIUtils;

/**
 * An InternalWindow to change the settings of the application
 * 
 * @author Michael
 *
 */
public class Options extends InternalWindow {

	private BorderPane pane;
	private GridPane values;
	private TreeView<String> folders;
	private String theme;

	public Options() {
		pane = new BorderPane();

		folders = new TreeView<>();
		GridPane.setVgrow(folders, Priority.ALWAYS);
		GridPane.setHgrow(folders, Priority.NEVER);
		folders.getStyleClass().add("tree");
		folders.setCursor(Cursor.DEFAULT);
		folders.minWidth(400);
		pane.setLeft(folders);

		values = new GridPane();
		values.setCursor(Cursor.DEFAULT);
		values.getStyleClass().add("options");
		values.setPadding(new Insets(0, 10, 0, 10));

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(values);
		GridPane.setVgrow(scroll, Priority.ALWAYS);
		GridPane.setHgrow(scroll, Priority.SOMETIMES);
		pane.setCenter(scroll);

		getContentPane().getChildren().add(pane);
	}

	@Override
	public void ready() {
		ObjectSetting settings = getWindowManager().getSettings().getAllSettings();

		FolderItem options = new FolderItem(settings);
		options.setExpanded(true);
		createTree(options, settings);

		folders.setRoot(options);
		folders.getSelectionModel().select(options);
		folders.getSelectionModel().selectedItemProperty().addListener((e) -> {
			FolderItem item = (FolderItem) folders.getSelectionModel().getSelectedItem();
			showComponents(item.getObjectSetting());
		});

		showComponents(settings);
		super.ready();
	}

	/**
	 * Creates the options tree
	 * 
	 * @param root
	 *            the options folder root
	 * @param settings
	 *            the object setting
	 */
	private void createTree(FolderItem root, ObjectSetting settings) {
		settings.getValue().stream().filter(value -> value.getSettingType() == SettingType.OBJECT).forEach(value -> {
			FolderItem innerItem = new FolderItem((ObjectSetting) value);
			createTree(innerItem, (ObjectSetting) value);
			innerItem.setExpanded(true);
			root.getChildren().add(innerItem);
		});
		root.getChildren().sort((a, b) -> a.getValue().compareTo(b.getValue()));
	}

	/**
	 * Shows the inner elements of the ObjectSetting
	 * 
	 * @param settings
	 *            the ObjectSettings to generate the inner elements for
	 */
	private void showComponents(ObjectSetting settings) {
		// Remove all existing components
		values.getChildren().removeAll(values.getChildren());
		int rowCount = 1;

		// Folder Title
		Label title = new Label(settings.getHumanName());
		title.setTextAlignment(TextAlignment.CENTER);
		GridPane.setHgrow(title, Priority.ALWAYS);
		GridPane.setHalignment(title, HPos.CENTER);
		title.getStyleClass().add("title");
		values.add(title, 0, rowCount++);

		// Folder description Tag
		if (!settings.getDescription().equals("")) {
			Label description = new Label(settings.getDescription());
			GridPane.setHgrow(description, Priority.ALWAYS);
			description.getStyleClass().add("description");
			values.add(description, 0, rowCount++);
		}

		// Separator
		Separator sep = new Separator();
		GridPane.setHgrow(sep, Priority.ALWAYS);
		values.add(sep, 0, rowCount++);

		for (SettingValue<?> value : settings.getValue()) {
			// Create appropriate control for the setting
			Node control = null;
			switch (value.getSettingType()) {
				case BOOLEAN:
					control = new BooleanControl((BooleanSetting) value);
					break;

				case DOUBLE:
					control = new DoubleControl((DoubleSetting) value);
					break;

				case INTEGER:
					control = new IntegerControl((IntegerSetting) value);
					break;

				case STRING:
					control = new StringControl((StringSetting) value);
					break;

				default:
					break;
			}

			// Add control to panel
			if (control != null) {
				GridPane.setHgrow(control, Priority.ALWAYS);
				control.getStyleClass().add("control");
				values.add(control, 0, rowCount++);
			}
		}

		// Give the new components the theme
		if (!theme.equals(""))
			updateChildrenThemes(pane, this.theme);
	}

	@Override
	public void close() {
		getWindowManager().getSettings().save();
		if (UIUtils.confirm("Restart Required", "To apply any changes the application must restart.\nRestart now?")) {
			super.close();
			getWindowManager().restart();
		} else {
			super.close();
		}
	}

	@Override
	public void setTheme(Theme theme) {
		// TODO: Fix themes
		super.setTheme(theme);
		this.theme = theme.getStyleSheet("options.css");
		updateChildrenThemes(pane, this.theme);
	}

	private void updateChildrenThemes(Control pane, String stylesheet) {
		if (pane != null) {
			pane.getStylesheets().removeAll(pane.getStylesheets());
			pane.getStylesheets().add(stylesheet);
		}
	}

	private void updateChildrenThemes(Pane pane, String stylesheet) {
		if (pane != null) {
			pane.getStylesheets().removeAll(pane.getStylesheets());
			pane.getStylesheets().add(stylesheet);
			for (Node n : pane.getChildren()) {

				// Update the children
				if (n instanceof Control)
					updateChildrenThemes((Control) n, stylesheet);
				else if (n instanceof Pane)
					updateChildrenThemes((Pane) n, stylesheet);
			}
		}
	}

	@SuppressWarnings("WeakerAccess") // getters need to be public
	private static class FolderItem extends TreeItem<String> {

		private final ObjectSetting setting;

		FolderItem(ObjectSetting setting) {
			super(setting.getHumanName());
			this.setting = setting;
		}

		public ObjectSetting getObjectSetting() {
			return setting;
		}
	}
}
