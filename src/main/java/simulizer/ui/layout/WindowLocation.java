package simulizer.ui.layout;

import simulizer.ui.interfaces.WindowEnum;

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

	public WindowEnum getWindowEnum() {
		return id;
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
