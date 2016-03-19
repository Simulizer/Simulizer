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
	private volatile boolean updatePaused = false;
	private volatile boolean alive = false;

	public DataStructureVisualiser(DataStructureModel model, HighLevelVisualisation vis) {
		this.model = model;
		this.vis = vis;
		model.addObserver(this);

		widthProperty().addListener(e -> repaint());
	}

	public void show() {
		if (!showing) {
			vis.addTab(this);
			startUpdateThreads();
			showing = true;
		}
	}

	public void hide() {
		if (showing) {
			vis.removeTab(this);
			stopUpdateThreads();
			showing = false;
		}
	}

	public abstract void processChange(ModelAction<?> action);

	public abstract void repaint();

	public abstract String getName();

	public void close() {
		stopUpdateThreads();
		model.deleteObserver(this);
	}

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
			repaint();
		} else if (arg instanceof ModelAction<?>) {
			changes.add((ModelAction<?>) arg);
			for (int i = 1; i < updateTimes.length; i++)
				updateTimes[i - 1] = updateTimes[i];
			long now = System.currentTimeMillis();
			if (lastUpdate != -1)
				updateTimes[updateTimes.length - 1] = (int) (now - lastUpdate);
			lastUpdate = now;
			rateSkips();
		}
	}

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
						if (updatePaused)
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

			// Limit the rate between 1 and 10
			if (newRate > 10)
				newRate = 10;
			else if (newRate < 1)
				newRate = 1;
			rate = newRate + 1;
		}
	}

	private void stopUpdateThreads() {
		if (!alive)
			return;
		alive = false;
		setUpdatePaused(false);
		updateThread.interrupt();// In case of waiting on an empty list
		timer.stop();
	}

	protected boolean isUpdatePaused() {
		return updatePaused;
	}

	protected void setUpdatePaused(boolean paused) {
		if (updatePaused == paused) // No Change
			return;
		else if (!paused) // Resuming
			updateWait.countDown();
		else // Pausing
			updateWait = new CountDownLatch(1);
		updatePaused = paused;
	}
}
