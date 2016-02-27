package simulizer.ui.windows;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;
import simulizer.settings.types.ObjectSetting;
import simulizer.ui.interfaces.InternalWindow;

public class Options extends InternalWindow {

	private GridPane pane, values;
	private TreeView<String> folders;

	public Options() {
		pane = new GridPane();

		folders = new TreeView<String>();
		GridPane.setVgrow(folders, Priority.ALWAYS);
		GridPane.setHgrow(folders, Priority.SOMETIMES);
		pane.add(folders, 0, 0);

		values = new GridPane();
		GridPane.setVgrow(values, Priority.ALWAYS);
		GridPane.setHgrow(values, Priority.ALWAYS);
		pane.add(values, 1, 0);

		// TODO: Replace with actual componenets
		Label temp = new Label("Components go here");
		temp.setTextAlignment(TextAlignment.CENTER);
		GridPane.setVgrow(temp, Priority.ALWAYS);
		GridPane.setHgrow(temp, Priority.ALWAYS);
		values.add(temp, 0, 0);

		getContentPane().getChildren().add(pane);
	}

	@Override
	public void ready() {
		TreeItem<String> options = new TreeItem<String>("Options");
		options.setExpanded(true);

		ObjectSetting settings = (ObjectSetting) getWindowManager().getSettings().getAllSettings();
		createTree(options, settings);
		folders.setRoot(options);
		folders.getSelectionModel().select(options);
		super.ready();
	}

	private void createTree(TreeItem<String> root, ObjectSetting settings) {
		for (SettingValue<?> value : settings.getValue()) {
			if (value.getSettingType() == SettingType.OBJECT) {
				TreeItem<String> innerItem = new TreeItem<String>(value.getHumanName());
				createTree(innerItem, (ObjectSetting) value);
				innerItem.setExpanded(true);
				root.getChildren().add(innerItem);
			}
		}
		root.getChildren().sort((a, b) -> a.getValue().compareTo(b.getValue()));
	}

}
