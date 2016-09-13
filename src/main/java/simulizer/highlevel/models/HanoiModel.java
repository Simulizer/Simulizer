package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import simulizer.simulation.cpu.user_interaction.IO;

/**
 * Model for Towers of Hanoi
 * 
 * @author Michael
 *
 */
@SuppressWarnings("WeakerAccess")
public class HanoiModel extends DataStructureModel {
	private final List<Stack<Integer>> pegs = new ArrayList<>(3);
	private int numDiscs = 0;

	HanoiModel(IO io) {
		super(io);
		setNumDisks(0);
	}

	/**
	 * Sets the number of discs to use for Towers of Hanoi
	 * 
	 * @param n
	 *            the number of discs to use
	 */
	public void setNumDisks(int n) {
		if (n < 0) {
			printError("Can not have " + n + " discs");
			return;
		}
		synchronized (pegs) {
			numDiscs = n;

			// Clear all pegs
			pegs.clear();
			for (int pegCount = 0; pegCount < 3; pegCount++)
				pegs.add(new Stack<>());

			// Get the first peg and add all the discs
			Stack<Integer> firstPeg = pegs.get(0);
			for (int disc = n - 1; disc >= 0; disc--)
				firstPeg.push(disc);
		}

		setChanged();
		notifyObservers(new Discs(n));
	}

	/**
	 * Moves the top disc of startPeg onto endPeg
	 * 
	 * @param startPeg
	 *            the peg to get the disc from
	 * @param endPeg
	 *            the peg to put the disc on
	 */
	public void move(int startPeg, int endPeg) {
		if (startPeg < 0 || startPeg > 2) {
			printError("There is no start peg " + startPeg);
			return;
		} else if (endPeg < 0 || endPeg > 2) {
			printError("There is no end peg " + endPeg);
			return;
		}
		synchronized (pegs) {
			// Apply Update
			int item = -1;
			try {
				item = pegs.get(startPeg).pop();
			} catch (EmptyStackException ex) {
				printError("There are no discs on: " + (startPeg + 1));
				return;
			}
			pegs.get(endPeg).push(item);
		}

		// Notify Observers
		setChanged();
		notifyObservers(new Move(startPeg, endPeg));
	}

	@SuppressWarnings("unchecked")
	public List<Stack<Integer>> getPegs() {
		synchronized (pegs) {
			// Copies all the pegs to a new object
			List<Stack<Integer>> pegsCopy = new ArrayList<>(3);
			for (Stack<Integer> pegOrig : pegs) {
				// noinspection unchecked
				pegsCopy.add((Stack<Integer>) pegOrig.clone());
			}
			return pegsCopy;
		}
	}

	/**
	 * @return the number of discs being used
	 */
	public int getNumDiscs() {
		return numDiscs;
	}

	@Override
	public ModelType modelType() {
		return ModelType.HANOI;
	}

	public class Move extends ModelAction<List<Stack<Integer>>> {
		public final int start, end;

		private Move(int start, int end) {
			super(getPegs(), true);
			this.start = start;
			this.end = end;
		}
	}

	public class Discs extends ModelAction<List<Stack<Integer>>> {
		public final int numDiscs;

		private Discs(int numDiscs) {
			super(getPegs(), false);
			this.numDiscs = numDiscs;
		}
	}
}
