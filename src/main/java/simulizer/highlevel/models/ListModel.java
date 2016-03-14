package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.util.Pair;

public class ListModel extends DataStructureModel {
	private List<Long> list;
	private Map<String, Integer> markers = new HashMap<>();

	public ListModel() {
		this.list = new ArrayList<>();
	}

	public ListModel(List<Long> list) {
		setList(list);
	}

	public void setList(List<Long> list) {
		this.list = list;

		setChanged();
		notifyObservers(new Action());
	}

	public void swap(int i, int j) {
		synchronized (list) {
			// Apply Update
			Long temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);

			// Notify Listeners
			setChanged();
			notifyObservers(new Swap(i, j));
		}
	}

	public void setMarkers(String markerName, int index) {
		synchronized (markers) {
			markers.put(markerName, index);
			setChanged();
			notifyObservers(new Marker(markerName, index));
		}
	}

	public void clearMarker(String markerName) {
		synchronized (markers) {
			markers.remove(markerName);
			setChanged();
			notifyObservers(new Marker(markerName));
		}
	}

	public void clearMarkers() {
		synchronized (markers) {
			markers.clear();
			setChanged();
			notifyObservers(new Marker());
		}
	}

	public void emphasise(int index) {
		setChanged();
		notifyObservers(new Emphasise(index));
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

	public class Action {
		public final List<Long> list = getList();
	}

	/**
	 * Defines a Swap Action
	 * 
	 * @author Michael
	 */
	public class Swap extends Action {
		public final int a, b;

		private Swap(final int a, final int b) {
			this.a = a;
			this.b = b;
		}
	}

	/**
	 * Defines a Marker Action
	 * 
	 * @author Michael
	 *
	 */
	public class Marker extends Action {
		public final String name;
		public final int index;

		/**
		 * Sets a marker to an index
		 * 
		 * @param name
		 *            the marker name
		 * @param index
		 *            the index
		 */
		private Marker(final String name, final int index) {
			this.name = name;
			this.index = index;
		}

		/**
		 * Clears a marker
		 * 
		 * @param name
		 *            the marker name
		 */
		private Marker(final String name) {
			this.name = name;
			this.index = -1;
		}

		/**
		 * Clear all markers
		 */
		private Marker() {
			this.name = "";
			this.index = -1;
		}
	}

	/**
	 * Defines an Emphasise Action
	 * 
	 * @author Michael
	 *
	 */
	public class Emphasise extends Action {
		public final int index;

		private Emphasise(final int index) {
			this.index = index;
		}
	}
}
