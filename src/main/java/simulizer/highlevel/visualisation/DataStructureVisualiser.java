package simulizer.highlevel.visualisation;

import javafx.scene.layout.Pane;

public abstract class DataStructureVisualiser {
	private Pane drawingPane;
	private int width;
	private int height;
	
	public DataStructureVisualiser(Pane drawingPane, int width, int height) {
		this.drawingPane = drawingPane;
		this.width = width;
		this.height = height;
	}

	/**
	 * @param drawingPane
	 *            the pane onto which this visualiser should draw
	 */
	public void setDrawingPane(Pane drawingPane) {
		this.drawingPane = drawingPane;
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
