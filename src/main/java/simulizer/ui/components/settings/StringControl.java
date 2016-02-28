package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.settings.types.StringSetting;

public class StringControl extends GridPane {

	public StringControl(StringSetting setting) {
		// Option Name
		Label title = new Label(setting.getHumanName());
		GridPane.setHgrow(title, Priority.SOMETIMES);
		title.getStyleClass().add("title");
		add(title, 0, 0);

		// Option Name
		Label desc = new Label(setting.getDescription());
		GridPane.setHgrow(desc, Priority.SOMETIMES);
		desc.getStyleClass().add("description");
		add(desc, 0, 1);

		// Option Value
		TextField value = new TextField();
		value.setText(setting.getValue());
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		desc.getStyleClass().add("value");
		add(value, 1, 0);
	}
}
