package simulizer.simulation.cpu.user_interaction;

import java.util.Observable;
import java.util.concurrent.CountDownLatch;

import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Logger;

public class LoggerIO extends Observable implements IO {
	private final Workspace workspace;

	public LoggerIO(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public String readString() {
		return requestInput();
	}

	@Override
	public int readInt() {
		try {
			return Integer.parseInt(requestInput());
		} catch (NumberFormatException e) {
			System.err.println(e.getMessage());
		}
		return 0;
	}

	@Override
	public char readChar() {
		return requestInput().charAt(0);
	}

	@Override
	public void printString(String str) {
		setChanged();
		notifyObservers(str);
	}

	@Override
	public void printInt(int num) {
		setChanged();
		notifyObservers("" + num);
	}

	@Override
	public void printChar(char letter) {
		setChanged();
		notifyObservers("" + letter);
	}

	private String requestInput() {
		Logger logger = (Logger) workspace.openInternalWindow(WindowEnum.LOGGER);
		return logger.nextMessage();
	}

	public void clear() {
		Logger logger = (Logger) workspace.findInternalWindow(WindowEnum.LOGGER);
		if (logger != null)
			logger.clear();
	}

}
