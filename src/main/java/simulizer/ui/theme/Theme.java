package simulizer.ui.theme;

public class Theme implements Comparable<Theme> {
	private String name, author, description;
	private double version;
	protected String location;
	protected Themes themes;
	private int ordering = Integer.MAX_VALUE;

	public String getName() {
		return name;
	}

	public String getAuthor() {
		return author;
	}

	public String getDescription() {
		return description;
	}

	public double getVersion() {
		return version;
	}

	public String getStyleSheet(String styleSheet) {
		// TODO: Check StyleSheet exists for theme -> Use default if it does not exist
		return location + styleSheet;
	}

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
