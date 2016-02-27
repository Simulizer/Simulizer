package simulizer.ui.layout;

public class Layout {
	private final String name;
	private final WindowLocation[] windows;

	public Layout(String name, WindowLocation[] windows) {
		this.name = name;
		this.windows = windows;
	}

	public String getName() {
		return name;
	}

	public WindowLocation[] getWindowLocations() {
		return windows;
	}

}
