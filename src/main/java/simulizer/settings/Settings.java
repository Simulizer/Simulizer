package simulizer.settings;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import simulizer.settings.types.BooleanSetting;
import simulizer.settings.types.DoubleSetting;
import simulizer.settings.types.IntegerSetting;
import simulizer.settings.types.ObjectSetting;
import simulizer.settings.types.StringSetting;

public class Settings {
	private ObjectSetting settings = new ObjectSetting("settings", "Settings");

	public static Settings loadSettings(File json) throws IOException {
		JsonParser parser = new JsonParser();
		JsonElement jsonElement = parser.parse(new FileReader(json));
		JsonObject jsonObject = jsonElement.getAsJsonObject();
		Settings settings = new Settings(jsonObject);
		return settings;
	}

	private Settings(JsonObject jsonObject) {
		// Sets up the structure of the settings file
		// @formatter:off
		settings.add(new ObjectSetting("workspace", "Workspace")
					.add(new StringSetting("theme", "Default Theme", "The default theme to load", "default"))
					.add(new StringSetting("layout", "Default Layout", "The default layout to load", "default.json"))
					.add(new ObjectSetting("scale-ui", "Scale User Interface")
							.add(new BooleanSetting("enabled", "Allow autosizing of Internal Windows", "Resize all Internal Windows when the main window resizes"))
							.add(new IntegerSetting("delay", "Delay before resize", "How long to wait until the Internal Windows resize", 50)))
					.add(new ObjectSetting("grid", "Grid Settings", "Configure when Internal Windows should snap to a grid")
					  	.add(new BooleanSetting("enabled", "Allow grid snapping", "Enables/Disable snapping Internal Windows to a grid"))
					  	.add(new IntegerSetting("horizontal", "Horizontal Lines", "Number of horizontal gridlines to snap to"))
					  	.add(new IntegerSetting("vertical", "Vertical Lines", "Number of vertical gridlines to snap to"))
					  	.add(new DoubleSetting("sensitivity", "Sensitivity", "How close the window needs to be to the gridline before it snaps", 10, 0, Double.MAX_VALUE))
					  	.add(new IntegerSetting("delay", "Delay before snap", "How long to wait until the window snaps", 200, 0, Integer.MAX_VALUE))
					  	)
					.add(new BooleanSetting("lock-to-window", "Lock to main window", "Stops InternalWindows from exiting the Main Window"))
					);
		settings.add(new ObjectSetting("simulation", "CPU Simulation")
					.add(new IntegerSetting("clock-speed", "Clock Speed", "Default speed of the simulation clock", 250, 0, Integer.MAX_VALUE))
					.add(new BooleanSetting("zero-memory", "Zero Memory", "Sets whether memory should be zeroed"))
					);
		settings.add(new ObjectSetting("code_editor", "Code Editor")
				.add(new BooleanSetting("word-wrap", "Word Wrapping", "Toggles word wrap"))
				);		
		// @formatter:on

		// Loads all the values from jsonObject
		for (SettingValue<?> setting : settings.getValue()) {
			loadFromJson(jsonObject, setting);
		}
	}

	private void loadFromJson(JsonObject jsonObject, SettingValue<?> setting) {
		JsonElement element = jsonObject.get(setting.getJsonName());

		// If element doesn't exist in json file, ignore it
		if (element == null)
			return;

		switch (setting.getSettingType()) {
			case "Boolean":
				((BooleanSetting) setting).setValue(element.getAsBoolean());
				break;

			case "Double":
				((DoubleSetting) setting).setValue(element.getAsDouble());
				break;

			case "Integer":
				((IntegerSetting) setting).setValue(element.getAsInt());
				break;

			case "Object":
				for (SettingValue<?> s : ((ObjectSetting) setting).getValue())
					loadFromJson(element.getAsJsonObject(), s);
				break;

			case "String":
				((StringSetting) setting).setValue(element.getAsString());
				break;

			default:
				System.err.println("Unknown: " + setting.getSettingType());
		}
	}

	public Object get(String settingPath) {
		String[] path = settingPath.split("\\.");
		SettingValue<?> setting = settings;
		for (int i = 0; i < path.length; i++) {
			 setting = ((ObjectSetting) setting).get(path[i]);
			if (setting == null || (i + 1 < path.length && !(setting instanceof ObjectSetting)))
				return null;
		}
		return setting.getValue();
	}

}
