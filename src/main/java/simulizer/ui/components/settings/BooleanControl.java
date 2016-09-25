package simulizer.ui.components.settings;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import simulizer.settings.types.BooleanSetting;
import simulizer.ui.windows.Options;

/**
 * Component to edit a BooleanSetting
 * 
 * @author Michael
 *
 */
public class BooleanControl extends GridPane {
	public BooleanControl(final Options o, final BooleanSetting setting) {
		CheckBox checkBox = new CheckBox();
		checkBox.setSelected(setting.getValue());
		checkBox.setTooltip(new Tooltip(setting.getDescription()));

		checkBox.selectedProperty().addListener(e -> {
			o.madeChanges();
			setting.setValue(checkBox.isSelected());
		});
		GridPane.setRowSpan(checkBox, 2);
		add(checkBox, 0, 0);

		Label name = new Label(setting.getHumanName());
		name.setFont(new Font(20));
		add(name, 1, 0);

		if (!setting.getDescription().equals("")) {
			Label desc = new Label(setting.getDescription());
			desc.setWrapText(true);
			desc.setFont(new Font(14));
			add(desc, 1, 1);
		}
	}
}
