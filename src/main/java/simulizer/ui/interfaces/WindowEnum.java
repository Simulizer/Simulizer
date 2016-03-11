package simulizer.ui.interfaces;

import simulizer.utils.UIUtils;

public enum WindowEnum {
	// @formatter:off
	EDITOR("Editor", "Editor"),
	CPU_VISUALISATION("CPUVisualisation", "CPU Visualisation"),
	OPTIONS("Options"),
	HIGH_LEVEL_VISUALISATION("HighLevelVisualisation", "High Level Visualisation"),
	LABELS("Labels"),
	LOGGER("Logger"),
	MEMORY_VIEW("MemoryView", "Memory View"),
	REGISTERS("Registers"),
	GUIDE("help.GuideWindow", "Guide", false),
	SYSCALL_REFERENCE("help.SyscallReference", "Syscall Reference", false),
	INSTRUCTION_REFERENCE("help.InstructionReference", "Instruction Reference", false);
	// @formatter:on

	private final String pkg = "simulizer.ui.windows.", className, defaultTitle;
	private final boolean showInWindowsMenu;

	WindowEnum(String className) {
		this(className, className, true);
	}

	WindowEnum(String className, String defaultTitle) {
		this(className, defaultTitle, true);
	}

	WindowEnum(String className, String defaultTitle, boolean showInWindowsMenu) {
		this.className = className;
		this.defaultTitle = defaultTitle;
		this.showInWindowsMenu = showInWindowsMenu;
	}

	public InternalWindow createNewWindow() {
		try {
			return (InternalWindow) Class.forName(pkg + className).newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			UIUtils.showExceptionDialog(e);
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

	public static WindowEnum ofString(String name) {
		for (WindowEnum we : WindowEnum.values())
			if (we.className.equals(name))
				return we;
		return null;
	}

	public static String getName(InternalWindow w) {
		WindowEnum e = toEnum(w);
		if (e != null) {
			return e.toString();
		} else {
			return "Unknown Window";
		}
	}

	public boolean showInWindowsMenu() {
		return showInWindowsMenu;
	}

	@Override
	public String toString() {
		return defaultTitle;
	}

}
