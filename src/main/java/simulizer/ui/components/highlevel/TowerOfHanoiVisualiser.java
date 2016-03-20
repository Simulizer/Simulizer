package simulizer.ui.components.highlevel;

import java.util.List;
import java.util.Stack;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import simulizer.highlevel.models.HanoiModel;
import simulizer.highlevel.models.HanoiModel.Discs;
import simulizer.highlevel.models.HanoiModel.Move;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;

public class TowerOfHanoiVisualiser extends DataStructureVisualiser {
	private List<Stack<Integer>> pegs;
	private volatile int numDiscs = 0;

	private Canvas canvas = new Canvas();
	private HanoiModel model;
	private Color[] colorGradient = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW };

	private int animatedDiscIndex;
	private DoubleProperty animatedDiscX = new SimpleDoubleProperty();
	private DoubleProperty animatedDiscY = new SimpleDoubleProperty();
	private double animatedDiscWidth;

	// Dimensions used for calculations
	private double xOffset;
	private double pegY0;
	private double pegHeight;
	private double platformWidth;
	private double pegWidth;
	private double discHeight;
	private double maxDiscWidth;
	private double discWidthDelta;

	public TowerOfHanoiVisualiser(HanoiModel model, HighLevelVisualisation vis) {
		super(model, vis);
		this.model = model;
		pegs = model.getPegs();
		numDiscs = model.getNumDiscs();
		getChildren().add(canvas);

		canvas.widthProperty().bind(super.widthProperty());
		canvas.widthProperty().addListener(e -> Platform.runLater(this::repaint));
		canvas.heightProperty().bind(super.heightProperty());
		canvas.heightProperty().addListener(e -> Platform.runLater(this::repaint));

		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setStroke(Color.BLACK);
	}

	@Override
	public synchronized void repaint() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		final double width = canvas.getWidth();
		final double height = canvas.getHeight();

		calculateDimensions(gc, width, height);

		// TODO Platform.runLater() ?
		gc.clearRect(0, 0, width, height);
		drawBase(gc);
		drawStaticDiscs(gc);
		if (isUpdatePaused()) {
			drawBorderedRectangle(gc, colorGradient[animatedDiscIndex % colorGradient.length], animatedDiscX.doubleValue(), animatedDiscY.doubleValue(), animatedDiscWidth, discHeight);
		}
	}

	private void drawBase(GraphicsContext gc) {
		gc.setFill(Color.BLACK);

		// Draw the platform
		gc.fillRect(xOffset, pegY0 + pegHeight, platformWidth, pegWidth);

		// Draw the pegs
		for (int i = 0; i < 3; ++i)
			gc.fillRect(getPegX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight + 2); // + 2 to avoid gap between peg and platform
	}

	private void drawStaticDiscs(GraphicsContext gc) {
		for (int pegIndex = 0; pegIndex < pegs.size(); ++pegIndex) {
			Stack<Integer> peg = pegs.get(pegIndex);

			for (int i = 0; i < peg.size(); ++i) {
				// n will go from the disc at the bottom to the top
				// 0 means it's the smallest disc
				int n = peg.get(i);

				// Don't draw the animated disc
				if (isUpdatePaused() && n == animatedDiscIndex)
					continue;

				double discWidth = getDiscWidth(n, numDiscs);
				double discY = getDiscY(i);
				double discX = getPegX(pegIndex) - discWidth / 2;

				drawBorderedRectangle(gc, colorGradient[n % colorGradient.length], discX, discY, discWidth, discHeight);

				gc.setFill(colorGradient[n % colorGradient.length]);
				gc.fillRect(discX, discY, discWidth, discHeight);
				gc.strokeRect(discX, discY, discWidth, discHeight);
			}
		}
	}

	private void drawBorderedRectangle(GraphicsContext gc, Color fill, double x, double y, double w, double h) {
		gc.setFill(fill);
		gc.fillRect(x, y, w, h);
		gc.strokeRect(x, y, w, h);
	}

	/**
	 * @param pegIndex
	 *            the peg whose x coordinate will be calculated
	 * @return the x coordinate of the specified peg
	 */
	private double getPegX(int pegIndex) {
		return (int) (xOffset + (pegIndex + 0.5) * platformWidth / 3);
	}

	private double getDiscWidth(int discIndex, int numDiscs) {
		return maxDiscWidth - discWidthDelta * (numDiscs - 1 - discIndex);
	}

	private double getDiscY(int fromBottom) {
		return pegY0 + pegHeight - discHeight * (fromBottom + 1);
	}

	private void calculateDimensions(GraphicsContext gc, double width, double height) {
		this.platformWidth = (4 * width) / 5;
		this.xOffset = (width - platformWidth) / 2;
		this.pegY0 = height / 3;
		this.pegHeight = height / 2;
		this.pegWidth = width / 40;

		this.discHeight = Math.min(height / 14, pegHeight / numDiscs);
		this.maxDiscWidth = platformWidth / 3 - width / 120;
		this.discWidthDelta = Math.min(width / 30, (maxDiscWidth - pegWidth - width / 120) / (numDiscs - 1));
	}

	@Override
	public String getName() {
		return "Towers of Hanoi";
	}

	@Override
	public synchronized void processChange(ModelAction<?> action) {
		System.out.println("Pegs: " + pegs.get(0).size() + ", " + pegs.get(1).size() + ", " + pegs.get(2).size());
		if (action instanceof Discs) {
			Discs discs = (Discs) action;
			pegs = discs.structure;
			numDiscs = discs.numDiscs;
			System.out.println("Discs " + discs.numDiscs + ": " + discs.structure.get(0).size() + ", " + discs.structure.get(1).size() + ", " + discs.structure.get(2).size() + "\n");
		} else if (action instanceof Move) {
			Move move = (Move) action;
			
			if (move.structure.get(move.start).size() != pegs.get(move.start).size() - 1 || move.structure.get(move.end).size() != pegs.get(move.end).size() + 1)
				System.out.println("INVALID MOVE");
			System.out.println("Move " + move.start + "->" + move.end + ": " + move.structure.get(0).size() + ", " + move.structure.get(1).size() + ", " + move.structure.get(2).size()  + "\n");

			int numDiscsOnStart = pegs.get(move.start).size();
			int numDiscsOnEnd = pegs.get(move.end).size();

			animatedDiscIndex = pegs.get(move.start).peek();
			this.animatedDiscWidth = getDiscWidth(animatedDiscIndex, model.getNumDiscs());

			double startX = getPegX(move.start) - animatedDiscWidth / 2;
			double startY = getDiscY(numDiscsOnStart);

			double upX = startX;
			double upY = pegY0 - canvas.getHeight() / 10;

			double shiftX = getPegX(move.end) - animatedDiscWidth / 2;
			double shiftY = upY;

			double endX = shiftX;
			double endY = getDiscY(numDiscsOnEnd);

			animatedDiscX.set(startX);
			animatedDiscY.set(startY);

			// @formatter:off
			Timeline timeline = new Timeline(
				new KeyFrame(Duration.seconds(0),
					new KeyValue(animatedDiscX, startX),
					new KeyValue(animatedDiscY, startY)
				),
				new KeyFrame(Duration.seconds(0.5),
					new KeyValue(animatedDiscX, upX),
					new KeyValue(animatedDiscY, upY)
				),
				new KeyFrame(Duration.seconds(0.8),
					new KeyValue(animatedDiscX, shiftX),
					new KeyValue(animatedDiscY, shiftY)
				),
				new KeyFrame(Duration.seconds(1.3),
					e -> {
						// Apply Update
						pegs = move.structure;
						setUpdatePaused(false);
					},
					new KeyValue(animatedDiscX, endX),
					new KeyValue(animatedDiscY, endY)
				)
			);
			// @formatter:on
			timeline.setCycleCount(1);
			timeline.setRate(rate);

			timeline.play();

			setUpdatePaused(true);
		}
	}

}
