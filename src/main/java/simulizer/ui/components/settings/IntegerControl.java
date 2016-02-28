package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.settings.types.IntegerSetting;

public class IntegerControl extends GridPane {

	public IntegerControl(IntegerSetting setting) {
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
		Spinner<Integer> value = new Spinner<Integer>(setting.getLowBound(), setting.getHighBound(), setting.getValue());
		value.setEditable(true);
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		value.getStyleClass().add("value");
		value.valueProperty().addListener((e) -> setting.setValue(value.getValue()));
		add(value, 1, 0);
	}
}
