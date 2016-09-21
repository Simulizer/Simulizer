package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.settings.types.DoubleSetting;

/**
 * Component to edit a DoubleSetting
 * 
 * @author Michael
 *
 */
public class DoubleControl extends GridPane {

	public DoubleControl(DoubleSetting setting) {
		// Option Name
		Label title = new Label(setting.getHumanName());
		GridPane.setHgrow(title, Priority.SOMETIMES);
		title.getStyleClass().add("title");
		add(title, 0, 0);

		// Option Desc
		Label desc = new Label(setting.getDescription());
		GridPane.setHgrow(desc, Priority.SOMETIMES);
		desc.getStyleClass().add("description");
		desc.setWrapText(true);
		add(desc, 0, 1);

		// Option Value
		TextField value = new TextField();
		value.setEditable(true);
		value.setText("" + setting.getValue());
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		value.getStyleClass().add("value");
		value.textProperty().addListener(e -> {
			boolean valid = false;
			double newValue = Double.NaN;
			try {
				newValue = Double.parseDouble(value.getText());
				valid = setting.isValid(newValue);
			} catch (NumberFormatException ex) {
				valid = false;
			}

			if (valid)
				setting.setValue(newValue);
			else
				value.setText("" + setting.getValue());
		});
		add(value, 1, 0);

		// Tooltip
		Tooltip tooltip = new Tooltip(setting.getDescription());
		Tooltip.install(title, tooltip);
		Tooltip.install(value, tooltip);
	}
}
