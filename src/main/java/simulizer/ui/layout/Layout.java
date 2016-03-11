package simulizer.ui.layout;

/**
 * Stores the WindowLocations of a layout
 * 
 * @author Michael
 *
 */
public class Layout {
	private final String name;
	private final WindowLocation[] windows;

	/**
	 * Creates a new Layout
	 * 
	 * @param name
	 *            the name of the layout
	 * @param windows
	 *            all the window locations
	 */
	public Layout(String name, WindowLocation[] windows) {
		this.name = name;
		this.windows = windows;
	}

	/**
	 * @return the layout name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the window locations
	 */
	public WindowLocation[] getWindowLocations() {
		return windows;
	}

}
