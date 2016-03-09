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
			getPegs().add(new Stack<Integer>());
	}

	public void setNumDisks(int n) {
		numDiscs = n;
		// Clear all pegs
		getPegs().clear();
		for (int pegCount = 0; pegCount < 3; pegCount++)
			getPegs().add(new Stack<Integer>());

		// Get the first peg and add all the discs
		Stack<Integer> firstPeg = getPegs().get(0);
		for (int disc = n - 1; disc >= 0; disc--)
			firstPeg.push(disc);

		setChanged();
		notifyObservers();
	}

	public void move(int startPeg, int endPeg) {
		// Apply Update
		int item = getPegs().get(startPeg).pop();
		getPegs().get(endPeg).push(item);

		// Notify Observers
		setChanged();
		notifyObservers(new Pair<Integer, Integer>(startPeg, endPeg));
	}

	public List<Stack<Integer>> getPegs() {
		return pegs;
	}

	public int getNumDiscs() {
		return numDiscs;
	}

	@Override
	public ModelType modelType() {
		return ModelType.HANOI;
	}
}
