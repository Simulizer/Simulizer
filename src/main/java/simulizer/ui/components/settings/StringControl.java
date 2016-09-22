package simulizer.ui.components.settings;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import simulizer.settings.types.StringSetting;

/**
 * Component to edit a StringSetting
 * 
 * @author Michael
 *
 */
public class StringControl extends VBox {

	public StringControl(StringSetting setting) {
		setSpacing(3);
		
		// Option Name
		Label title = new Label(setting.getHumanName());
		title.getStyleClass().add("title");
		title.setFont(new Font(20));
		getChildren().add(title);

		// Option Desc
		Label desc = new Label(setting.getDescription());
		desc.getStyleClass().add("description");
		desc.setWrapText(true);
		desc.setFont(new Font(14));
		getChildren().add(desc);

		// Option Value
		TextField value = new TextField();
		value.setText(setting.getValue());
		value.getStyleClass().add("value");
		value.setManaged(true);
		value.textProperty().addListener(e -> {
			try {
				setting.setValue(value.getText());
			} catch (IllegalArgumentException ex) {
				// TODO: Notify user of invalid setting
			}
		});
		getChildren().add(value);
	}
}
