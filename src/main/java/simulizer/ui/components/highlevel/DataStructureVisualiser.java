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

	/**
	 * Sets the rate of the animation in milliseconds
	 *
	 * @param rate
	 *            the rate of the animation
	 */
	public void setRate(int rate) {
		this.rate = rate;
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
		System.out.println("Closed");
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
		}
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
