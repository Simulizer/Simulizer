package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javafx.util.Pair;

public class HanoiModel extends DataStructureModel {
	private final List<Stack<Integer>> pegs = new ArrayList<>();
	private int numDiscs = 0;

	public HanoiModel() {
		for (int pegCount = 0; pegCount < 3; pegCount++)
			pegs.add(new Stack<Integer>());
	}

	public void setNumDisks(int n) {
		numDiscs = n;
		// Clear all pegs
		pegs.clear();
		for (int pegCount = 0; pegCount < 3; pegCount++)
			pegs.add(new Stack<Integer>());

		// Get the first peg and add all the discs
		Stack<Integer> firstPeg = pegs.get(0);
		for (int disc = n - 1; disc >= 0; disc--)
			firstPeg.push(disc);

		setChanged();
		notifyObservers();
	}

	public void move(int startPeg, int endPeg) {
		synchronized (pegs) {
			Pair<Integer, Integer> move = new Pair<>(startPeg, endPeg);

			// Apply Update
			int item = pegs.get(startPeg).pop();
			pegs.get(endPeg).push(item);

			// Notify Observers
			setChanged();
			notifyObservers(move);
		}
	}

	public List<Stack<Integer>> getPegs() {
		synchronized (pegs) {
			// Copies all the pegs to a new object
			List<Stack<Integer>> pegsCopy = new ArrayList<>();
			for (Stack<Integer> pegOrig : pegs) {
				// Copy peg into revPeg
				Stack<Integer> pegRev = new Stack<>();
				pegRev.addAll(pegOrig);

				// Copy revPeg into a new peg
				Stack<Integer> pegCopy = new Stack<>();
				for (Integer item : pegRev) 
					pegCopy.add(new Integer(item));
				
				// Add new peg to pegsCopy
				pegsCopy.add(pegCopy);
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
}
