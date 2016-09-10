package simulizer.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import simulizer.Simulizer;
import simulizer.settings.types.BooleanSetting;
import simulizer.settings.types.DoubleSetting;
import simulizer.settings.types.IntegerSetting;
import simulizer.settings.types.ObjectSetting;
import simulizer.settings.types.StringSetting;
import simulizer.utils.UIUtils;

/**
 * The settings of the application
 * 
 * @author Michael
 *
 */
public class Settings {
	private ObjectSetting settings = new ObjectSetting("settings", "Settings");
	private File json;

	/**
	 * Loads the passed json file into a Settings object
	 * 
	 * @param json
	 *            the json file to load/parse
	 * @return the settings object representing the json file
	 * @throws IOException
	 */
	public static Settings loadSettings(File json) {
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(new FileReader(json));
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			return new Settings(json, jsonObject);
		} catch (IOException e) {
			return new Settings(json, null);
		}
	}

	private Settings(File json, JsonObject jsonObject) {
		this.json = json;
		// Sets up the structure of the settings file
		// @formatter:off
		settings.add(new BooleanSetting("debug", "Debug Menu", "Show debug menu in the Menu Bar", false));
		settings.add(new ObjectSetting("window", "Main Window")
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
					);
		settings.add(new ObjectSetting("internal-window", "Internal Window")
				.add(new BooleanSetting("mouse-borders", "Lock to main window", "Stop internal windows from being lost outside the Main Window", true))
				.add(new ObjectSetting("extractable", "Extractable")
					.add(new BooleanSetting("enabled", "Enable feature", "Allow for internal windows to be extracted from the main window", true))
					.add(new BooleanSetting("menu-bar", "Include menu bar", "Include the main menu bar in each extracted window for easy access", false))
					)
			);
		settings.add(new ObjectSetting("simulation", "CPU Simulation")
						.add(new DoubleSetting("default-CPU-frequency", "Default CPU cycle frequency", "Default number of cycles (runs of fetch+decode+execute) per second (Hz)", 4, 0, Integer.MAX_VALUE))
						.add(new BooleanSetting("zero-memory", "Zero Memory", "Sets whether memory should be zeroed"))
						.add(new BooleanSetting("pipelined", "Use Pipelined CPU", "Sets whether to use the pipelined CPU or not", false))
					);
		settings.add(new ObjectSetting("editor", "Editor")
					.add(new StringSetting("font-family", "Font family", "Font family (optional). Supports all installed monospace fonts, use single quotes for names with spaces. Separate multiple choices with commas", "monospace"))
					.add(new IntegerSetting("font-size", "Font size", "Font size in px", 20, 0, Integer.MAX_VALUE))
					.add(new StringSetting("initial-file", "Initial file", "Path to a file to load at startup (optional)"))
					.add(new DoubleSetting("scroll-speed", "Scroll speed", "Scroll speed", 0.1, 0, Double.MAX_VALUE))
					.add(new BooleanSetting("soft-tabs", "Soft tabs", "Soft tabs", true))
					.add(new StringSetting("theme", "Color theme", "Name of the color scheme to load. Supported: (prefix: /ace/theme/) default, high-viz, monokai, ambiance, chaos, tomorrow_night_eighties, predawn, flatland", "/ace/theme/default"))
					.add(new BooleanSetting("user-control-during-execution", "User control during execution", "Whether the user is allowed to scroll freely during execution of a program", false))
					.add(new BooleanSetting("vim-mode", "Vim mode", "Vim keybindings for the editor", false))
					.add(new BooleanSetting("wrap", "Wrap long lines", "Wrap long lines", false))
					.add(new BooleanSetting("continuous-assembly", "Continuous Assembly", "Repeatedly assemble the program behind the scenes as you type, and highlight problems in the editor", true))
					.add(new IntegerSetting("continuous-assembly-refresh-period", "Continuous Assembly Period", "The time between refreshing the highlighted problems by assembling the program (milliseconds)", 1500, 1, Integer.MAX_VALUE))
					);
		settings.add(new ObjectSetting("splash-screen", "Splash Screen")
					.add(new BooleanSetting("enabled", "Show splash screen", "Toggles whether the splash screen is shown on launch", true))
					.add(new IntegerSetting("delay", "Display Time (in ms)", "Minimum time the splash screen should be shown for", 750, 0, Integer.MAX_VALUE))
					.add(new IntegerSetting("width", "Splash Screen Width", "Width of the splash screen", 676, 0, Integer.MAX_VALUE))
					.add(new IntegerSetting("height", "Splash Screen Height", "Height of the splash screen", 235, 0, Integer.MAX_VALUE))
					);	
		settings.add(new ObjectSetting("logger", "Logger")
				.add(new BooleanSetting("emphasise", "Emphasise Logger", "Toggles whether to emphasise logger when requesting input", true))
				.add(new IntegerSetting("font-size", "Font Size", "Font size for the Program I/O", 15))
				);
		settings.add(new ObjectSetting("hlvis", "High Level Visualiser")
				.add(new BooleanSetting("auto-open", "Automatically Open High Level Visualiser", "Automatically Open High Level Visualiser when a new visualisation is shown", true))
				);
		// @formatter:on

		// Loads all the values from jsonObject (if we have one)
		if (jsonObject != null)
			for (SettingValue<?> setting : settings.getValue()) {
				loadFromJson(jsonObject, setting);
			}
	}

	private void loadFromJson(JsonObject jsonObject, SettingValue<?> setting) {
		JsonElement element = jsonObject.get(setting.getJsonName());

		// If element doesn't exist in json file, ignore it
		if (element == null)
			return;

		// Handle null case
		if (element.isJsonNull()) {
			setting.setValue(null);
			return;
		}
		try {
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
					UIUtils.showErrorDialog("Unknown Setting Type", "" + setting.getSettingType());
			}
		} catch (Exception e) {
			// TODO: Find exact exception
			// Setting was of invalid type, ignoring
		}
	}

	/**
	 * get a loaded value for a setting, specified using a path eg "editor.font-size"
	 * 
	 * @param settingPath
	 *            the path to the option, separated by a dot
	 * @return the requested setting
	 */
	public Object get(String settingPath) {
		String[] path = settingPath.split("\\.");
		SettingValue<?> setting = settings;
		for (int i = 0; i < path.length; i++) {
			setting = ((ObjectSetting) setting).get(path[i]);
			if (setting == null || (i + 1 < path.length && !(setting instanceof ObjectSetting)))
				throw new IllegalArgumentException("Invalid Setting: " + settingPath);
		}
		return setting.getValue();
	}

	/**
	 * @return all of the settings
	 */
	public ObjectSetting getAllSettings() {
		return settings;
	}

	private void saveSetting(JsonObject parent, SettingValue<?> setting) {
		try {
			switch (setting.getSettingType()) {
				case BOOLEAN:
					parent.addProperty(setting.getJsonName(), (Boolean) setting.getValue());
					break;
				case DOUBLE:
					parent.addProperty(setting.getJsonName(), (Double) setting.getValue());
					break;
				case INTEGER:
					parent.addProperty(setting.getJsonName(), (Integer) setting.getValue());
					break;
				case OBJECT:
					JsonObject element = new JsonObject();
					parent.add(setting.getJsonName(), element);
					for (SettingValue<?> s : ((ObjectSetting) setting).getValue())
						saveSetting(element, s);
					break;
				case STRING:
					parent.addProperty(setting.getJsonName(), (String) setting.getValue());
					break;
				default:
					UIUtils.showErrorDialog("Unknown Setting Type", "" + setting.getSettingType());
			}
		} catch (Exception e) {
			// TODO: Find exact exception
			// Setting was of invalid type, ignoring
		}
	}

	/**
	 * Saves settings to the json file
	 */
	public void save() {
		try (Writer writer = new FileWriter(json)) {
			Gson gson = new GsonBuilder().serializeSpecialFloatingPointValues().setPrettyPrinting().disableHtmlEscaping().create();
			JsonObject element = new JsonObject();
			for (SettingValue<?> s : settings.getValue())
				saveSetting(element, s);
			gson.toJson(element, writer);
		} catch (IOException e) {
			Simulizer.handleException(e);
		}
	}

}
