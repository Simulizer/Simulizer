package simulizer.settings.types;

import simulizer.settings.SettingValue;

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

}
