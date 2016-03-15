package simulizer.ui.components.settings;

import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
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

		// Option Value
		DoubleSpinnerValueFactory factory = new DoubleSpinnerValueFactory(setting.getLowBound(), setting.getHighBound(), setting.getValue());
		Spinner<Double> value = new Spinner<Double>(factory);
		value.setEditable(true);
		GridPane.setRowSpan(value, 2);
		GridPane.setVgrow(value, Priority.SOMETIMES);
		GridPane.setValignment(value, VPos.CENTER);
		value.getStyleClass().add("value");
		value.valueProperty().addListener((e) -> {
			try {
				setting.setValue(value.getValue());
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
