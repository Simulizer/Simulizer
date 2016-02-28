package simulizer.ui.components.highlevel;

import simulizer.ui.windows.HighLevelVisualisation;

//TODO: remove
public class PresentationTowerOfHanoiVisualiser extends TowerOfHanoiVisualiser {
	private int[][] moves = { { 0, 1 }, { 0, 2 }, { 1, 2 }, { 0, 1 }, { 2, 0 }, { 2, 1 }, { 0, 1 }, { 0, 2 }, { 1, 2 }, { 1, 0 }, { 2, 0 },
			{ 1, 2 }, { 0, 1 }, { 0, 2 }, { 1, 2 } };
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
	public PresentationTowerOfHanoiVisualiser(HighLevelVisualisation vis, double width, double height, int startingPeg, int numDisks) {
		super(vis, width, height, startingPeg, numDisks);
	}

	public void nextMove() {
		try {
			// will throw IndexOutOfBounds and then won't increase moveIndex
			int i = moves[moveIndex + 1][0], j = moves[moveIndex + 1][1];
			// if no IndexOutOfBounds error then increase moveIndex
			++moveIndex;
			move(i, j);
			commit();
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}

}
