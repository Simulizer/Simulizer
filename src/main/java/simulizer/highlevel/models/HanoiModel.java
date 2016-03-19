package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class HanoiModel extends DataStructureModel {
	private final List<Stack<Integer>> pegs = new ArrayList<>(3);
	private int numDiscs = 0;

	public HanoiModel() {
		for (int pegCount = 0; pegCount < 3; pegCount++)
			pegs.add(new Stack<>());
	}

	public void setNumDisks(int n) {
		numDiscs = n;
		// Clear all pegs
		pegs.clear();
		for (int pegCount = 0; pegCount < 3; pegCount++)
			pegs.add(new Stack<>());

		// Get the first peg and add all the discs
		Stack<Integer> firstPeg = pegs.get(0);
		for (int disc = n - 1; disc >= 0; disc--)
			firstPeg.push(disc);

		setChanged();
		notifyObservers(new Discs(n));
	}

	public void move(int startPeg, int endPeg) {
		System.out.println("" + startPeg + "->" + endPeg);
		synchronized (pegs) {
			// Apply Update
			int item = pegs.get(startPeg).pop();
			pegs.get(endPeg).push(item);

			// Notify Observers
			setChanged();
			notifyObservers(new Move(startPeg, endPeg));
		}
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
