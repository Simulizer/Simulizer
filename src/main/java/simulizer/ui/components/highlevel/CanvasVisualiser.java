package simulizer.ui.components.highlevel;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import simulizer.highlevel.models.CanvasModel;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;

/**
 * Visualises a Canvas Model which provides access to a JavaFX canvas and some limited keyboard input
 * 
 * @author mbway
 *
 */
public class CanvasVisualiser extends DataStructureVisualiser {
    final private CanvasModel model;
	final private Canvas canvas;

	/**
	 * Creates a new canvas visualisation
	 * 
	 * @param model
	 *            the model to visualise
	 * @param vis
	 *            the high level visualisation window
	 */
	public CanvasVisualiser(CanvasModel model, HighLevelVisualisation vis) {
		super(model, vis);

		this.model = model;
		canvas = new Canvas();
		model.canvas = canvas;
        model.ctx = canvas.getGraphicsContext2D();

		model.ctx.setLineWidth(2);

		getChildren().add(canvas);

		vis.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
			switch(event.getCode()) {
				case UP: case W:
					setInputBit(model.UP, true); break;

				case DOWN: case S:
					setInputBit(model.DOWN, true); break;

				case LEFT: case A:
					setInputBit(model.LEFT, true); break;

				case RIGHT: case D:
					setInputBit(model.RIGHT, true); break;

				case SPACE:
					setInputBit(model.SPACE, true); break;

				default: break;
			}
			event.consume();
		});

		vis.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
			switch(event.getCode()) {
				case UP: case W:
					setInputBit(model.UP, false); break;

				case DOWN: case S:
					setInputBit(model.DOWN, false); break;

				case LEFT: case A:
					setInputBit(model.LEFT, false); break;

				case RIGHT: case D:
					setInputBit(model.RIGHT, false); break;

				case SPACE:
					setInputBit(model.SPACE, false); break;

				default: break;
			}
		});
	}

	private void setInputBit(int mask, boolean set) {
		if(set)
            model.input |= mask;
        else
			model.input &= ~mask;
	}

	@Override
	public void repaint() {
		double windowWidth = vis.getWindowWidth();
		double windowHeight = getHeight(); // exclude the tab at the bottom

		System.out.println("canvas dimensions: (" + windowWidth + ", " + windowHeight + ")");

        if(model.squareShaped) {
			double dimension = Math.min(windowWidth, windowHeight);
			canvas.setWidth(dimension);
			canvas.setHeight(dimension);
		} else {
			canvas.setWidth(windowWidth);
			canvas.setHeight(windowHeight);
		}
	}

	@Override
	public String getName() {
		return "Canvas";
	}

	@Override
	public void processChange(ModelAction<?> action) {

	}

}
