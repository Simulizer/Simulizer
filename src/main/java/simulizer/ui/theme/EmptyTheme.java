package simulizer.ui.theme;

/**
 * Used when the default theme can not be found
 * 
 * @author Michael
 *
 */
public class EmptyTheme extends Theme {

	@Override
	public String getName() {
		return "MISSING_THEME";
	}

	@Override
	public String getAuthor() {
		return "MISSING_AUTHOR";
	}

	@Override
	public String getDescription() {
		return "MISSING_DESCRIPTION";
	}

	@Override
	public double getVersion() {
		return 1;
	}

	@Override
	public String getStyleSheet(String styleSheet) {
		return "";
	}

	@Override
	public int getOrdering() {
		return Integer.MAX_VALUE;
	}
}
