package simulizer.ui.components.settings;

import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import simulizer.settings.types.DoubleSetting;
import simulizer.ui.windows.Options;

/**
 * Component to edit a DoubleSetting
 * 
 * @author Michael
 *
 */
public class DoubleControl extends VBox {

	public DoubleControl(Options o, DoubleSetting setting) {
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
		Spinner<Double> value = new Spinner<>();
		DoubleSpinnerValueFactory factory = new DoubleSpinnerValueFactory(0, 0);
		factory.setMax(setting.getHighBound());
		factory.setMin(setting.getLowBound());
		factory.setValue(setting.getValue());
		double step = (setting.getHighBound() - setting.getLowBound()) / 1000;
		factory.setAmountToStepBy(step <= 20 ? step : 20);
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
	}
}
