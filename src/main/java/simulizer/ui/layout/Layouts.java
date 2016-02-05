package simulizer.ui.layout;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class Layouts implements Iterable<Layout> {
	// TODO: Save/Load layout locations
	// TODO: Find a way to scale to window size

	private Path folder;
	private Set<Layout> layouts = new HashSet<Layout>();

	public Layouts(String folder) {
		this.folder = Paths.get(folder);
		reload();
	}

	public void reload() {
		layouts.clear();
		Gson g = new Gson();

		try {
			// Check all files in the layouts folder
			for (Path layout : Files.newDirectoryStream(folder)) {
				// Ignore subfolders
				if (layout.toFile().isFile()) {
					try {
						Layout l = g.fromJson(new JsonReader(Files.newBufferedReader(layout)), Layout.class);
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

	@Override
	public Iterator<Layout> iterator() {
		return layouts.iterator();
	}
}
