package simulizer.ui.layout;

public class Layout {
	private final String name;
	private final double width, height;
	private final WindowLocation[] windows;

	public Layout(String name, double width, double height, WindowLocation[] windows) {
		this.name = name;
		this.width = width;
		this.height = height;
		this.windows = windows;
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

	public WindowLocation[] getWindowLocations() {
		return windows;
	}

}
