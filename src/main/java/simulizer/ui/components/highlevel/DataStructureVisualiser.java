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

	protected volatile int rate = 1;
	private long[] updateTimes = { -1, -1, -1 }, processTimes = { -1, -1, -1 };

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
			for (int i = 1; i < updateTimes.length - 1; i++)
				updateTimes[i - 1] = updateTimes[i];
			updateTimes[updateTimes.length - 1] = System.currentTimeMillis();
			rateSkips();
		}
	}

	private long averageTime(long[] times) {
		boolean containsNegative = false;

		if (containsNegative)
			return -1L;

		return 0L;
	}

	private void startUpdateThreads() {
		if (alive)
			return;
		alive = true;
		updateThread = new Thread(() -> {
			while (alive) {
				try {
					// Wait if paused
					if (updatePaused)
						updateWait.await();

					// Process change
					if (alive)
						processChange(changes.take());

					// Add process time
					for (int i = 1; i < processTimes.length - 1; i++)
						processTimes[i - 1] = processTimes[i];
					processTimes[processTimes.length - 1] = System.currentTimeMillis();

					// Recalculate rate/skips
					rateSkips();
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
		long avgUpdate = averageTime(updateTimes);
		long avgProcess = averageTime(processTimes);
		if (avgUpdate != -1 && avgProcess != -1) {
			int newRate = rate;
			if (avgUpdate + 3 > avgProcess && avgUpdate < avgProcess + 3) {
				// Averages are roughly the same, do nothing
			} else if (avgUpdate < avgProcess) {
				// Average update is faster, increase rate
				newRate += 0.2;
			} else {
				// Average update is slower, decrease rate
				newRate -= 0.2;
			}

			// Limit the rate between 1 and 10
			if (newRate > 10)
				newRate = 10;
			else if (newRate < 1)
				newRate = 1;
			rate = newRate;
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
