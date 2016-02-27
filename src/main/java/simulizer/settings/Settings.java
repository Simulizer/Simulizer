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
		settings.add(new ObjectSetting("window", "Window")
				.add(new IntegerSetting("width", "Width", "Default window width", 1024, 300, Integer.MAX_VALUE))
				.add(new IntegerSetting("height", "Height", "Default window height", 705, 300, Integer.MAX_VALUE))
			);
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
		settings.add(new ObjectSetting("splash-screen", "Splash Screen")
					.add(new BooleanSetting("enabled", "Show splash screen", "Toggles whether the splash screen is shown on launch", true))
					.add(new IntegerSetting("delay", "Display Time", "Minimum time the splash screen should be shown for", 750, 0, Integer.MAX_VALUE))
					.add(new IntegerSetting("width", "Splash Screen Width", "Width of the splash screen", 676, 0, Integer.MAX_VALUE))
					.add(new IntegerSetting("height", "Splash Screen Height", "Height of the splash screen", 235, 0, Integer.MAX_VALUE))
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
		if (element == null) return;

		switch (setting.getSettingType()) {
			case BOOLEAN:
				((BooleanSetting) setting).setValue(element.getAsBoolean());
				break;

			case DOUBLE:
				((DoubleSetting) setting).setValue(element.getAsDouble());
				break;

			case INTEGER:
				((IntegerSetting) setting).setValue(element.getAsInt());
				break;

			case OBJECT:
				for (SettingValue<?> s : ((ObjectSetting) setting).getValue())
					loadFromJson(element.getAsJsonObject(), s);
				break;

			case STRING:
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
			if (setting == null || (i + 1 < path.length && !(setting instanceof ObjectSetting))) return null;
		}
		return setting.getValue();
	}

	public ObjectSetting getAllSettings() {
		return settings;
	}

}
