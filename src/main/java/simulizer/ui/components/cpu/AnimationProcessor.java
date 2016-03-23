package simulizer.ui.components.cpu;

import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Processes animations and queues them up to be run by an executor service
 * @author Theo Styles
 */
public class AnimationProcessor {

	/**
	 * Represents an animation to be run
	 * @author Theo Styles
	 */
    class Animation{
        public int delayFromCycleStart;
        public Runnable job;

        public Animation(int delayFromCycleStart, Runnable job){
            this.delayFromCycleStart = delayFromCycleStart;
            this.job = job;
        }
    }

    private final ScheduledExecutorService executorService;
    private final ScheduledFuture<?> executorTask;
    private final PriorityBlockingQueue<Animation> animationTasks;
	private ArrayList<Animation> animationsForInstruction;
	private long cycleStartTime; // in ms
	private int cycleDelay; // in ms
	private boolean showingWarning;
    public CPUListener cpuListener;
    public CPU cpuVisualisation;

	/**
	 * Sets initial values and sets up the executor service and task
	 * @param cpuVisualisation The cpu visualisation
     */
    public AnimationProcessor(CPU cpuVisualisation){
        this.cpuVisualisation = cpuVisualisation;
		int dispatchInterval = 20;

        cycleStartTime = -1;
        showingWarning = false;

		animationsForInstruction = new ArrayList<>();
		animationTasks = new PriorityBlockingQueue<>(10, (a1, a2) -> Integer.compare(a1.delayFromCycleStart, a2.delayFromCycleStart));
        executorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadUtils.NamedThreadFactory("CPU-Visualisation-Job-Dispatch"));
        executorTask = executorService.scheduleAtFixedRate(
				this::dispatchAnimationJobs, dispatchInterval, dispatchInterval, TimeUnit.MILLISECONDS);
    }

	/**
	 * Run each time there is a new cycle, resets the cycleDelay and sets the cycleStartTime
	 * also clears the tasks if there are any left
	 */
	public synchronized void newCycle() {
		cycleDelay = 0;
		cycleStartTime = System.currentTimeMillis();
		animationsForInstruction.clear();
		if (!animationTasks.isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				UIUtils.showExceptionDialog(e);
			}
			animationTasks.clear();
		}
	}

	/**
	 * Processes animations jobs
	 */
	private synchronized void dispatchAnimationJobs() {
			if (!animationTasks.isEmpty()) {
				// Are there animation jobs?
				// Is the clock speed too fast? If so show the warning label
				if (cpuListener.getSimCpu().getCycleFreq() > 2) {
					animationTasks.clear();
					if (!showingWarning) {
						cpuVisualisation.showText("Please lower the clock speed to less than 2Hz to see animations", 1000, false);
						showingWarning = true;
					}
					return;
				}

				showingWarning = false;

				if (animationTasks.size() > 15) {
					// Too many animations, remove the lower priority ones
					ArrayList<Animation> tmp = new ArrayList<>(15);
					animationTasks.drainTo(tmp, 15);
					animationTasks.clear();
					animationTasks.addAll(tmp);
				}

				// Updates the time into the cycle
				long timeIntoCycle = System.currentTimeMillis() - cycleStartTime;

				// Runs the next task when it should be run (its absolute time is lower or equal to the time into the cycle)
				if (animationTasks.peek().delayFromCycleStart <= timeIntoCycle) {
					animationTasks.poll().job.run();
				}
			}
	}

	/**
	 * Sets the cpu listener
	 * @param cpuListener The cpu listener
     */
    public void setCpuListener(CPUListener cpuListener){
        this.cpuListener = cpuListener;
    }

	/**
	 * Shuts down the executor task and service
	 */
    public void shutdown() {
		executorTask.cancel(true);
        executorService.shutdownNow();
    }

	/**
	 * Schedule several animation tasks with a fixed delay in between, and add to the previous instruction list
	 * @param delay The speed of each animation
	 * @param jobs The animations to run
	 */
	public synchronized void scheduleRegularAnimations(int delay, Runnable... jobs) {
		for(Runnable r : jobs) {
			Animation animation = new Animation(cycleDelay, r);
			animationTasks.add(animation);
			animationsForInstruction.add(animation);
			// Add to the overall cycleDelay (reset on each cycle)
			cycleDelay += delay;
		}
	}

	/**
	 * Adds to the previous list of instructions
	 * @param instructionName The name of the instruction
	 */
	public synchronized void addToPreviousList(String instructionName){
		// Add to the list of previous instructions
		ArrayList<Animation> animations = new ArrayList<>();
		animationsForInstruction.forEach(item -> animations.add(item));
		if (!showingWarning) cpuVisualisation.previousInstructions.addInstruction(instructionName, animations);
	}

	/**
	 * Replays animations
	 * @param animations The animations to replay
     */
	public synchronized void replayAnimations(ArrayList<Animation> animations){
		// Start a new cycle to get the timings right
		newCycle();
		animationTasks.addAll(animations);
	}
}