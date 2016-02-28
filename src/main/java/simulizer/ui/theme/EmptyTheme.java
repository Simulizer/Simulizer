package simulizer.ui.theme;

public class EmptyTheme extends Theme {

	public String getName() {
		return "MISSING_THEME";
	}

	public String getAuthor() {
		return "MISSING_AUTHOR";
	}

	public String getDescription() {
		return "MISSING_DESCRIPTION";
	}

	public double getVersion() {
		return 1;
	}

	public String getStyleSheet(String styleSheet) {
		return "";
	}

	public int getOrdering() {
		return Integer.MAX_VALUE;
	}
}
