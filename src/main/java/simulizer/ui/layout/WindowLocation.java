package simulizer.ui.layout;

import simulizer.ui.interfaces.WindowEnum;

public class WindowLocation {

	private final WindowEnum window;
	private final double x, y, width, height;

	public WindowLocation(WindowEnum window, double x, double y, double width, double height) {
		this.window = window;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

	}

	public WindowEnum getWindowEnum() {
		return window;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

}
