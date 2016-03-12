package simulizer.settings.types;

import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;

/**
 * Represents a boolean setting
 * 
 * @author Michael
 *
 */
public class BooleanSetting extends SettingValue<Boolean> {

	public BooleanSetting(String jsonName, String humanName, String description) {
		super(jsonName, humanName, description, false);
	}

	public BooleanSetting(String jsonName, String humanName, String description, boolean defaultValue) {
		super(jsonName, humanName, description, defaultValue);
	}

	@Override
	public boolean isValid(Boolean value) {
		return false;
	}

	@Override
	public SettingType getSettingType() {
		return SettingType.BOOLEAN;
	}

}
