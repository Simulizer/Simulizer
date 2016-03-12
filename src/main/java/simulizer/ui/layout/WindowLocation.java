package simulizer.ui.layout;

import simulizer.ui.interfaces.WindowEnum;

/**
 * Contains the normalised InternalWindow dimensions
 * 
 * @author Michael
 *
 */
public class WindowLocation {

	private final WindowEnum id;
	private final double x, y, width, height;

	public WindowLocation(WindowEnum id, double x, double y, double width, double height) {
		this.id = id;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}

	/**
	 * @return the InternalWindow enum
	 */
	public WindowEnum getWindowEnum() {
		return id;
	}

	/**
	 * @return the normalised x position
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the normalised y position
	 */
	public double getY() {
		return y;
	}

	/**
	 * @return the normalised width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @return the normalised height
	 */
	public double getHeight() {
		return height;
	}

}
