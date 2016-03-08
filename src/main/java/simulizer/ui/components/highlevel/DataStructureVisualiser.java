package simulizer.ui.components.highlevel;

import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import simulizer.ui.windows.HighLevelVisualisation;

public abstract class DataStructureVisualiser {
	private double width;
	private double height;
	private int rate = 1000;
	protected HighLevelVisualisation vis;

	public DataStructureVisualiser(HighLevelVisualisation vis) {
		this.width = vis.getWindowWidth();
		this.height = vis.getWindowHeight();
		this.vis = vis;

		// TODO use Platform.runLater somehow
		vis.getDrawingPane().getChildren().clear();
		//Platform.runLater(() -> vis.getDrawingPane().getChildren().clear());
	}

	/**
	 * Sets the rate of the animation in milliseconds
	 *
	 * @param rate
	 *            the rate of the animation
	 */
	public void setRate(int rate) {
		this.rate = rate;
	}

	/**
	 * @return the rate of the animation
	 */
	public int getRate() {
		return rate;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setAttrs(Rectangle rect, double x, double y, double width, double height) {
		if(rect != null) {
			rect.setX(x);
			rect.setY(y);
			rect.setWidth(width);
			rect.setHeight(height);
		}
	}

	public abstract void resize();

}
