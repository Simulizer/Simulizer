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

import simulizer.ui.components.Workspace;
import javafx.application.Platform;
import simulizer.ui.WindowManager;
import simulizer.ui.interfaces.InternalWindow;

public class Layouts implements Iterable<Layout> {

	private final Path folder = Paths.get("layouts");
	private Set<Layout> layouts = new HashSet<Layout>();
	private Layout layout, defaultLayout;
	private Workspace workspace;

	public Layouts(Workspace workspace) {
		this.workspace = workspace;

		reload(true);
	}

	public void reload(boolean findDefault) {
		layouts.clear();
		Gson g = new Gson();

		try {
			String layoutName = (String)workspace.getSettings().get("workspace.layout");

			// Check all files in the layouts folder
			for (Path layout : Files.newDirectoryStream(folder)) {
				// Ignore subfolders
				if (layout.toFile().isFile()) {
					try {
						Layout l = g.fromJson(new JsonReader(Files.newBufferedReader(layout)), Layout.class);
						if (layout.toFile().getName().equals(layoutName))
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
		Platform.runLater(() -> setLayout(defaultLayout));
	}

	public void saveLayout(File saveFile) {
		Layout l = workspace.generateLayout(saveFile.getName());
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
			newOpenWindows[i] = workspace.openInternalWindow(locations[i].getWindowEnum());
			setWindowDimentions(newOpenWindows[i]);
		}

		workspace.closeAllExcept(newOpenWindows);

	}

	public void setWindowDimentions(InternalWindow w) {
		WindowLocation[] wl = layout.getWindowLocations();
		for (int i = 0; i < wl.length; i++) {
			if (wl[i].getWindowEnum().equals(w)) {
				// Resize window to layout dimensions
				w.setWorkspaceSize(layout.getWidth(), layout.getHeight());
				w.setBoundsWithoutResize(wl[i].getX(), wl[i].getY(), wl[i].getWidth(), wl[i].getHeight());
				double width = workspace.getWidth(), height = workspace.getHeight();
				if (width > 0 && height > 0)
					w.setWorkspaceSize(workspace.getWidth(), workspace.getHeight());
				return;
			}
		}

		// If there are no bounds set in the layout, use this default
		w.setBoundsWithoutResize(0, 0, workspace.getWidth(), workspace.getHeight());
	}

	@Override
	public Iterator<Layout> iterator() {
		return layouts.iterator();
	}
}
