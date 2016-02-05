package simulizer.ui.theme;

public class Theme {
	private final String name, author, description;
	private final double version;

	public Theme(String name, String author, double version, String description) {
		this.name = name;
		this.author = author;
		this.version = version;
		this.description = description;
	}

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
}
