package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.util.Pair;

public class ListModel extends DataStructureModel {
	private List<Long> list;
	private Queue<Pair<Integer, Integer>> swaps = new LinkedList<>();

	public ListModel() {
		this.list = new ArrayList<>();
	}

	public ListModel(List<Long> list) {
		setList(list);
	}

	public void setList(List<Long> list) {
		this.list = list;

		setChanged();
		notifyObservers();
	}

	public void swap(int i, int j) {
		synchronized (list) {
			Pair<Integer, Integer> swap = new Pair<Integer, Integer>(i, j);
			swaps.add(swap);

			// Apply Update
			Long temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);

			setChanged();
			notifyObservers(swap);
		}
	}

	public void setMarkers(String markerName, int index) {
		// TODO: setMarkers
	}

	public void emphasise(int index) {
		// TODO: emphasise
	}

	public int size() {
		return list.size();
	}

	public List<Long> getList() {
		synchronized (list) {
			List<Long> copyList = new ArrayList<>();
			for (Long item : list)
				copyList.add(new Long(item));
			return copyList;
		}
	}

	@Override
	public ModelType modelType() {
		return ModelType.LIST;
	}
}
