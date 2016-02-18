package simulizer.simulation.cpu.components;

import java.util.concurrent.CountDownLatch;

/**thread for the clock keeping time of the IE cycle
 * 
 * @author Charlie Street
 *
 */
public class Clock extends Thread {

	public int tickMillis;// time for a single clock tick

	private boolean isRunning;//determine if still running
	private long ticks;
	CountDownLatch latch;
	int waitForCount; // the number of threads waiting on the latch
	
	/**constructor sets up field
	 */
	public Clock(int tickMillis)
	{
		this.tickMillis = tickMillis;
		this.isRunning = false;//not running on initial creation
		ticks = 0;
		waitForCount = 2;
	}

	public void reset() {
		isRunning = false;
		ticks = 0;
		latch = null;
	}

	private synchronized void incrementTicks()  {
		ticks++;
	}
	public synchronized long getTicks() {
		return ticks;
	}

	public void waitForNextTick() throws InterruptedException {
		if(latch != null) {
			latch.countDown();
			latch.await();
		}
	}


	public void startRunning() {
		isRunning = true;
	}

	public void stopRunning() {
		isRunning = false;

		for(int i = 0; i < waitForCount; i++) {
			// may become null while doing this
			if(latch != null) {
				latch.countDown();
			}
		}

		latch = null;

	}

	/**run method will run the loop of the clock
	 * 
	 */
	public void run()
	{
		long lastTickStart;
		long overshoot = 0;
		try {
			while(isRunning)
			{
				// 1 for cpu, 1 for the clock.
				// all must be waiting before the clock advances
				latch = new CountDownLatch(waitForCount);
				lastTickStart = System.currentTimeMillis();
				Thread.sleep(Math.max(tickMillis - overshoot, 0));

				// wait for all others to finish
				waitForNextTick();

				overshoot = System.currentTimeMillis() - lastTickStart - tickMillis;

				incrementTicks();
			}
		} catch (InterruptedException ignored) {

		}
	}
	
}
