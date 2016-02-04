package simulizer.ui.interfaces;

import simulizer.ui.windows.CPUVisualiser;
import simulizer.ui.windows.CodeEditor;
import simulizer.ui.windows.Logger;
import simulizer.ui.windows.Registers;

public enum WindowEnum {
	CODE_EDITOR("CodeEditor", "Code Editor"), CPU_VISUALISER("CPUVisualiser", "Visualisation"), LOGGER("Logger"), REGISTERS("Registers");

	private String className;
	private String humanReadable;

	private WindowEnum(String className) {
		this.className = className;
		this.humanReadable = className;
	}

	private WindowEnum(String className, String humanReadable) {
		this.className = className;
		this.humanReadable = humanReadable;
	}

	public InternalWindow createNewWindow() {
		switch (this) {
			case CODE_EDITOR:
				return new CodeEditor();
			case CPU_VISUALISER:
				return new CPUVisualiser();
			case LOGGER:
				return new Logger();
			case REGISTERS:
				return new Registers();
		}
		return null;
	}

	public boolean is(InternalWindow w) {
		// Fully Qualified Name
		String fQN = w.getClass().toString().split(" ")[1];
		return ("simulizer.ui.windows." + className).equals(fQN);
	}

	@Override
	public String toString() {
		return humanReadable;
	}
}
