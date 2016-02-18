package simulizer.simulation.cpu.components;

/**thread for the clock keeping time of the IE cycle
 * 
 * @author Charlie Street
 *
 */
public class Clock extends Thread {

	public static long ClockSpeedMillis = 100;//clock speed in milliseconds
	
	private boolean isRunning;//determine if still running
	private CPU cpu;//cpu object being run
	
	/**constructor sets up field
	 * @param cpu the cpu being run
	 */
	public Clock(CPU cpu)
	{
		this.isRunning = false;//not running on initial creation
		this.cpu = cpu;
	}
	
	/**run method will run the loop of the clock
	 * 
	 */
	public void run()
	{
		while(isRunning)
		{
			this.cpu.setWait(false);//allowing continuation
			try {
				Thread.sleep(ClockSpeedMillis);
			} catch (InterruptedException e) {//this shouldn't happen, only two threads where nothing will interrupt the clock
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
