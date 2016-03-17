package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ListModel extends DataStructureModel {
	private long[] list;
	private Map<Integer, ArrayList<String>> markers = new HashMap<>();

	public ListModel(List<Long> list) {
		setList(list);
	}

	public ListModel() {
		list = new long[0];
	}

	public void setList(List<Long> list) {
		this.list = new long[list.size()];

		for (int i = 0; i < list.size(); i++)
			this.list[i] = list.get(i);

		setChanged();
		notifyObservers(new Action());
	}

	public void set(int i, Long item) {
		synchronized (list) {
			list[i] = item;
			setChanged();
			notifyObservers(new Action());
		}
	}

	public void swap(int i, int j) {
		synchronized (list) {
			// Apply Update
			long temp = list[i];
			list[i] = list[j];
			list[j] = temp;

			// Notify Listeners
			setChanged();
			notifyObservers(new Swap(i, j));
		}
	}

	public void setMarkers(String markerName, int index) {
		synchronized (markers) {
			ArrayList<String> exMarkers = markers.get(index);
			if (exMarkers != null)
				exMarkers.add(markerName);
			else {
				exMarkers = new ArrayList<>(2);
				markers.put(index, exMarkers);
			}

			setChanged();
			notifyObservers(new Marker(index, markerName));
		}
	}

	public void clearMarker(int index) {
		synchronized (markers) {
			markers.remove(index);
			setChanged();
			notifyObservers(new Marker(index));
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
		return list.length;
	}

	public long[] getList() {
		synchronized (list) {
			if (list.length == 0)
				return new long[0];
			else
				return Arrays.copyOf(list, list.length);
		}
	}

	public Map<Integer, ArrayList<String>> getMarkers() {
		synchronized (markers) {
			Map<Integer, ArrayList<String>> copy = new HashMap<>();
			for (Map.Entry<Integer, ArrayList<String>> entry : markers.entrySet()) {
				copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
			}

			return copy;
		}
	}

	@Override
	public ModelType modelType() {
		return ModelType.LIST;
	}

	public class Action {
		public final long[] list = getList();
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
		public final Optional<String> name;
		public final Optional<Integer> index;

		/**
		 * Sets a marker to an index
		 *
		 * @param name
		 *            the marker name
		 * @param index
		 *            the index
		 */
		private Marker(final int index, final String name) {
			this.index = Optional.of(index);
			this.name = Optional.of(name);
		}

		/**
		 * Clears a marker
		 *
		 * @param index
		 *            the index
		 */
		private Marker(final int index) {
			this.index = Optional.of(index);
			this.name = Optional.empty();
		}

		/**
		 * Clear all markers
		 */
		private Marker() {
			this.name = Optional.empty();
			this.index = Optional.empty();
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
