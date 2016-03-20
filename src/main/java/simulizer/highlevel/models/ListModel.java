package simulizer.highlevel.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import simulizer.simulation.cpu.user_interaction.IO;

public class ListModel extends DataStructureModel {
	private long[] list;
	private int size = 0;
	private Map<Integer, ArrayList<String>> markers = new HashMap<>();

	public ListModel(IO io, List<Long> list) {
		super(io);
		setList(list);
	}

	public ListModel(IO io) {
		super(io);
		list = new long[0];
	}

	public void setList(List<Long> list) {
		synchronized (this.list) {
			synchronized (markers) {
				this.list = new long[list.size()];
				size = list.size();

				for (int i = 0; i < list.size(); i++)
					this.list[i] = list.get(i);
			}
		}
		setChanged();
		notifyObservers(new ListAction());
	}

	private boolean checkIndex(int index) {
		if (index < 0 || index >= size) {
			printError("There is no element " + index);
			return true;
		}
		return false;
	}

	public void set(int i, Long item) {
		synchronized (list) {
			if (checkIndex(i))
				return;
			list[i] = item;
			setChanged();
			notifyObservers(new ListAction());
		}
	}

	public void swap(int i, int j) {
		synchronized (list) {
			if (checkIndex(i))
				return;
			if (checkIndex(j))
				return;

			// Apply Update
			long temp = list[i];
			list[i] = list[j];
			list[j] = temp;

			// Notify Listeners
			setChanged();
			notifyObservers(new SwapAction(i, j));
		}
	}

	public void setMarkers(String markerName, int index) {
		synchronized (markers) {
			if (checkIndex(index))
				return;

			ArrayList<String> exMarkers = markers.get(index);
			if (exMarkers != null)
				exMarkers.add(markerName);
			else {
				exMarkers = new ArrayList<>(2);
				markers.put(index, exMarkers);
			}

			setChanged();
			notifyObservers(new MarkerAction(index, markerName));
		}
	}

	public void highlightMarker(int index) {
		setChanged();
		notifyObservers(new HighlightAction(index));
	}

	public void clearMarker(int index) {
		synchronized (markers) {
			markers.remove(index);
			setChanged();
			notifyObservers(new MarkerAction(index));
		}
	}

	public void clearMarkers() {
		synchronized (markers) {
			markers.clear();
			setChanged();
			notifyObservers(new MarkerAction());
		}
	}

	public void emphasise(int index) {
		setChanged();
		notifyObservers(new EmphasiseAction(index));
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

	public class ListAction extends ModelAction<long[]> {

		private ListAction() {
			super(getList(), false);
		}
	}

	/**
	 * Defines a Swap Action
	 *
	 * @author Michael
	 */
	public class SwapAction extends ModelAction<long[]> {
		public final int a, b;

		private SwapAction(final int a, final int b) {
			super(getList(), true);
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
	public class MarkerAction extends ModelAction<long[]> {
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
		private MarkerAction(final int index, final String name) {
			super(getList(), false);
			this.index = Optional.of(index);
			this.name = Optional.of(name);
		}

		/**
		 * Clears a marker
		 *
		 * @param index
		 *            the index
		 */
		private MarkerAction(final int index) {
			super(getList(), false);
			this.index = Optional.of(index);
			this.name = Optional.empty();
		}

		/**
		 * Clear all markers
		 */
		private MarkerAction() {
			super(getList(), false);
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
	public class EmphasiseAction extends ModelAction<long[]> {
		public final int index;

		private EmphasiseAction(final int index) {
			super(getList(), false);
			this.index = index;
		}
	}

	public class HighlightAction extends ModelAction<long[]> {
		public final int index;

		private HighlightAction(final int index) {
			super(getList(), false);
			this.index = index;
		}
	}
}
