package simulizer.ui.components.highlevel;

import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import simulizer.ui.windows.HighLevelVisualisation;

public abstract class DataStructureVisualiser extends Pane {
	private int rate = 1000;
	protected HighLevelVisualisation vis;
	private boolean showing = false;

	public DataStructureVisualiser(HighLevelVisualisation vis) {
		this.vis = vis;
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

	public void setAttrs(Rectangle rect, double x, double y, double width, double height) {
		if (rect != null) {
			rect.setX(x);
			rect.setY(y);
			rect.setWidth(width);
			rect.setHeight(height);
		}
	}

	public void show() {
		if (!showing)
			vis.addTab(this);
		showing = true;
	}

	public void hide() {
		if (showing)
			vis.removeTab(this);
		showing = false;
	}

	public abstract void resize();

	public abstract String getName();

}
