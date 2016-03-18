package simulizer.ui.components.highlevel;

import java.util.Observable;
import java.util.Observer;
import javafx.scene.layout.Pane;
import simulizer.highlevel.models.DataStructureModel;
import simulizer.ui.windows.HighLevelVisualisation;

public abstract class DataStructureVisualiser extends Pane implements Observer {
	private int rate = 1000;
	protected HighLevelVisualisation vis;
	private boolean showing = false;
	private DataStructureModel model;

	public DataStructureVisualiser(DataStructureModel model, HighLevelVisualisation vis) {
		this.model = model;
		this.vis = vis;
		model.addObserver(this);

		widthProperty().addListener(e -> repaint());
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

	public abstract void repaint();

	public abstract String getName();

	public void close() {
		model.deleteObserver(this);
	}

	public DataStructureModel getModel() {
		return model;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == null) {
			if (model.isVisible() != showing) {
				if (model.isVisible())
					show();
				else
					hide();
			}
			repaint();
		}
	}
}
