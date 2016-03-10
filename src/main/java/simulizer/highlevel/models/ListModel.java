package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.util.Pair;

public class ListModel extends DataStructureModel {
	private List<Object> list;
	private Queue<Pair<Integer, Integer>> swaps = new LinkedList<>();

	public ListModel() {
		this.list = new ArrayList<>();
	}

	public ListModel(List<Object> list) {
		setList(list);
	}

	public void setList(List<Object> list) {
		this.list = list;

		setChanged();
		notifyObservers();
	}

	public void swap(int i, int j) {
		Pair<Integer, Integer> swap = new Pair<Integer, Integer>(i, j);
		swaps.add(swap);

		setChanged();
		notifyObservers(swap);
	}

	public void step() {
		Pair<Integer, Integer> move = swaps.poll();
		int i = move.getKey(), j = move.getValue();

		// Apply Update
		Object temp = list.get(i);
		list.set(i, list.get(j));
		list.set(j, temp);
	}

	public int size() {
		return list.size();
	}

	public List<Object> getList() {
		return list;
	}

	@Override
	public ModelType modelType() {
		return ModelType.LIST;
	}
}
