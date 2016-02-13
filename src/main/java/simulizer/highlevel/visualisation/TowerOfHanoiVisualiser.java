package simulizer.highlevel.visualisation;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.VLineTo;
import javafx.util.Duration;

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

	private int xOffset = 80;
	private int pegY0 = 150;
	private int pegHeight = 350;
	private int platformWidth = 840;
	private int pegWidth = 30;
	private int diskHeight;
	private int maxDiskWidth = platformWidth / 3 - 10;
	private int diskWidthDelta = 10;

	private List<Animation> animationBuffer = new ArrayList<>();
	private List<MoveIndices> moveIndices = new ArrayList<>();
	private Color[] colorGradient = { Color.RED, Color.ORANGE, Color.BLUE, Color.GREEN, Color.YELLOW };

	public TowerOfHanoiVisualiser(Pane drawingPane, int width, int height, int startingPeg, int numDisks) {
		super(drawingPane, width, height);
		this.startingPeg = startingPeg;
		this.numDiscs = numDisks;
		this.diskHeight = Math.min(50, pegHeight / numDisks);
		this.diskWidthDelta = Math.min(40, (maxDiskWidth - pegWidth - 10) / (numDisks - 1));
		initPlatform();
	}

	/**
	 * Draws the platform and the initial disks
	 */
	private void initPlatform() {
		Pane contentPane = getDrawingPane();
		// Remove any existing items
		contentPane.getChildren().clear();

		Rectangle base = new Rectangle(80, 500, 840, 35);
		contentPane.getChildren().addAll(base);

		for (int i = 0; i < 3; ++i) {
			contentPane.getChildren().add(new Rectangle(getX(i) - pegWidth / 2, pegY0, pegWidth, pegHeight));
		}

		for (int i = 0; i < 3; ++i) {
			pegs.add(new Stack<Rectangle>());
		}

		// Draw discs
		int y = pegY0 + pegHeight - diskHeight;
		int width = maxDiskWidth;
		int x = xOffset + 5;
		for (int i = 0; i < numDiscs; ++i) {
			Rectangle rect = new Rectangle(x, y, width, diskHeight);
			rect.setFill(colorGradient[i % colorGradient.length]);
			rect.setStroke(Color.BLACK);

			pegs.get(startingPeg).push(rect);

			contentPane.getChildren().add(rect);
			x += diskWidthDelta / 2;
			width -= diskWidthDelta;
			y -= diskHeight;
		}
	}

	/**
	 * @param pegIndex
	 *            the peg whose x coordinate will be calculated
	 * @return the x coordinate of the specified peg
	 */
	private int getX(int pegIndex) {
		return (int) (xOffset + (pegIndex + 0.5) * platformWidth / 3);
	}

	/**
	 * @param pegIndex
	 *            the index of the peg for the calculation
	 * @return the y coordinate of the topmost disk on the specified peg
	 */
	private int getY(int pegIndex) {
		int numDisksOnPeg = pegs.get(pegIndex).size();
		return (pegY0 + pegHeight) - numDisksOnPeg * diskHeight;
	}

	/**
	 * Calculates the animation for moving the disk at peg i to peg j. If there
	 * is no disk on peg i, then it does nothing
	 * 
	 * @param i
	 *            the index of the peg to move the disc from
	 * @param j
	 *            the index of the peg to move the disc to
	 */
	public void move(int i, int j) {
		if (pegs.get(i).size() == 0)
			return;

		Rectangle disc = pegs.get(i).peek();

		animationBuffer.add(getTransition(disc, getX(i), getY(i), getX(j), getY(j) - diskHeight));
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
	 * Calculates the transition for the given disk from the source to the
	 * target.
	 * 
	 * @param disc
	 *            the disk to be animated
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
	private PathTransition getTransition(Rectangle disc, int x1, int y1, int x2, int y2) {
		int height = disc.heightProperty().intValue();

		Path path = new Path();
		path.getElements().add(new MoveTo(x1, y1 + height / 2));
		path.getElements().add(new VLineTo(pegY0 - 70));
		path.getElements().add(new HLineTo(x2));
		path.getElements().add(new VLineTo(y2 + height / 2));

		PathTransition pathTransition = new PathTransition();
		pathTransition.setDuration(Duration.millis(2000));
		pathTransition.setPath(path);
		pathTransition.setNode(disc);
		pathTransition.setCycleCount(1);

		return pathTransition;
	}

}
