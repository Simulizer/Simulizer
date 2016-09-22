package simulizer.ui.components.settings;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import simulizer.settings.types.DoubleSetting;

/**
 * Component to edit a DoubleSetting
 * 
 * @author Michael
 *
 */
public class DoubleControl extends VBox {

	public DoubleControl(DoubleSetting setting) {
		// Option Name
		Label title = new Label(setting.getHumanName());
		title.setFont(new Font(20));
		title.getStyleClass().add("title");
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
		getChildren().add(value);
	}
}
