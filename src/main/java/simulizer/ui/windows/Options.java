package simulizer.ui.windows;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;
import simulizer.settings.types.ObjectSetting;
import simulizer.ui.interfaces.InternalWindow;

public class Options extends InternalWindow {

	private GridPane pane, values;
	private ListView<String> folders;

	public Options() {
		pane = new GridPane();

		folders = new ListView<String>();
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
		ObservableList<String> items = FXCollections.observableArrayList();
		ObjectSetting settings = (ObjectSetting) getWindowManager().getSettings().getAllSettings();
		for (SettingValue<?> value : settings.getValue()) {
			if (value.getSettingType() == SettingType.OBJECT) {
				items.add(value.getHumanName());
			}
		}
		folders.setItems(items);
		super.ready();
	}

}
