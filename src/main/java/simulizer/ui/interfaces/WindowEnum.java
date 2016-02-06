package simulizer.ui.interfaces;

public enum WindowEnum {
	CODE_EDITOR("CodeEditor", "Code Editor"), CPU_VISUALISATION("CPUVisualisation",
			"CPU Visualisation"), LOGGER("Logger"), REGISTERS("Registers"), HIGH_LEVEL_VISUALISATION("HighLevelVisualisation", "High Level Visualisation");

	private final String pkg = "simulizer.ui.windows.";
	private final String className;
	private final String defaultTitle;

	private WindowEnum(String className) {
		this.className = className;
		this.defaultTitle = className;
	}

	private WindowEnum(String className, String defaultTitle) {
		this.className = className;
		this.defaultTitle = defaultTitle;
	}

	public InternalWindow createNewWindow() {
		try {
			return (InternalWindow) Class.forName(pkg + className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean equals(InternalWindow w) {
		// Fully Qualified Name
		String fQN = w.getClass().toString().split(" ")[1];
		return (pkg + className).equals(fQN);
	}

	public static WindowEnum toEnum(InternalWindow w) {
		for (WindowEnum we : WindowEnum.values())
			if (we.equals(w))
				return we;
		return null;
	}

	@Override
	public String toString() {
		return defaultTitle;
	}
}