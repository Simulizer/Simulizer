package simulizer.ui.layout;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.utils.UIUtils;

/**
 * Represents all the Layouts stored in the layouts folder. Handles converting them between json files and Layout objects
 * 
 * @author Michael
 *
 */
public class Layouts implements Iterable<Layout> {

	private final Path folder = Paths.get("layouts");
	private List<Layout> layouts = new ArrayList<>();
	private Layout layout, defaultLayout;
	private Workspace workspace;

	/**
	 * @param workspace
	 *            the workspace to apply the layouts to
	 * @throws IOException
	 */
	public Layouts(Workspace workspace) throws IOException {
		this.workspace = workspace;

		// Check layouts folder exists
		if (!Files.exists(folder))
			throw new IOException("layouts folder is missing");

		reload(true);
	}

	/**
	 * Refreshes the list of layouts
	 * 
	 * @param findDefault
	 *            whether to automatically load the default layout
	 */
	public void reload(boolean findDefault) {
		layouts.clear();
		Gson g = new Gson();

		try {
			String layoutName = (String) workspace.getSettings().get("workspace.layout");

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
						UIUtils.showErrorDialog("Invalid File", "Invalid File: " + layout.toUri().toString());
					}
				}
			}
		} catch (IOException e) {
			UIUtils.showExceptionDialog(e);
		}

		// sort by layout name
		layouts = layouts.stream().sorted((l1, l2) -> l1.getName().compareTo(l2.getName())).collect(Collectors.toList());

		if (defaultLayout == null)
			defaultLayout = new Layout("MISSING_LAYOUT", new WindowLocation[0]);

		if (layout == null)
			layout = defaultLayout;
	}

	/**
	 * Sets the current layout to the default
	 */
	public void setDefaultLayout() {
		setLayout(defaultLayout);
	}

	/**
	 * Saves the current state of the workspace as a layout
	 * 
	 * @param saveFile
	 *            the file to save to
	 */
	public void saveLayout(File saveFile) {
		Layout l = workspace.generateLayout(saveFile.getName());
		Gson g = new GsonBuilder().setPrettyPrinting().create();
		try {
			// Thanks to: http://stackoverflow.com/questions/7366266/best-way-to-write-string-to-file-using-java-nio#answer-21982658
			Files.write(Paths.get(saveFile.toURI()), g.toJson(l).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			UIUtils.showErrorDialog("Error Saving Layout", "Unable to save the file");
		}
	}

	/**
	 * Restores the state of the workspace to the passed layout
	 * 
	 * @param layout
	 *            the layout to set the workspace as
	 */
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

	/**
	 * Sets the passed InternalWindow to the dimensions defined in the selected layout
	 * 
	 * @param w the InternalWindow to set the dimensions for
	 */
	public void setWindowDimentions(InternalWindow w) {
		double width = workspace.getWidth(), height = workspace.getHeight();

		WindowLocation[] wl = layout.getWindowLocations();
		for (WindowLocation loc : wl) {
			if (loc.getWindowEnum().equals(w)) {
				// Resize window to layout dimensions
				w.setNormalisedDimentions(loc.getX(), loc.getY(), loc.getWidth(), loc.getHeight());
				if (width > 0 && height > 0) {
					w.setWorkspaceSize(workspace.getWidth(), workspace.getHeight());
				}
				return;
			}
		}

		// If there are no bounds set in the layout, use this default
		w.setNormalisedDimentions(0.1, 0.1, 0.8, 0.8);
		if (width > 0 && height > 0) {
			w.setWorkspaceSize(workspace.getWidth(), workspace.getHeight());
		}
	}

	@Override
	public Iterator<Layout> iterator() {
		return layouts.iterator();
	}
}
