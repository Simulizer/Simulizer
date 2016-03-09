package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javafx.util.Pair;

public class HanoiModel extends DataStructureModel {
	private final List<Stack<Integer>> pegs = new ArrayList<>();
	private final Queue<Pair<Integer, Integer>> moves = new LinkedList<>();
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
		Pair<Integer, Integer> move = new Pair<>(startPeg, endPeg);
		moves.add(move);

		// Notify Observers
		setChanged();
		notifyObservers(move);
	}

	public void step() {
		Pair<Integer, Integer> move = moves.poll();
		int startPeg = move.getKey(), endPeg = move.getValue();

		// Apply Update
		int item = getPegs().get(startPeg).pop();
		getPegs().get(endPeg).push(item);
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
