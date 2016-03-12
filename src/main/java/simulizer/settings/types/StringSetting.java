package simulizer.settings.types;

import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;

/**
 * Represents a String setting
 * 
 * @author Michael
 *
 */
public class StringSetting extends SettingValue<String> {

	public StringSetting(String jsonName, String humanName, String description) {
		super(jsonName, humanName, description, "");
	}

	public StringSetting(String jsonName, String humanName, String description, String defaultValue) {
		super(jsonName, humanName, description, defaultValue);
	}

	@Override
	public boolean isValid(String value) {
		return true;
	}

	@Override
	public SettingType getSettingType() {
		return SettingType.STRING;
	}

}
