package simulizer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import simulizer.utils.FileUtils;

public class BuildInfo {
	public final String REPO, BRANCH, COMMIT, VERSION_STRING;
	public final double VERSION_NUMBER;
	public final boolean MODIFIED;

	private static BuildInfo instance;

	private BuildInfo() {
		// Defaults when unknown (temp variables)
		String REPO = "Unknown", BRANCH = "Unknown", COMMIT = "Unknown", VERSION = "Unknown";
		Boolean MODIFIED = null;

		// Try to load from build.json
		try {
			JsonObject build = new JsonParser().parse(FileUtils.getResourceContent("/build.json")).getAsJsonObject();
			REPO = build.get("repo").getAsString().replaceAll("\\.git", "");
			BRANCH = build.get("branch").getAsString();
			COMMIT = build.get("commit").getAsString();
			VERSION = build.get("version").getAsString().replaceAll("\\[a-z]", "");
			MODIFIED = build.get("modified").getAsBoolean();
		} catch (Exception e) {
			// Ignore it.
		}

		// Set temp variable to public
		this.REPO = REPO;
		this.BRANCH = BRANCH;
		this.COMMIT = COMMIT;

		// Check whether loading was successful
		if (!REPO.equals("Unknown") && !BRANCH.equals("Unknown") && !COMMIT.equals("Unknown") && MODIFIED != null && !VERSION.equals("Unknown")) {
			// Produce version string from metadata
			this.MODIFIED = MODIFIED;
			VERSION_NUMBER = Double.parseDouble(VERSION.replaceAll("([a-zA-Z\\-])", ""));
			if (REPO.equals("https://github.com/Simulizer/Simulizer") && BRANCH.equals("master") && !MODIFIED)
				VERSION_STRING = VERSION;
			else
				VERSION_STRING = BRANCH + "@" + COMMIT.substring(0, 8) + (MODIFIED ? "-modified" : "");

		} else {
			// Alert that there is no build info
			System.err.println("Unable to read build metadata");
			VERSION_STRING = "Unknown";
			VERSION_NUMBER = Double.NaN;
			this.MODIFIED = false;
		}
	}

	public static BuildInfo getInstance() {
		if (instance == null)
			instance = new BuildInfo();
		return instance;
	}
}
