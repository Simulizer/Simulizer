package simulizer.ui.components.settings;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import simulizer.settings.types.BooleanSetting;

public class BooleanControl extends CheckBox {

	public BooleanControl(final BooleanSetting setting) {
		setText(setting.getHumanName());
		setSelected(setting.getValue());
		setTooltip(new Tooltip(setting.getDescription()));

		onActionProperty().addListener((e) -> setting.setValue(isSelected()));
	}
}
