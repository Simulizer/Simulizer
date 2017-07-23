package simulizer.ui.theme;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

/**
 * Represents all the themes stored in the themes folder. Handles the converting between the file system and Theme objects
 * 
 * @author Michael
 *
 */
public class Themes implements Iterable<Theme> {
	private final String defaultTheme;
	private final Path folder;
	private SortedSet<Theme> themes = new TreeSet<>();
	private Set<Themeable> themeables = new HashSet<>();
	private Theme theme = null;

	/**
	 * @param defaultTheme
	 *            the default theme to load
	 * @throws IOException
	 */
	public Themes(String defaultTheme) throws IOException {
		this.defaultTheme = defaultTheme;

		folder = FileUtils.getPath("themes");

		// Check themes folder exists and is a directory
		if (!Files.isDirectory(folder))
			throw new IOException("themes folder is missing");

		reload();
	}

	/**
	 * Refreshes the list of themes
	 */
	public void reload() {
		themes.clear();
		Gson g = new Gson();


		try {
			File[] fs = folder.toFile().listFiles();
			assert(fs != null);

			// Check all folders in the theme folder
			for (File themeFolder : fs) {
				if (themeFolder.isDirectory()) {
					// Check for a theme.json file
					File[] themeJSONs = themeFolder.listFiles((e) -> e.getName().toLowerCase().equals("theme.json"));
                    assert(themeJSONs != null);
					if (themeJSONs.length == 1) {
						File themeJSON = themeJSONs[0];
                        Reader reader = FileUtils.getUTF8FileReader(themeJSON);
						Theme t = g.fromJson(new JsonReader(reader), Theme.class);
						t.location = themeFolder.toURI().toString();
						t.themes = this;
						// @formatter:off
						// Selects the theme to start with (either default, or last selected)
						if ((theme == null && themeFolder.getName().equals(defaultTheme)) ||
								(theme != null && t.getName().equals(theme.getName())))
							theme = t;
						// @formatter:on
						themes.add(t);
					}
				}
			}
		} catch (Exception e) {
			UIUtils.showExceptionDialog(e);
		}

		if (theme == null) {
			theme = new EmptyTheme();
		}
	}

	@Override
	public Iterator<Theme> iterator() {
		return themes.iterator();
	}

	/**
	 * @return the current theme
	 */
	public Theme getTheme() {
		return theme;
	}

	/**
	 * Sets the theme to the passed theme
	 * 
	 * @param theme
	 *            the theme to set all themeable components to
	 */
	public void setTheme(Theme theme) {
		this.theme = theme;

		// Update all themeable elements
		for (Themeable themeable : themeables)
			themeable.setTheme(theme);
	}

	/**
	 * Sets the theme to the passed theme
	 * 
	 * @param theme
	 *            the theme to set all themeable components to
	 */
	public void setTheme(String theme) {
		for (Theme t : themes) {
			if (t.getName().equals(theme)) {
				setTheme(t);
				return;
			}
		}
	}

	/**
	 * Adds a theme change listener
	 * 
	 * @param t
	 *            the themeable component
	 */
	public void addThemeableElement(Themeable t) {
		themeables.add(t);
	}

	/**
	 * Removes a themeable listener
	 * 
	 * @param t
	 *            the themeable component
	 */
	public void removeThemeableElement(Themeable t) {
		themeables.remove(t);
	}
	
	public void resetTheme() {
		setTheme(getTheme());
	}
}
