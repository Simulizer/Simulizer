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
	private volatile boolean updating = true;
	protected int rate = 1;
	private int FRAME_RATE = 45;
	private Thread updateThread;
	private CountDownLatch updateRunning;
	private AnimationTimer timer = new AnimationTimer() {
		long lastTime = -1;

		@Override
		public void handle(long now) {
			if (lastTime == -1 || now - lastTime > 1e9 / FRAME_RATE) {
				lastTime = now;
				repaint();
			}
		}
	};

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
			
			// Start Threads
			startUpdateThread();
		}
		showing = true;
	}

	public void hide() {
		if (showing) {
			vis.removeTab(this);
		}
		showing = false;
	}

	public abstract void processChange(ModelAction<?> action);

	public abstract void repaint();

	public abstract String getName();

	public void close() {
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
			synchronized (changes) {
				changes.add((ModelAction<?>) arg);
			}
		}
	}

	private void startUpdateThread() {
		updateThread = new Thread(() -> {
			while (showing) {
				// Wait until we are updating
				if (!updating) {
					try {
						updateRunning.await();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				ModelAction<?> action = null;
				synchronized (changes) {
					action = changes.poll();
				}
				processChange(action);
			}
		} , "DataStructureUpdate");
		updateThread.setDaemon(true);
		updateThread.start();
	}

	protected boolean isUpdatePaused() {
		return updating;
	}

	protected void setUpdatePaused(boolean paused) {
		if (updating == !paused) // No Change
			return;
		else if (!paused) // Resuming
			updateRunning.countDown();
		else // Pausing
			updateRunning = new CountDownLatch(1);
		updating = paused;
	}
}
