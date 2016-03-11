package simulizer.settings;

public interface SettingChangedListener<E> {
	
	/** Fires when a setting is changed
	 * @param setting the setting that changed
	 * @param newValue the new value
	 */
	public void settingChanged(String setting, E newValue);

}
