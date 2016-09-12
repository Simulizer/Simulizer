package simulizer.ui.interfaces;

import javafx.application.Platform;
import javafx.concurrent.Task;
import simulizer.utils.UIUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Contains all possible InternalWindows
 *
 * @author Michael
 *
 */
public enum WindowEnum {
	// @formatter:off
	EDITOR("Editor", "Editor"),
	CPU_VISUALISATION("CPUVisualisation", "CPU Visualisation"),
	HIGH_LEVEL_VISUALISATION("HighLevelVisualisation", "High Level Visualisation"),
	LABELS("Labels"),
	LOGGER("Logger", "Program I/O"),
	PIPELINE_VIEW("PipelineView", "Pipeline View"),
	MEMORY_VIEW("MemoryView", "Memory View"),
	REGISTERS("Registers"),
	OPTIONS("Options", "Options", false),
	SYSCALL_REFERENCE("help.SyscallReference", "Syscall Reference", false),
	REGISTER_REFERENCE("help.RegisterReference", "Register Reference", false),
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

	/**
	 * @return A new instance of the InternalWindow
	 */
	public InternalWindow createNewWindow() {
		try {
			return UIUtils.runLaterWithResult(() -> (InternalWindow) Class.forName(pkg + className).newInstance());
		} catch (Exception e) {
            UIUtils.showExceptionDialog(e);
		}
		return null;
	}

	/**
	 * Returns true if the current enum represents the passed InternalWindow
	 *
	 * @param w
	 *            the InternalWindow to compare to
	 * @return whether the enum represents the InternalWindow
	 */
	public boolean equals(InternalWindow w) {
		// Fully Qualified Name
		String fQN = w.getClass().toString().split(" ")[1];
		return (pkg + className).equals(fQN);
	}

	/**
	 * Converts the InternalWindow to an enum
	 *
	 * @param w
	 *            The InternalWindow to convert
	 * @return the enum
	 */
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

	/**
	 * @return whether the current enum should be part of the Windows MainMenuBar
	 */
	public boolean showInWindowsMenu() {
		return showInWindowsMenu;
	}

	@Override
	public String toString() {
		return defaultTitle;
	}

}
