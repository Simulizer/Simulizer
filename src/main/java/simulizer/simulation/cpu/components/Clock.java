package simulizer.simulation.cpu.components;

import simulizer.utils.ThreadUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * thread for the clock keeping time of the IE cycle
 * 
 * @author mbway
 *
 */
public class Clock {

	/**
	 * the duration of a single clock tick in microseconds (10^-6 seconds)
	 */
	private long tickPeriod;
	/**
     * Whether the clock is 'fast'. Indicates to listeners that they might want
     * to drop some messages
	 */
	private boolean highSpeed;

	private final LongAdder ticks;
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> ticker;

	/**
     * Below a certain threshold the time taken to handle the
     * clock may become significant and decrease performance
	 * value in microseconds (1000 = 1 millisecond)
	 */
	private static final long MIN_PERIOD = 100;

	// 10 seconds
	private static final long MAX_PERIOD = 1000 * 1000 * 10;

	/**
	 * a delay <= this value is considered to be 'high speed'
	 */
	private static final long HIGH_SPEED_THRESHOLD = 1000;



	public Clock() {
		this.tickPeriod = MIN_PERIOD;
		ticks = new LongAdder();
		executor = Executors.newSingleThreadScheduledExecutor(
				new ThreadUtils.NamedThreadFactory("Clock"));
		ticker = null;
	}


	public void setTickFrequency(double freq) {
		if(freq < 0.00001) {
			// practically zero => max speed
			setTickPeriod(0);
		} else {
			setTickPeriod((long) (1000000 / freq));
		}
	}
	public double getTickFrequency() {
		return 1000000.0 / tickPeriod;
	}

	private synchronized void setTickPeriod(long requestedPeriod) {
		if(tickPeriod == requestedPeriod) return;

		tickPeriod = Math.min(Math.max(requestedPeriod, MIN_PERIOD), MAX_PERIOD);
		highSpeed = tickPeriod <= HIGH_SPEED_THRESHOLD;

		if(isRunning()) {
			start(); // stops current ticker and makes a new one
		}
	}

	public boolean isHighSpeed() {
		return highSpeed;
	}

	public long getTicks() {
		return ticks.longValue();
	}

	void resetTicks() {
		ticks.reset();
	}

	public void waitForNextTick() throws InterruptedException {
		// no point waiting, just carry on
		if (tickPeriod <= MIN_PERIOD) {
			return;
		}

		synchronized (ticks) {
			ticks.wait();
		}
	}

	private void incrementTicks() {
		synchronized (ticks) {
			ticks.increment();
			ticks.notifyAll();
		}
	}

	synchronized void start() {
		if (executor.isShutdown()) {
			throw new IllegalStateException();
		}
		stop();

		ticker = executor.scheduleAtFixedRate(this::incrementTicks, 0, tickPeriod, TimeUnit.MICROSECONDS);
	}

	synchronized void stop() {
		if (isRunning()) {
			ticker.cancel(true); // may interrupt if running
			ticker = null;
		}
		// let any threads waiting on the clock fall through
		synchronized (ticks) {
			// one should be sufficient but this is to be safe
			for(int i = 0; i < 5; i++) {
				ticks.notifyAll();
				try {
					Thread.sleep(10);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	synchronized void shutdown() {
		stop();
		executor.shutdown();
		try {
			executor.awaitTermination(3, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}

	public synchronized boolean isRunning() {
		return !executor.isShutdown() && ticker != null && !ticker.isCancelled();
	}
	public synchronized boolean isPaused() {
		return !executor.isShutdown() && (ticker == null || ticker.isCancelled());
	}
}
