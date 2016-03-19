package simulizer.settings;

/**
 * A Setting Value, which can be used to generate the GUI
 * 
 * @author Michael
 *
 * @param <E>
 *            The type of the value to be stored
 */
public abstract class SettingValue<E> {
	private final String jsonName, humanName, description;
	private E value;

	/**
	 * Creates a Setting-Value pair
	 * 
	 * @param jsonName
	 *            the json name of the current setting
	 * @param humanName
	 *            the human readable name of the current setting
	 * @param description
	 *            a description of what the setting does
	 * @param defaultValue
	 *            the default value of the setting
	 */
	public SettingValue(String jsonName, String humanName, String description, E defaultValue) {
		this.jsonName = jsonName;
		this.humanName = humanName;
		this.description = description;
		value = defaultValue;
	}

	/**
	 * @return the json name
	 */
	public String getJsonName() {
		return jsonName;
	}

	/**
	 * @return the human readable name
	 */
	public String getHumanName() {
		return humanName;
	}

	/**
	 * @return the description of the setting
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the value
	 */
	public E getValue() {
		return value;
	}

	/**
	 * Changes the setting value and notifies all listeners
	 * 
	 * @param value
	 *            the value to set the setting to
	 */
	public void setValue(E value) {
		if (!isValid(value))
			throw new IllegalArgumentException(value + " is not valid for this setting");
		this.value = value;
	}

	/**
	 * Provides validation for the setting.
	 * 
	 * @param value
	 *            the value to check
	 * @return whether the value is acceptable for this setting
	 */
	public abstract boolean isValid(E value);

	/**
	 * @return the type of this setting
	 */
	public abstract SettingType getSettingType();

}
