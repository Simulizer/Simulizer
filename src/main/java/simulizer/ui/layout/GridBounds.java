package simulizer.ui.layout;

import java.util.Arrays;

public class GridBounds {
	private final int hor, ver, timeout; // Number of Horizontal and Vertical Lines
	private double xGap, yGap; // Size of the Main Window
	private double sens;
	private boolean gridSnap = false;

	/**
	 * @param horizontal
	 *            Number of horizontal lines in the grid
	 * @param verical
	 *            Number of vertical lines in the grid
	 * @param sensitivity
	 *            How close an Internal Window needs to be to a GridLine to snap
	 * @param timeout
	 *            The time the window needs to have stayed still until it will snap into place
	 */
	public GridBounds(int horizontal, int verical, double sensitivity, int timeout) {
		this.hor = horizontal;
		this.ver = verical;
		this.sens = sensitivity;
		this.timeout = timeout;
	}

	/**
	 * Sets the Main Window size
	 * 
	 * @param width
	 *            the width of the main window
	 * @param height
	 *            the height of the main window
	 */
	public void setWindowSize(double width, double height) {
		xGap = width / hor;
		yGap = height / ver;
		System.out.println("xGap: " + xGap + ", yGap: " + yGap);
	}

	/**
	 * Converts a list of coordinates for an InternalWindow so that they line up with the grid
	 * 
	 * @param window
	 *            The list of coordinates in the order layoutX, layoutY, layoutX + width, layoutY + height
	 * @return Converted points
	 */
	public double[] moveToGrid(double[] window, boolean resize) {
		double[] windowAdjusted = Arrays.copyOf(window, window.length);
		if (gridSnap) {
			System.out.println("BEGINNING: " + window[0] + " " + window[1] + " " + window[2] + " " + window[3]);
			if (window.length != 4)
				throw new IllegalArgumentException();
			windowAdjusted[0] = moveIfSens(window[0], xGap);
			windowAdjusted[1] = moveIfSens(window[1], yGap);
			windowAdjusted[2] = moveIfSens(window[2], xGap);
			windowAdjusted[3] = moveIfSens(window[3], yGap);

			System.out.println("MIDDLE: " + windowAdjusted[0] + " " + windowAdjusted[1] + " " + windowAdjusted[2] + " " + windowAdjusted[3]);
			if (!resize) {
				double width = window[2] - window[0], height = window[3] - window[1];
				// Find closest gridline
				if (windowAdjusted[2] == window[2] ||  Math.abs(windowAdjusted[2] - window[2]) <= Math.abs(windowAdjusted[0] - window[0])) {
					windowAdjusted[2] = windowAdjusted[0] + width;
				} else {
					windowAdjusted[0] = windowAdjusted[2] - width;
				}
				if (windowAdjusted[3] == window[3] || Math.abs(windowAdjusted[3] - window[3]) <= Math.abs(windowAdjusted[1] - window[1])) {
					windowAdjusted[3] = windowAdjusted[1] + height;
				} else {
					windowAdjusted[1] = windowAdjusted[3] - height;
				}
			}

			System.out.println("END: " + windowAdjusted[0] + " " + windowAdjusted[1] + " " + windowAdjusted[2] + " " + windowAdjusted[3] + "\n");
		}
		return windowAdjusted;
	}

	private double moveIfSens(double coord, double gap) {
		double mod = coord % gap;
		if (Math.abs(mod) <= sens) {
			return coord - mod;
		} else if (gap - Math.abs(mod) <= sens) {
			if (mod >= 0) {
				return coord + (gap - mod);
			} else {
				return coord - (gap + mod);
			}
		}
		return coord;
	}

	/**
	 * Sets whether InternalWindows should snap to the grid
	 * 
	 * @param value
	 */
	public void setGridSnap(boolean value) {
		gridSnap = value;
	}

	public long getTimeout() {
		return timeout;
	}
}
