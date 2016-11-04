package simulizer.ui.layout;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

import simulizer.GuiMode;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.utils.FileUtils;
import simulizer.utils.UIUtils;

/**
 * Represents all the Layouts stored in the layouts folder. Handles converting them between json files and Layout objects
 * 
 * @author Michael
 *
 */
public class Layouts implements Iterable<Layout> {

	private final Path folder;
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

		folder = FileUtils.getPath("layouts");

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
			String layoutName;
			if (GuiMode.args.layout != null) {
				// eg "High Level" --> "high-level.json"
				layoutName = GuiMode.args.layout.toLowerCase().replace(" ", "-") + ".json";
			} else {
				layoutName = (String) workspace.getSettings().get("workspace.layout");
			}

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

		// sort by layout name, with "Default" always first
		layouts = layouts.stream().sorted((l1, l2) -> {
			String n1 = l1.getName();
			String n2 = l2.getName();
			if (n1.equals("Default"))
				return -1;
			else if (n2.equals("Default"))
				return 1;
			else
				return n1.compareTo(n2);
		}).collect(Collectors.toList());

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
			Files.write(FileUtils.getPath(saveFile.toString()), g.toJson(l).getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
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

		WindowLocation[] locations = layout.getWindowLocations();
		List<InternalWindow> layoutWindows = new ArrayList<>();
		List<WindowLocation> needToOpenWindows = new ArrayList<>();

		// Find out what is already open
		for (int i = 0; i < locations.length; i++) {
			InternalWindow window = workspace.findInternalWindow(locations[i].getWindowEnum());
			if (window != null)
				layoutWindows.add(window);
			else
				needToOpenWindows.add(locations[i]);
		}

		// Close any internal windows not in the layout
		workspace.closeAllExcept(layoutWindows);

		// Open the rest of the layout windows
		needToOpenWindows.forEach(l -> layoutWindows.add(workspace.openInternalWindow(l.getWindowEnum())));
		needToOpenWindows.clear();

		// Update all InternalWindow dimensions
		layoutWindows.forEach(window -> {
			// Ensure the window is not extracted
			if (window.isExtracted())
				window.toggleWindowExtracted();
			
			// Update internal window dimensions
			setWindowDimensions(window);
		});

	}

	/**
	 * Sets the passed InternalWindow to the dimensions defined in the selected layout
	 * 
	 * @param w
	 *            the InternalWindow to set the dimensions for
	 */
	public void setWindowDimensions(InternalWindow w) {
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

		// If there are no bounds set in the layout, use default
		w.setToDefaultDimensions();
		if (width > 0 && height > 0) {
			w.setWorkspaceSize(workspace.getWidth(), workspace.getHeight());
		}
	}

	@Override
	public Iterator<Layout> iterator() {
		return layouts.iterator();
	}
}
