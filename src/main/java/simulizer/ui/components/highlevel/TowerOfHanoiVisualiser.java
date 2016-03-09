package simulizer.ui.components.highlevel;

import java.util.List;
import java.util.Observable;
import java.util.Stack;

import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import simulizer.highlevel.models.HanoiModel;
import simulizer.ui.windows.HighLevelVisualisation;

public class TowerOfHanoiVisualiser extends DataStructureVisualiser {
	private Canvas canvas = new Canvas();
	private HanoiModel model;

	// Dimensions used for calculations
	private double xOffset;
	private double pegY0;
	private double pegHeight;
	private double platformWidth;
	private double pegWidth;
	private double discHeight;
	private double maxDiscWidth;
	private double discWidthDelta;

	private Color[] colorGradient = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW };

	public TowerOfHanoiVisualiser(HanoiModel model, HighLevelVisualisation vis) {
		super(model, vis);
		this.model = model;
		getChildren().add(canvas);

		canvas.widthProperty().bind(super.widthProperty());
		canvas.heightProperty().bind(super.heightProperty());

		repaint();
	}

	@Override
	public void update(Observable o, Object obj) {
		super.update(o, obj);

		repaint();
	}

	@Override
	public void repaint() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		final double width = canvas.getWidth();
		final double height = canvas.getHeight();

		calculateDimensions(gc, width, height);

		Platform.runLater(() -> {
			// Clear the screen
			gc.clearRect(0, 0, width, height);

			drawBase(gc);
			drawDiscs(gc);
		});
	}

	private void drawBase(GraphicsContext gc) {
		gc.setFill(Color.BLACK);

		// Draw the platform
		gc.fillRect(xOffset, pegY0 + pegHeight, platformWidth, pegWidth);

		// Draw the pegs
		for (int i = 0; i < 3; ++i)
			gc.fillRect(getX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight);
	}

	private void drawDiscs(GraphicsContext gc) {
		List<Stack<Integer>> pegs = model.getPegs();
		int numDiscs = model.getNumDiscs();

		for (int pegIndex = 0; pegIndex < pegs.size(); ++pegIndex) {
			Stack<Integer> peg = pegs.get(pegIndex);

			for (int i = 0; i < peg.size(); ++i) {
				// n will go from the disc at the bottom to the top
				// 0 means it's the smallest disc
				int n = peg.get(i);
				double discWidth = maxDiscWidth - discWidthDelta * (numDiscs - 1 - n);
				double discY = pegY0 + pegHeight - discHeight * (i + 1);
				double discX = getX(pegIndex) - discWidth / 2;

				gc.setLineWidth(2);
				gc.strokeRect(discX, discY, discWidth, discHeight);
				gc.setFill(colorGradient[n % colorGradient.length]);
				gc.fillRect(discX + 1, discY + 1, discWidth - 1, discHeight - 1);
			}
		}
	}

	/**
	 * @param pegIndex
	 *            the peg whose x coordinate will be calculated
	 * @return the x coordinate of the specified peg
	 */
	private double getX(int pegIndex) {
		return (int) (xOffset + (pegIndex + 0.5) * platformWidth / 3);
	}

	private void calculateDimensions(GraphicsContext gc, double width, double height) {
		this.platformWidth = (4 * width) / 5;
		this.xOffset = (width - platformWidth) / 2;
		this.pegY0 = height / 3;
		this.pegHeight = height / 2;
		this.pegWidth = width / 40;

		this.discHeight = Math.min(height / 14, pegHeight / model.getNumDiscs());
		this.maxDiscWidth = platformWidth / 3 - width / 120;
		this.discWidthDelta = Math.min(width / 30, (maxDiscWidth - pegWidth - width / 120) / (model.getNumDiscs() - 1));
	}

	@Override
	public String getName() {
		return "Towers of Hanoi";
	}

}
