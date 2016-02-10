package simulizer.ui;

import java.util.Observable;

public class GridBounds extends Observable {

	private final int hor, ver; // Number of Horizontal and Vertical Lines
	private double xGap, yGap; // Size of the Main Window
	private static double minSens = 0; // To account for errors in division
	private double sens;

	/** @param horizontal Number of horizontal lines in the grid
	 * @param verical Number of vertical lines in the grid
	 * @param sensitivity How close an Internal Window needs to be to a GridLine to snap */
	public GridBounds(int horizontal, int verical, double sensitivity) {
		this.hor = horizontal;
		this.ver = verical;
		this.sens = sensitivity;
	}

	/** Sets the Main Window size
	 * @param width the width of the main window
	 * @param height the height of the main window */
	public void setWindowSize(double width, double height) {
		xGap = width / hor;
		yGap = height / ver;
		System.out.println(xGap + " " + yGap);
		setChanged();
		notifyObservers();
	}

	/** Converts a list of coordinates for an InternalWindow so that they line up with the grid
	 * @param window The list of coordinates in the order layoutX, layoutY, layoutX + width, layoutY + height
	 * @return Converted points */
	public double[] moveToGrid(double[] window) {
		System.out.println("BEFORE: " + window[0] + " " + window[1] + " " + window[2] + " " + window[3]);
		if (window.length != 4) throw new IllegalArgumentException();
		window[0] = moveIfSens(window[0], xGap);
		window[1] = moveIfSens(window[1], yGap);
		window[2] = moveIfSens(window[2], xGap);
		window[3] = moveIfSens(window[3], yGap);
		System.out.println("AFTER: " + window[0] + " " + window[1] + " " + window[2] + " " + window[3] + "\n");
		return window;
	}

	private double moveIfSens(double coord, double gap) {
		double mod = coord % gap;
		if (mod > minSens) {
			if (mod <= sens) return coord - mod;
			else if (mod >= gap - sens) return coord + (gap - mod);
		}
		return coord;
	}
}
