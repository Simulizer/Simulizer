package simulizer.simulation.cpu.components;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;

/**thread for the clock keeping time of the IE cycle
 * 
 * @author Charlie Street
 *
 */
public class Clock extends Thread {

	public static long ClockSpeedMillis = 100;//clock speed in milliseconds
	
	private boolean isRunning;//determine if still running
	private long ticks;
	private CPU cpu;//cpu object being run
	CountDownLatch latch;
	
	/**constructor sets up field
	 * @param cpu the cpu being run
	 */
	public Clock(CPU cpu)
	{
		this.isRunning = false;//not running on initial creation
		ticks = 0;
		this.cpu = cpu;
	}

	private synchronized void incrementTicks()  {
		ticks++;
	}
	public synchronized long getTicks() {
		return ticks;
	}

	public void waitForNextTick() throws InterruptedException {
		latch.countDown();
		latch.await();
	}


	
	/**run method will run the loop of the clock
	 * 
	 */
	public void run()
	{
		while(isRunning)
		{
			try {
				// 1 for cpu, 1 for the clock.
				// all must be waiting before the clock advances
				latch = new CountDownLatch(2);
				Thread.sleep(ClockSpeedMillis);

				latch.countDown();
				latch.await(); // wait for all things to finish their cycle

				incrementTicks();
			} catch (InterruptedException e) {
				//this shouldn't happen, only two threads where nothing will interrupt the clock
				System.out.println("Clock has been interrupted");
				e.printStackTrace();
			}
		}
	}
	
	/**this method sets the clock's running state
	 * 
	 * @param runningState the running state of the clock
	 */
	public synchronized void setClockRunning(boolean runningState)
	{
		this.isRunning = runningState;
	}
	
}
