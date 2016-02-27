package simulizer.settings;

public abstract class SettingValue<E> {
	private final String jsonName, humanName, description;
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
	}

	public abstract boolean isValid(E value);

	public abstract SettingType getSettingType();

}
