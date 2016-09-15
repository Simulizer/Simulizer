package simulizer.simulation.cpu.user_interaction;

import java.util.Observable;

import simulizer.ui.components.Workspace;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.windows.Logger;

/**
 * Implementation of the IO interface for use in the Logger window
 * 
 * @author Michael
 *
 */
public class LoggerIO extends Observable implements IO {
	private final Workspace workspace;
	private final StringBuilder[] logs; // the output streams

	public LoggerIO(Workspace workspace) {
		this.workspace = workspace;
		logs = new StringBuilder[IOStream.values().length];
		for (int i = 0; i < IOStream.values().length; i++)
			logs[i] = new StringBuilder();

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
		logs[stream.getID()].append(str);
		setChanged();
		notifyObservers(stream);
	}

	@Override
	public void printInt(IOStream stream, int num) {
		logs[stream.getID()].append(num);
		setChanged();
		notifyObservers(stream);
	}

	@Override
	public void printChar(IOStream stream, char letter) {
		logs[stream.getID()].append(letter);
		setChanged();
		notifyObservers(stream);
	}

	/**
	 * Requests an input message (will wait until input is given)
	 * 
	 * @param stream
	 *            the stream to request on
	 * @return the input message
	 */
	private String requestInput(IOStream stream) {
		// Open the logger window
		Logger logger = (Logger) workspace.openInternalWindow(WindowEnum.LOGGER);

		// Get the input
		String input = logger.nextMessage(stream);

		// Notify observers of change
		if (input != null) {
			logs[stream.getID()].append(input + "\n");
			setChanged();
			notifyObservers(stream);
		}

		return input;
	}

	/**
	 * Clears all the logs
	 */
	public void clear() {
		for (IOStream i : IOStream.values()) {
			logs[i.getID()] = new StringBuilder();
			setChanged();
			notifyObservers(i);
		}
	}

	/**
	 * Gets the contents of the log for an IOStream
	 * 
	 * @param stream
	 *            the IOStream to get
	 * @return the log history
	 */
	public String getLog(IOStream stream) {
		return logs[stream.getID()].toString();
	}

}
