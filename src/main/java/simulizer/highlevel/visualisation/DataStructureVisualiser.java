package simulizer.highlevel.visualisation;

import javafx.scene.layout.Pane;

public abstract class DataStructureVisualiser {
	private Pane drawingPane;
	private int width;
	private int height;
	private int rate = 1000;

	public DataStructureVisualiser(Pane drawingPane, int width, int height) {
		this.drawingPane = drawingPane;
		this.width = width;
		this.height = height;

		// Clear existing content
		drawingPane.getChildren().clear();
	}

	/**
	 * @param drawingPane
	 *            the pane onto which this visualiser should draw
	 */
	public void setDrawingPane(Pane drawingPane) {
		this.drawingPane = drawingPane;
	}

	/**
	 * Sets the rate of the animation in milliseconds
	 * @param rate the rate of the animation
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

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Pane getDrawingPane() {
		return this.drawingPane;
	}

}
