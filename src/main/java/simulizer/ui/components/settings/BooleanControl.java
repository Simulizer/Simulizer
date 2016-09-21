package simulizer.ui.components.settings;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import simulizer.settings.types.BooleanSetting;

/**
 * Component to edit a BooleanSetting
 * 
 * @author Michael
 *
 */
public class BooleanControl extends GridPane {
	public BooleanControl(final BooleanSetting setting) {
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(setting.getValue());
		checkBox.setTooltip(new Tooltip(setting.getDescription()));

		checkBox.selectedProperty().addListener(e -> setting.setValue(checkBox.isSelected()));
		GridPane.setRowSpan(checkBox, 2);
		add(checkBox, 0, 0);
		
		Label name = new Label(setting.getHumanName());
		add(name, 1, 0);
		
		Label desc = new Label(setting.getDescription());
		add(desc, 1, 1);
	}
}
