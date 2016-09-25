package simulizer.ui.components.settings;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import simulizer.settings.types.IntegerSetting;
import simulizer.ui.windows.Options;

/**
 * Component to edit a IntegerSetting
 * 
 * @author Michael
 *
 */
public class IntegerControl extends VBox {

	public IntegerControl(Options o, IntegerSetting setting) {
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
		Spinner<Integer> value = new Spinner<>();
		IntegerSpinnerValueFactory factory = new IntegerSpinnerValueFactory(0, 0);
		factory.setMax(setting.getHighBound());
		factory.setMin(setting.getLowBound());
		factory.setValue(setting.getValue());
		value.setValueFactory(factory);
		value.setEditable(true);
		value.getStyleClass().add("value");
		value.setPrefWidth(Double.MAX_VALUE);
		value.valueProperty().addListener(e -> {
			if (setting.isValid(value.getValue())) {
				o.madeChanges();
				setting.setValue(value.getValue());
			} else
				value.getValueFactory().setValue(setting.getValue());
		});
		getChildren().add(value);

		// Tooltip
		// Tooltip tooltip = new Tooltip(setting.getDescription());
		// Tooltip.install(title, tooltip);
		// Tooltip.install(value, tooltip);
	}
}
