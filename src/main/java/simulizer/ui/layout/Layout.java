package simulizer.ui.layout;

import java.util.Iterator;

public class Layout implements Iterable<WindowLocation> {
	private final String id, name;
	private final double width, height;
	private final WindowLocation[] windows;

	private int index = 0;

	public Layout(String id, String name, double width, double height, WindowLocation[] windows) {
		this.id = id;
		this.name = name;
		this.width = width;
		this.height = height;
		this.windows = windows;
	}

	@Override
	public Iterator<WindowLocation> iterator() {
		index = 0;
		return new Iterator<WindowLocation>() {

			@Override
			public boolean hasNext() {
				return index < windows.length;
			}

			@Override
			public WindowLocation next() {
				WindowLocation out = windows[index];
				index++;
				return out;
			}

		};
	}

	public String getName() {
		return name;
	}

	public String getId() {
		return id;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
