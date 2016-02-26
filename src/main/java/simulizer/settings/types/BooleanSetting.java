package simulizer.settings.types;

import simulizer.settings.SettingValue;

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
	public String getSettingType() {
		return "Boolean";
	}

}
