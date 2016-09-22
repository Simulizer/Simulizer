package simulizer.ui.components.settings;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import simulizer.settings.types.IntegerSetting;

/**
 * Component to edit a IntegerSetting
 * 
 * @author Michael
 *
 */
public class IntegerControl extends VBox {

	public IntegerControl(IntegerSetting setting) {
		// Option Name
		Label title = new Label(setting.getHumanName());
		title.getStyleClass().add("title");
		title.setFont(new Font(20));
		getChildren().add(title);

		// Option Desc
		Label desc = new Label(setting.getDescription());
		desc.getStyleClass().add("description");
		desc.setFont(new Font(14));
		desc.setWrapText(true);
		getChildren().add(desc);

		// Option Value
		TextField value = new TextField();
		value.setEditable(true);
		value.setText("" + setting.getValue());
		value.getStyleClass().add("value");
		value.textProperty().addListener(e -> {
			boolean valid = false;
			int newValue = 0;
			try {
				newValue = Integer.parseInt(value.getText());
				valid = setting.isValid(newValue);
			} catch (NumberFormatException ex) {
				valid = false;
			}

			if (valid)
				setting.setValue(newValue);
			else
				value.setText("" + setting.getValue());
		});
		getChildren().add(value);

		// Tooltip
		// Tooltip tooltip = new Tooltip(setting.getDescription());
		// Tooltip.install(title, tooltip);
		// Tooltip.install(value, tooltip);
	}
}
