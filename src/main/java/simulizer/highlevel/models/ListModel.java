package simulizer.highlevel.models;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListModel extends DataStructureModel {
	private long[] list;
	private Map<String, Integer> markers = new HashMap<>();

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
