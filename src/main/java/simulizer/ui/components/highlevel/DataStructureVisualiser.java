package simulizer.ui.components.highlevel;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

import javafx.animation.AnimationTimer;
import javafx.scene.layout.Pane;
import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;

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

	private int FRAME_RATE = 45;
	private AnimationTimer timer;

	protected volatile double rate = 1;
	private int[] updateTimes = { -1 }, processTimes = { -1 };
	private long lastUpdate = -1;

	private Thread updateThread;
	private CountDownLatch updateWait;
	private Boolean updatePaused = false;
	private volatile boolean alive = false;

	/**
	 * @param model
	 *            the model to visualise
	 * @param vis
	 *            the high level visualisation window
	 */
	public DataStructureVisualiser(DataStructureModel model, HighLevelVisualisation vis) {
		this.model = model;
		this.vis = vis;
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
	public void hide() {
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
			changes.add((ModelAction<?>) arg);
			for (int i = 1; i < updateTimes.length; i++)
				updateTimes[i - 1] = updateTimes[i];
			long now = System.currentTimeMillis();
			if (lastUpdate != -1)
				updateTimes[updateTimes.length - 1] = (int) (now - lastUpdate);
			lastUpdate = now;
		}
	}

	/**
	 * Calculates the average time from a list
	 * 
	 * @param times
	 *            the times to average
	 * @return the average time
	 */
	private int averageTime(int[] times) {
		int totalTime = 0;
		int numTimes = 0;

		for (int i = times.length - 1; i >= 0; i--) {
			if (times[i] < 0)
				return -1;
			totalTime += times[i];
			numTimes++;
		}

		return totalTime / numTimes;
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
					if (alive) {
						ModelAction<?> change = changes.take();
						before = System.currentTimeMillis();
						processChange(change);

						// Wait if paused
						boolean hold = false;
						synchronized (updatePaused) {
							hold = updatePaused;
						}
						if (hold)
							updateWait.await();

						after = System.currentTimeMillis();
					}

					if (alive && before != -1 && after != -1) {
						// Add process time
						for (int i = 1; i < processTimes.length; i++)
							processTimes[i - 1] = processTimes[i];
						processTimes[processTimes.length - 1] = (int) (after - before);

						// Recalculate rate/skips
						rateSkips();
					}
				} catch (InterruptedException e) {
				}
			}
		} , "DataStructure " + getName() + " Updates");
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
		int avgUpdate = averageTime(updateTimes);
		int avgProcess = averageTime(processTimes);
		if (avgUpdate > 0 && avgProcess > 0) {
			double newRate = rate - 1;
			// Averages are roughly the same, do nothing
			if (avgUpdate + 50 > avgProcess && avgUpdate < avgProcess + 50)
				return;

			// Averages are too far apart, calculate a new rate
			newRate += (avgProcess - avgUpdate) / (avgProcess * newRate);

			// Limit the rate between 1 and 20
			if (newRate > 20)
				newRate = 20;
			else if (newRate < 1)
				newRate = 1;
			rate = newRate + 1;
		}
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
	}

	/**
	 * @return whether the updates are paused
	 */
	protected boolean isUpdatePaused() {
		return updatePaused;
	}

	/**
	 * Sets whether to pause the update threads. Used for when animating a change.
	 * 
	 * @param paused
	 *            whether updates should be paused
	 */
	protected void setUpdatePaused(boolean paused) {
		synchronized (updatePaused) {
			if (updatePaused == paused) // No Change
				return;
			else if (!paused) // Resuming
				updateWait.countDown();
			else // Pausing
				updateWait = new CountDownLatch(1);
			updatePaused = paused;
		}
	}
}
