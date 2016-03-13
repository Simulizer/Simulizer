package simulizer.ui.theme;

/**
 * Contains the meta data for a theme
 * 
 * @author Michael
 *
 */
@SuppressWarnings("unused")
public class Theme implements Comparable<Theme> {
	private String name, author, description;
	private double version;
	protected String location;
	protected Themes themes;
	private int ordering = Integer.MAX_VALUE;

	/**
	 * @return the name of the theme
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the theme description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the theme version
	 */
	public double getVersion() {
		return version;
	}

	/**
	 * Gets the stylesheet from the current theme
	 * 
	 * @param styleSheet
	 *            the stylesheet to get
	 * @return the path of the stylesheet
	 */
	public String getStyleSheet(String styleSheet) {
		// TODO: Check StyleSheet exists for theme -> Use default if it does not exist
		return location + styleSheet;
	}

	/**
	 * @return the position the theme should be in
	 */
	public int getOrdering() {
		return ordering;
	}

	@Override
	public int compareTo(Theme o) {
		int diff = ordering - o.getOrdering();
		if (diff != 0)
			return diff;
		else
			return location.compareTo(o.location);
	}
}
