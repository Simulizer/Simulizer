package simulizer.ui.windows;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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
	private VBox values;
	private TreeView<String> folders;
	private String theme;
	private boolean madeChanges = false;

	public Options() {
		pane = new BorderPane();

		folders = new TreeView<>();
		GridPane.setVgrow(folders, Priority.ALWAYS);
		GridPane.setHgrow(folders, Priority.NEVER);
		folders.getStyleClass().add("tree");
		folders.setCursor(Cursor.DEFAULT);
		folders.minWidth(400);
		pane.setLeft(folders);

		values = new VBox(5);
		values.setCursor(Cursor.DEFAULT);
		values.getStyleClass().add("options");
		values.setPadding(new Insets(0, 10, 0, 10));

		ScrollPane scroll = new ScrollPane();
		scroll.setContent(values);
		scroll.setFitToWidth(true);

		GridPane.setVgrow(scroll, Priority.ALWAYS);
		GridPane.setHgrow(scroll, Priority.SOMETIMES);
		GridPane.setFillWidth(scroll, true);
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
		folders.setShowRoot(false);
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
		// Alphabetical order
		root.getChildren().sort((a, b) -> a.getValue().compareTo(b.getValue()));
	}

	/**
	 * Shows the inner elements of the ObjectSetting
	 * 
	 * @param settings
	 *            the ObjectSettings to generate the inner elements for
	 */
	private void showComponents(ObjectSetting settings) {
		if (getWindowManager().getSettings().getAllSettings() == settings)
			return;

		// Remove all existing components
		values.getChildren().removeAll(values.getChildren());

		boolean first = true;
		for (SettingValue<?> value : settings.getValue()) {
			// Create appropriate control for the setting
			Node control = null;
			switch (value.getSettingType()) {
				case BOOLEAN:
					control = new BooleanControl(this, (BooleanSetting) value);
					break;

				case DOUBLE:
					control = new DoubleControl(this, (DoubleSetting) value);
					break;

				case INTEGER:
					control = new IntegerControl(this, (IntegerSetting) value);
					break;

				case STRING:
					control = new StringControl(this, (StringSetting) value);
					break;

				default:
					break;
			}

			// Add control to panel
			if (control != null) {
				// Separator
				if (!first)
					values.getChildren().add(new Separator());

				control.getStyleClass().add("control");
				values.getChildren().add(control);
			}

			first = false;
		}

		// Give the new components the theme
		if (!theme.equals(""))
			updateChildrenThemes(pane, this.theme);
	}

	@Override
	public void close() {
		if (madeChanges) {
			getWindowManager().getSettings().save();
			UIUtils.showInfoDialog("Restart Required", "To apply changes the application must restart");
		}
		super.close();
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

	public void madeChanges() {
		madeChanges = true;
	}
}
