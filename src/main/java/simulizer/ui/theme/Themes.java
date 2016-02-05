package simulizer.ui.theme;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Themes {

	private final Path folder;
	private List<Theme> themes = new ArrayList<Theme>();

	public Themes(Path folder) {
		this.folder = folder;
		fetchThemes();
	}

	private void fetchThemes() {
		// Check all folders in the theme folder
		for (File themeFolder : folder.toFile().listFiles()) {
			if (themeFolder.isDirectory()) {
				// Check for a theme.json file
				File[] themeJSONs = themeFolder.listFiles((e) -> e.getName().toLowerCase().equals("theme.json"));
				if (themeJSONs.length == 1) {
					// TODO: Parse the JSON file
					File themeJSON = themeJSONs[0];
					Gson g = new Gson();
					try (InputStream in = Files.newInputStream(themeJSON.toPath()); BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
						Theme t = g.fromJson(new JsonReader(reader), Theme.class);

						// MenuItem folderThemeItem = new MenuItem(t.name + " (" + t.version + ")");
						// folderThemeItem.setOnAction(e -> wm.setTheme("themes/" + themeFolder.getName()));
						// themeMenu.getItems().addAll(folderThemeItem);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
}
