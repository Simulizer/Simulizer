package simulizer.simulation.cpu.user_interaction;

import java.util.Observable;

import javafx.util.Pair;
import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Logger;

public class LoggerIO extends Observable implements IO {
	private final Workspace workspace;

	public LoggerIO(Workspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public String readString(IOStream stream) {
		String in = requestInput(stream);
		return in == null ? "" : in;
	}

	@Override
	public int readInt(IOStream stream) {
		try {
			String in = requestInput(stream);
			return in == null || in.isEmpty() ? 0 : Integer.parseInt(in);
		} catch (NumberFormatException e) {
			printString(IOStream.ERROR, e.getClass().getName() + ":\n\t" + e.getMessage() + "\n");
		}
		return 0;
	}

	@Override
	public char readChar(IOStream stream) {
		String in = requestInput(stream);
		return in == null || in.isEmpty() ? '\0' : in.charAt(0);
	}

	@Override
	public void cancelRead() {
		Logger logger = (Logger) workspace.findInternalWindow(WindowEnum.LOGGER);
		if (logger != null)
			logger.cancelNextMessage();
	}

	@Override
	public void printString(IOStream stream, String str) {
		setChanged();
		notifyObservers(new Pair<>(stream, str));
	}

	@Override
	public void printInt(IOStream stream, int num) {
		setChanged();
		notifyObservers(new Pair<>(stream, "" + num));
	}

	@Override
	public void printChar(IOStream stream, char letter) {
		setChanged();
		notifyObservers(new Pair<>(stream, "" + letter));
	}

	private String requestInput(IOStream stream) {
		Logger logger = (Logger) workspace.openInternalWindow(WindowEnum.LOGGER);
		return logger.nextMessage(stream);
	}

	public void clear() {
		Logger logger = (Logger) workspace.findInternalWindow(WindowEnum.LOGGER);
		if (logger != null)
			logger.clear();
	}

}
