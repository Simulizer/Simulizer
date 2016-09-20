package simulizer.ui.components.highlevel;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;
import simulizer.utils.CircularLongBuffer;
import simulizer.utils.ThreadUtils;

/**
 * A high level visualisation
 * 
 * @author Michael
 *
 */
public abstract class DataStructureVisualiser extends Pane implements Observer {
	protected HighLevelVisualisation vis;
	private DataStructureModel model;

	private boolean showing = false;

	private BlockingQueue<ModelAction<?>> changes = new LinkedBlockingQueue<>();

	private int FRAME_RATE = 45, BUFFER_SIZE = 1;
	private AnimationTimer timer;

	volatile double rate = 1;
	private final HashMap<String, CircularLongBuffer> updateTimes = new HashMap<String, CircularLongBuffer>();
	private final HashMap<String, CircularLongBuffer> processTimes = new HashMap<String, CircularLongBuffer>();
	private final HashMap<String, Boolean> hasChanged = new HashMap<String, Boolean>();
	private final HashMap<String, Long> lastUpdates = new HashMap<String, Long>();

	private Thread updateThread;
	private ThreadUtils.Blocker updatePaused;
	private volatile boolean alive = false;

	/**
	 * @param model
	 *            the model to visualise
	 * @param vis
	 *            the high level visualisation window
	 */
	DataStructureVisualiser(DataStructureModel model, HighLevelVisualisation vis) {
		this.model = model;
		this.vis = vis;
		updatePaused = new ThreadUtils.Blocker();
		model.addObserver(this);

		widthProperty().addListener(e -> repaint());
	}

	/**
	 * Shows the visualisation
	 */
	public void show() {
		if (!showing) {
			vis.addTab(this);
			startUpdateThreads();
			showing = true;
		}
	}

	/**
	 * Hides the visualisation
	 */
	private void hide() {
		if (showing) {
			vis.removeTab(this);
			stopUpdateThreads();
			showing = false;
		}
	}

	/**
	 * Processes a change.
	 * 
	 * @param action
	 *            the change to process
	 */
	public abstract void processChange(ModelAction<?> action);

	/**
	 * Repaints the visualisation
	 */
	public abstract void repaint();

	/**
	 * @return the name of the visualisation
	 */
	public abstract String getName();

	/**
	 * Closes the visualisation
	 */
	public void close() {
		stopUpdateThreads();
		model.deleteObserver(this);
	}

	/**
	 * @return the model to visualise
	 */
	public DataStructureModel getModel() {
		return model;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg == null) {
			if (model.isVisible() != showing) {
				if (model.isVisible())
					show();
				else
					hide();
			}
		} else if (arg instanceof ModelAction<?>) {
			String className = arg.getClass().getName();
			changes.add((ModelAction<?>) arg);
			long now = System.nanoTime();

			if (lastUpdates.containsKey(className)) {
				if (!updateTimes.containsKey(className))
					updateTimes.put(className, new CircularLongBuffer(BUFFER_SIZE));

				updateTimes.get(className).add(now - lastUpdates.get(className));
				hasChanged.put(className, true);
			}

			lastUpdates.put(className, now);
		}
	}

	/**
	 * Starts all update threads
	 */
	private void startUpdateThreads() {
		if (alive)
			return;
		alive = true;
		updateThread = new Thread(() -> {
			while (alive) {
				try {
					// Process change
					long before = -1, after = -1;
					ModelAction<?> change = null;
					if (alive) {
						change = changes.take();
						before = System.nanoTime();
						processChange(change);

						// Wait if paused
						updatePaused.waitIfPaused();

						after = System.nanoTime();
					}

					if (alive && before != -1 && after != -1 && change != null) {
						// Add process time
						String className = change.getClass().getName();
						if (!processTimes.containsKey(className))
							processTimes.put(className, new CircularLongBuffer(BUFFER_SIZE));
						processTimes.get(className).add(after - before);
						hasChanged.put(className, true);

						// Recalculate rate/skips
						rateSkips();
					}
				} catch (InterruptedException ignored) {
				}
			}
		}, "DataStructure " + getName() + " Updates");
		updateThread.setDaemon(true);
		updateThread.start();

		timer = new AnimationTimer() {
			long lastTime = -1;

			@Override
			public void handle(long now) {
				if (lastTime == -1 || now - lastTime > 1e9 / FRAME_RATE) {
					lastTime = now;
					repaint();
				}
			}
		};
		timer.start();
	}

	/**
	 * Calculate the rate at which animations should be animating at to stay in sync. Skips some changes if it gets too far behind
	 */
	private synchronized void rateSkips() {
		System.out.println("\nbeforeRate: " + rate);
		double maxChange = Double.NEGATIVE_INFINITY;
		double minChange = Double.POSITIVE_INFINITY;
		String maxBlame = "", minBlame = "";
		for (String className : updateTimes.keySet()) {

			long avgUpdate = updateTimes.get(className).mean();
			long avgProcess = processTimes.containsKey(className) ? processTimes.get(className).mean() : 0;
			System.out.println(className);
			System.out.println("avgUpdate: " + avgUpdate);
			System.out.println("avgProcess: " + avgProcess);
			if (avgUpdate > 0 && avgProcess > 0 && hasChanged.getOrDefault(className, false)) {
				// How many changes of our type
				int numChanges = changes.stream().mapToInt(c -> c.getClass().getName().equals(className) ? 1 : 0).sum();
				System.out.println("Changes: " + numChanges);

				double position = avgProcess * Math.log(numChanges + 1) + 1; // Calculated Position
				double target = avgUpdate; // Calculated Target
				double error = position - target; // error = process - update
				double change = 0.0000000001 * error; // Adjust rate to scaled error
				System.out.println("rateChange: " + change);

				// Find the highest change
				if (change > maxChange) {
					maxChange = change;
					maxBlame = className;
				}

				// Find the smallest change
				if (change > minChange) {
					minChange = change;
					minBlame = className;
				}

				// We have seen this change
				hasChanged.put(className, false);
			}
		}

		if (maxChange > 0) {
			rate += maxChange;
			System.out.println("rateChange: " + maxChange);
			System.out.println("blame: " + maxBlame);
		} else if (minChange < 0) {
			rate += minChange;
			System.out.println("rateChange: " + minChange);
			System.out.println("blame: " + minBlame);
		}

		// Slowest speed
		if (rate < 0.1)
			rate = 0.1;
		System.out.println("afterRate: " + rate + "\n");
	}

	/**
	 * Stops all update threads
	 */
	private void stopUpdateThreads() {
		if (!alive)
			return;
		alive = false;
		setUpdatePaused(false);
		updateThread.interrupt();// In case of waiting on an empty list
		timer.stop();
		updateTimes.clear();
		processTimes.clear();
		lastUpdates.clear();
		hasChanged.clear();
	}

	/**
	 * @return whether the updates are paused
	 */
	boolean isUpdatePaused() {
		return updatePaused.isPaused();
	}

	/**
	 * Sets whether to pause the update threads. Used for when animating a change.
	 * 
	 * @param paused
	 *            whether updates should be paused
	 */
	void setUpdatePaused(boolean paused) {
		if (paused)
			updatePaused.pause();
		else
			updatePaused.resume();
	}
}
