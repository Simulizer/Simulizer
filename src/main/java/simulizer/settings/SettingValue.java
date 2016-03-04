package simulizer.settings;

import java.util.ArrayList;
import java.util.List;

public abstract class SettingValue<E> {
	private final String jsonName, humanName, description;
	private final List<SettingChangedListener<E>> listeners = new ArrayList<SettingChangedListener<E>>();
	private E value;

	public SettingValue(String jsonName, String humanName, String description, E defaultValue) {
		this.jsonName = jsonName;
		this.humanName = humanName;
		this.description = description;
		value = defaultValue;
	}

	public String getJsonName() {
		return jsonName;
	}

	public String getHumanName() {
		return humanName;
	}

	public String getDescription() {
		return description;
	}

	public E getValue() {
		return value;
	}

	public void setValue(E value) {
		this.value = value;

		// TODO: Check this doesn't need another thread
		// TODO: Use fully qualified jsonName
		// Notify Listeners of change
		for (SettingChangedListener<E> listener : listeners)
			listener.settingChanged(jsonName, value);
	}

	public void addListener(SettingChangedListener<E> listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	public void removeListener(SettingChangedListener<E> listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	public abstract boolean isValid(E value);

	public abstract SettingType getSettingType();

}
