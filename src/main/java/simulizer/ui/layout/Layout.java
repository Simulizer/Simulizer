package simulizer.ui.layout;

import java.util.Iterator;

public class Layout implements Iterable<WindowLocation> {
	private final String name;
	private final double width, height;
	private final WindowLocation[] windows;

	public Layout(String name, double width, double height, WindowLocation[] windows) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.windows = windows;
	}

	@Override
	public Iterator<WindowLocation> iterator() {
		return new Iterator<WindowLocation>() {
			private int index = 0;

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

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
