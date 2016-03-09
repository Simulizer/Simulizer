package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import javafx.util.Pair;

public class HanoiModel extends DataStructureModel {
	private final List<Stack<Integer>> pegs = new ArrayList<>();
	private final Queue<Pair<Integer, Integer>> updateQueue = new LinkedList<>();
	private boolean queuing = false;
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

	public void batch() {
		queuing = true;
	}

	public void move(int startPeg, int endPeg) {
		updateQueue.add(new Pair<Integer, Integer>(startPeg, endPeg));
		if (!queuing)
			commit();
	}

	public void commit() {
		Queue<Pair<Integer, Integer>> copyQueue = new LinkedList<>();

		// Apply Update
		for (Pair<Integer, Integer> update : updateQueue) {
			copyQueue.add(update);
			int item = getPegs().get(update.getKey()).pop();
			getPegs().get(update.getValue()).push(item);
		}
		queuing = false;

		// Notify Observers
		setChanged();
		notifyObservers(copyQueue);
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
