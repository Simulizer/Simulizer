package simulizer.highlevel.visualisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;
import simulizer.ui.windows.HighLevelVisualisation;

public class TowerOfHanoiVisualiser extends DataStructureVisualiser {
	private class MoveIndices {
		public int a;
		public int b;

		public MoveIndices(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	private int startingPeg;
	private int numDiscs;
	private List<Stack<Rectangle>> pegs = new ArrayList<>();

	// Dimensions
	private double xOffset;
	private double pegY0;
	private double pegHeight;
	private double platformWidth;
	private double pegWidth;
	private double discHeight;
	private double maxdiscWidth;
	private double discWidthDelta;

	private List<Animation> animationBuffer = new ArrayList<>();
	private List<MoveIndices> moveIndices = new ArrayList<>();

	private Color[] colorGradient = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW };

	// Shapes
	private Rectangle base;
	private Rectangle[] vPegs = new Rectangle[3];
	private Rectangle[] discs;

	public TowerOfHanoiVisualiser(HighLevelVisualisation vis, int width, int height, int startingPeg, int numDiscs) {
		super(vis, width, height);

		this.startingPeg = startingPeg;
		this.numDiscs = numDiscs;
		this.discs = new Rectangle[numDiscs];

		this.platformWidth = (4 * width) / 5;
		this.xOffset = (width - platformWidth) / 2;
		this.pegY0 = height / 3;
		this.pegHeight = height / 2;
		this.pegWidth = width / 40;

		this.discHeight = Math.min(height / 14, pegHeight / numDiscs);
		this.maxdiscWidth = platformWidth / 3 - width / 120;
		this.discWidthDelta = Math.min(width / 30, (maxdiscWidth - pegWidth - width / 120) / (numDiscs - 1));

		init();
		resize();
	}

	/**
	 * Draws the platform and the initial discs
	 */
	private void init() {
		HighLevelVisualisation vis = getHighLevelVisualisation();

		this.base = new Rectangle(xOffset, pegY0 + pegHeight, platformWidth, discHeight);
		vis.add(base);

		for (int i = 0; i < 3; ++i) {
			vPegs[i] = new Rectangle(getX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight);
			vis.add(vPegs[i]);
		}

		for (int i = 0; i < 3; ++i) {
			pegs.add(new Stack<Rectangle>());
		}

		// Draw discs
		double y = pegY0 + pegHeight - discHeight;
		double width = maxdiscWidth;
		double x = xOffset + 5 / 2;
		for (int i = 0; i < numDiscs; ++i) {
			discs[i] = new Rectangle(x, y, width, discHeight);
			discs[i].setFill(colorGradient[i % colorGradient.length]);
			discs[i].setStroke(Color.BLACK);

			pegs.get(startingPeg).push(discs[i]);

			vis.add(discs[i]);
			x += (discWidthDelta / 2);
			width -= discWidthDelta;
			y -= discHeight;
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

	/**
	 * @param pegIndex
	 *            the index of the peg for the calculation
	 * @return the y coordinate of the topmost disc on the specified peg
	 */
	private double getY(int pegIndex) {
		int numdiscsOnPeg = pegs.get(pegIndex).size();
		return ((pegY0 + pegHeight) - numdiscsOnPeg * discHeight);
	}

	/**
	 * Calculates the animation for moving the disc at peg i to peg j. If there
	 * is no disc on peg i, then it does nothing
	 *
	 * @param i
	 *            the index of the peg to move the disc from
	 * @param j
	 *            the index of the peg to move the disc to
	 */
	public void move(int i, int j) {
		if (pegs.get(i).size() == 0 || i == j) return;

		Rectangle disc = pegs.get(i).peek();

		animationBuffer.add(getTransition(disc, getX(i), getY(i), getX(j), getY(j) - discHeight));
		moveIndices.add(new MoveIndices(i, j));
	}

	/**
	 * Commits and animates any buffered animations.
	 */
	public void commit() {
		ParallelTransition animation = new ParallelTransition();
		animation.getChildren().addAll(animationBuffer);
		animation.play();
		animationBuffer.clear();

		// Move the pegs in memory after animating them
		for (MoveIndices m : moveIndices) {
			pegs.get(m.b).push(pegs.get(m.a).pop());
		}

		moveIndices.clear();
	}

	/**
	 * Calculates the transition for the given disc from the source to the
	 * target.
	 *
	 * @param disc
	 *            the disc to be animated
	 * @param x1
	 *            the starting x coordinate
	 * @param y1
	 *            the starting y coordinate
	 * @param x2
	 *            the destination x coordinate
	 * @param y2
	 *            the destination y coordinate
	 * @return the transition representing the animation from the source to the
	 *         target
	 */
	private PathTransition getTransition(Rectangle disc, double x1, double y1, double x2, double y2) {
		int height = disc.heightProperty().intValue();
		double arcHeight = (getHighLevelVisualisation().getWindowHeight() / 10);

		Path path = new Path();
		path.getElements().add(new MoveTo(x1, y1 + height / 2));
		path.getElements().add(new VLineTo(pegY0 - arcHeight));
		path.getElements().add(new HLineTo(x2));
		path.getElements().add(new VLineTo(y2 + (height / 2)));

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(getRate()));
		pathTransition.setPath(path);
		pathTransition.setNode(disc);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

	@Override
	public void resize() {
		double windowWidth = getHighLevelVisualisation().getWindowWidth();
		double windowHeight = getHighLevelVisualisation().getWindowHeight();

		this.platformWidth = (4 * windowWidth) / 5;
		this.xOffset = (windowWidth - platformWidth) / 2;
		this.pegY0 = windowHeight / 3;
		this.pegHeight = windowHeight / 2;
		this.pegWidth = windowWidth / 40;

		this.discHeight = Math.min(windowHeight / 14, pegHeight / numDiscs);
		this.maxdiscWidth = platformWidth / 3 - windowWidth / 120;
		this.discWidthDelta = Math.min(windowWidth / 30, (maxdiscWidth - pegWidth - windowWidth / 120) / (numDiscs - 1));

		setAttrs(this.base, xOffset, pegY0 + pegHeight, platformWidth, discHeight);

		for (int i = 0; i < 3; ++i) {
			setAttrs(vPegs[i], getX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight);
		}

		double w = getWidth();
		double h = getHeight();

		// Draw discs
		for (int i = 0; i < numDiscs; ++i) {
			Rectangle disc = discs[i];

			double newX = (disc.getX() / w) * windowWidth;
			double newY = (disc.getY() / h) * windowHeight;
			double newWidth = (disc.widthProperty().doubleValue() / w) * windowWidth;
			double newHeight = (disc.heightProperty().doubleValue() / h) * windowHeight;

			setAttrs(discs[i], newX, newY, newWidth, newHeight);
		}

		setWidth(windowWidth);
		setHeight(windowHeight);
	}

}
