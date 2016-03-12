package simulizer.settings.types;

import java.util.ArrayList;
import java.util.List;

import simulizer.settings.SettingType;
import simulizer.settings.SettingValue;

/**
 * Represents an object setting
 * This can be used to group settings together
 * 
 * @author Michael
 *
 */
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
	public SettingType getSettingType() {
		return SettingType.OBJECT;
	}

	public SettingValue<?> get(String jsonSetting) {
		List<SettingValue<?>> subs = getValue();
		for (SettingValue<?> sub : subs) {
			if (sub.getJsonName().equals(jsonSetting))
				return sub;
		}
		return null;
	}

}
