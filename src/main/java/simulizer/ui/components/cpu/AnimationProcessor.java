package simulizer.ui.components.cpu;

import javafx.application.Platform;
import javafx.concurrent.Task;
import simulizer.assembler.representation.Instruction;
import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.ui.windows.help.SyscallReference;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

import java.util.ArrayList;
import java.util.concurrent.*;

public class AnimationProcessor {

    class Animation{
        public int absoluteTime;
		public int relativeTime;
        public Runnable job;

        public Animation(int absoluteTime, int relativeTime, Runnable job){
            this.absoluteTime = absoluteTime;
			this.relativeTime = relativeTime;
            this.job = job;
        }

    }


    private final ScheduledExecutorService executorService;
    private final ScheduledFuture<?> executorTask;
    private final PriorityBlockingQueue<Animation> animationTasks;
	private long cycleStartTime; // in ms
	private int dispatchInterval; // in ms
	private int cycleDelay; // in ms

    public CPUListener cpuListener;
    public CPU cpuVisualisation;

    public boolean showingWarning;

    public AnimationProcessor(CPU cpuVisualisation){
        this.cpuVisualisation = cpuVisualisation;

        dispatchInterval = 20;
        cycleStartTime = -1;
        showingWarning = false;

		animationTasks = new PriorityBlockingQueue<>(10, (a1, a2) -> Integer.compare(a1.absoluteTime, a2.absoluteTime));
        executorService = Executors.newSingleThreadScheduledExecutor(
                new ThreadUtils.NamedThreadFactory("CPU-Visualisation-Job-Dispatch"));
        executorTask = executorService.scheduleAtFixedRate(
				this::dispatchAnimationJobs, dispatchInterval, dispatchInterval, TimeUnit.MILLISECONDS);

    }

	public synchronized void newCycle() {
		cycleDelay = 0;
		cycleStartTime = System.currentTimeMillis();
		if(!animationTasks.isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				UIUtils.showExceptionDialog(e);
			}
			animationTasks.clear();
		}
	}

	private void dispatchAnimationJobs() {
		if(!animationTasks.isEmpty()) {
			// Are there animation jobs?
			if(cpuListener.getSimCpu().getCycleFreq() > 2) {
				animationTasks.clear();
				if(!showingWarning) {
					cpuVisualisation.showText("Please lower the clock speed to less than 2Hz to see animations", 1000, false);
					showingWarning = true;
				}
				return;
			}

			showingWarning = false;
			synchronized (animationTasks) {
				if (animationTasks.size() > 10) {
					// too many animations, remove the lower priority ones
					ArrayList<Animation> tmp = new ArrayList<>(10);
					animationTasks.drainTo(tmp, 10);
					animationTasks.clear();
					animationTasks.addAll(tmp);
				}

				long timeIntoCycle = System.currentTimeMillis() - cycleStartTime;

				if (animationTasks.peek().absoluteTime <= timeIntoCycle) {
					System.out.println("running job at " + timeIntoCycle);
					animationTasks.poll().job.run();
				}
			}
		}
	}

    public void setCpuListener(CPUListener cpuListener){
        this.cpuListener = cpuListener;
    }

    public void shutdown() {
		executorTask.cancel(true);
        executorService.shutdownNow();
    }

	public void scheduleRegularAnimations(int delay, Runnable... jobs) {
		int thisDelay = 0;
		for(Runnable r : jobs) {
			animationTasks.add(new Animation(cycleDelay, thisDelay, r));
			System.out.println("Adding " + cycleDelay);
			cycleDelay += delay;
			thisDelay += delay;
		}
	}

	/**
	 * Schedule several animation tasks with a fixed delay in between
	 */
	public void scheduleRegularAnimations(String instructionName, int delay, Runnable... jobs) {
		int thisDelay = 0;
		ArrayList<Animation> animationsForInstruction = new ArrayList<>();
		for(Runnable r : jobs) {
			Animation animation = new Animation(cycleDelay, thisDelay, r);
			animationTasks.add(animation);
			animationsForInstruction.add(animation);
			System.out.println("Adding " + instructionName + " " + cycleDelay);
			cycleDelay += delay;
			thisDelay += delay;
		}
		cpuVisualisation.previousInstructions.addInstruction(instructionName, animationsForInstruction);
	}

	public void replayAnimations(ArrayList<Animation> animations){
		newCycle();
		for (Animation a : animations){
			a.absoluteTime = a.relativeTime;
		}
		animationTasks.addAll(animations);
	}
}
