package simulizer.settings.types;

import java.util.ArrayList;
import java.util.List;

import simulizer.settings.SettingValue;

public class ObjectSetting extends SettingValue<List<SettingValue<?>>> {

	public ObjectSetting(String jsonName, String humanName) {
		super(jsonName, humanName, "", new ArrayList<SettingValue<?>>());
	}
	
	public ObjectSetting(String jsonName, String humanName, String description) {
		super(jsonName, humanName, description, new ArrayList<SettingValue<?>>());
	}

	@Override
	public boolean isValid(List<SettingValue<?>> value) {
		return true;
	}

	public ObjectSetting add(SettingValue<?> value) {
		getValue().add(value);
		return this;
	}

	@Override
	public String getSettingType() {
		return "Object";
	}

}
