package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.settings.SettingValue;
import simulizer.settings.types.DoubleSetting;
import simulizer.settings.types.IntegerSetting;

public class NumberControl<E> extends GridPane {

	public NumberControl(IntegerSetting setting) {
		this(setting, setting.getLowBound(), setting.getHighBound(), setting.getValue());
	}

	public NumberControl(DoubleSetting setting) {
		this(setting, setting.getLowBound(), setting.getHighBound(), setting.getValue());
	}

	private NumberControl(SettingValue<?> setting, double lowBound, double highBound, double initialValue) {
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
		Spinner<E> value = new Spinner<E>(lowBound, highBound, initialValue);
		value.setEditable(true);
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		desc.getStyleClass().add("value");
		add(value, 1, 0);
	}
}
