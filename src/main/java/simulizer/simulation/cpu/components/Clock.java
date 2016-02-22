package simulizer.simulation.cpu.components;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**thread for the clock keeping time of the IE cycle
 * 
 * @author Charlie Street
 *
 */
public class Clock extends Thread {

	public int tickMillis;// time for a single clock tick

	private boolean isRunning;//determine if still running
	private long ticks;
	CyclicBarrier barrier;
	int waitForCount; // the number of threads waiting on the barrier
	
	/**constructor sets up field
	 */
	public Clock(int tickMillis)
	{
		this.tickMillis = tickMillis;
		this.isRunning = false;//not running on initial creation
		ticks = 0;
		waitForCount = 2;
		barrier = new CyclicBarrier(waitForCount);
	}

	public void reset() {
		isRunning = false;
		ticks = 0;
		barrier.reset();
	}

	private synchronized void incrementTicks()  {
		ticks++;
	}
	public synchronized long getTicks() {
		return ticks;
	}

	public void waitForNextTick() throws BrokenBarrierException, InterruptedException {
		barrier.await();
	}


	public void startRunning() {
		isRunning = true;
	}

	public void stopRunning() {
		isRunning = false;
		barrier.reset();
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
				lastTickStart = System.currentTimeMillis();
				Thread.sleep(Math.max(tickMillis - overshoot, 0));

				if(!isRunning) {
					break;
				}

				// wait for all others to finish
				waitForNextTick();

				overshoot = System.currentTimeMillis() - lastTickStart - tickMillis;

				incrementTicks();
			}
		} catch (InterruptedException | BrokenBarrierException ignored) {

		}
	}
	
}
