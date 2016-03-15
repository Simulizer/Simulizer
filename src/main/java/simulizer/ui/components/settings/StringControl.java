package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import simulizer.settings.types.StringSetting;

/**
 * Component to edit a StringSetting
 * 
 * @author Michael
 *
 */
public class StringControl extends GridPane {

	public StringControl(StringSetting setting) {
		// Option Name
		Label title = new Label(setting.getHumanName());
		GridPane.setHgrow(title, Priority.SOMETIMES);
		title.getStyleClass().add("title");
		add(title, 0, 0);

		// Option Value
		TextField value = new TextField();
		value.setText(setting.getValue());
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		value.getStyleClass().add("value");
		value.textProperty().addListener(e -> {
			try {
				setting.setValue(value.getText());
			} catch (IllegalArgumentException ex) {
				// TODO: Notify user of invalid setting
			}
		});
		add(value, 1, 0);

		// Tooltip
		Tooltip tooltip = new Tooltip(setting.getDescription());
		Tooltip.install(title, tooltip);
		Tooltip.install(value, tooltip);
	}
}
