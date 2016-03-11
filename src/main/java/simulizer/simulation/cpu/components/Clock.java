package simulizer.simulation.cpu.components;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

/**
 * thread for the clock keeping time of the IE cycle
 * 
 * @author Charlie Street
 *
 */
public class Clock {

	/**
	 * the duration of a single clock tick in microseconds (10^-6 seconds)
	 */
	private long tickPeriod;

	private final LongAdder ticks;
	private ScheduledExecutorService executor;
	private ScheduledFuture<?> ticker;

	/**
     * Untested, but below a certain threshold the time taken to handle the
     * clock may become significant and decrease performance (setting to 1ns
     * would definitely be sub-optimal for example)
	 */
	private static final long MIN_PERIOD = 50;



	public Clock() {
		this.tickPeriod = MIN_PERIOD;
		ticks = new LongAdder();
		executor = Executors.newSingleThreadScheduledExecutor();
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

	private synchronized void setTickPeriod(long tickPeriod) {
		this.tickPeriod = Math.max(tickPeriod, MIN_PERIOD);

		if(isRunning()) {
			start(); // stops current ticker and makes a new one
		}
	}


	public long getTicks() {
		return ticks.longValue();
	}

	public void resetTicks() {
		ticks.reset();
	}

	public void waitForNextTick() throws InterruptedException {
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

	public synchronized void start() {
		if (executor.isShutdown()) {
			throw new IllegalStateException();
		}
		stop();

		ticker = executor.scheduleAtFixedRate(this::incrementTicks, 0, tickPeriod, TimeUnit.MICROSECONDS);
	}

	public synchronized void stop() {
		if (isRunning()) {
			ticker.cancel(true); // may interrupt if running
			ticker = null;
		}
		// let any threads waiting on the clock fall through
		synchronized (ticks) {
			ticks.notifyAll();
		}
	}

	public synchronized void shutdown() {
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
}
