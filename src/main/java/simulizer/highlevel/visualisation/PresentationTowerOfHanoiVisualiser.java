package simulizer.highlevel.visualisation;

import javafx.scene.layout.Pane;

public class PresentationTowerOfHanoiVisualiser extends TowerOfHanoiVisualiser {
	private int[][] moves;
	private int moveIndex = -1;

	/**
	 * @param drawingPane
	 * @param width
	 * @param height
	 * @param startingPeg
	 * @param numDisks
	 * @param moves
	 *            a two dimensional array where each element is an array of two elements denoting the source peg and the destination peg,
	 *            0-2
	 */
	public PresentationTowerOfHanoiVisualiser(Pane drawingPane, int width, int height, int startingPeg, int numDisks, int[][] moves) {
		super(drawingPane, width, height, startingPeg, numDisks);
	}

	public void nextMove() {
		// will throw IndexOutOfBounds and then won't increase moveIndex
		int i = moves[moveIndex + 1][0], j = moves[moveIndex + 1][1];
		// if no IndexOutOfBounds error then increase moveIndex
		++moveIndex;
		super.move(i, j);
	}

}
