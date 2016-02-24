package simulizer.ui.layout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;

public class Layouts implements Iterable<Layout> {
	// TODO: Find a way to scale to window size

	private final Path folder = Paths.get("layouts");
	private Set<Layout> layouts = new HashSet<Layout>();
	private Layout layout;
	private WindowManager wm;

	private static final String DEFAULT_LAYOUT = "default.json";
	private Layout defaultLayout = null;

	public Layouts(WindowManager wm) {
		this.wm = wm;
		reload(true);
	}

	public void reload(boolean findDefault) {
		layouts.clear();
		Gson g = new Gson();

		try {
			// Check all files in the layouts folder
			for (Path layout : Files.newDirectoryStream(folder)) {
				// Ignore subfolders
				if (layout.toFile().isFile()) {
					try {
						Layout l = g.fromJson(new JsonReader(Files.newBufferedReader(layout)), Layout.class);
						if (layout.toFile().getName().equals(DEFAULT_LAYOUT))
							defaultLayout = l;
						layouts.add(l);
					} catch (JsonIOException | JsonSyntaxException | IOException e) {
						System.out.println("Invalid File: " + layout.toUri().toString());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setDefaultLayout() {
		setLayout(defaultLayout);
	}

	public void saveLayout(File saveFile) {
		Layout l = wm.getWorkspace().generateLayout(saveFile.getName());
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		try {
			// Thanks to: http://stackoverflow.com/questions/7366266/best-way-to-write-string-to-file-using-java-nio#answer-21982658
			Files.write(Paths.get(saveFile.toURI()), g.toJson(l).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			System.err.println("Unable to save file");
		}
	}

	public void setLayout(Layout layout) {
		this.layout = layout;

		// For each new window
		WindowLocation[] locations = layout.getWindowLocations();
		InternalWindow[] newOpenWindows = new InternalWindow[locations.length];
		for (int i = 0; i < locations.length; i++) {
			newOpenWindows[i] = wm.getWorkspace().openInternalWindow(locations[i].getWindowEnum());
		}

		wm.getWorkspace().closeAllExcept(newOpenWindows);

	}

	public void setWindowDimentions(InternalWindow w) {
		WindowLocation[] wl = layout.getWindowLocations();
		for (int i = 0; i < wl.length; i++) {
			if (wl[i].getWindowEnum().equals(w)) {
				// Resize window to layout dimentions
				/*
				 * GridBounds b = new GridBounds(wl[i].getWidth(), wl[i].getHeight());
				 * w.setGridBounds(b);
				 * b.setWindowSize(wm.getPane().getWidth(), wm.getPane().getHeight());
				 */
				return;
			}
		}

		// If there are no bounds set in the layout, use this default
		w.setBoundsWithoutResize(10, 35, wm.getWorkspace().getWidth(), wm.getWorkspace().getHeight());
	}

	@Override
	public Iterator<Layout> iterator() {
		return layouts.iterator();
	}
}
