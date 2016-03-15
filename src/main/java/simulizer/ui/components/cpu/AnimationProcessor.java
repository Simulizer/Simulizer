package simulizer.ui.components.cpu;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

public class AnimationProcessor {

	class Animation {
		public int time;
		public Runnable job;

		public Animation(int time, Runnable job) {
			this.time = time;
			this.job = job;
		}

	}

	private final ScheduledExecutorService executorService;
	private final ScheduledFuture<?> executorTask;
	private final PriorityBlockingQueue<Animation> animationTasks;
	private long cycleStartTime; // in ms
	private int dispatchInterval; // in ms

	public CPUListener cpuListener;
	public CPU cpuVisualisation;

	public boolean showingWarning;

	public AnimationProcessor(CPU cpuVisualisation) {
		this.cpuVisualisation = cpuVisualisation;

		dispatchInterval = 20;
		cycleStartTime = -1;

		showingWarning = false;

		animationTasks = new PriorityBlockingQueue<>(10, (a1, a2) -> Integer.compare(a1.time, a2.time));
		executorService = Executors.newSingleThreadScheduledExecutor(new ThreadUtils.NamedThreadFactory("CPU-Visualisation-Job-Dispatch"));
		executorTask = executorService.scheduleAtFixedRate(this::dispatchAnimationJobs, dispatchInterval, dispatchInterval, TimeUnit.MILLISECONDS);

	}

	public synchronized void newCycle() {
		cycleStartTime = System.currentTimeMillis();
		if (!animationTasks.isEmpty()) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				UIUtils.showExceptionDialog(e);
			}
			animationTasks.clear();
		}
	}

	private void dispatchAnimationJobs() {
		if (!animationTasks.isEmpty()) {
			// Are there animation jobs?
			if (cpuListener.getSimCpu().getCycleFreq() > 2) {
				animationTasks.clear();
				if (!showingWarning) {
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

				while (animationTasks.peek().time <= timeIntoCycle) {
					System.out.println("running job at " + timeIntoCycle);
					animationTasks.poll().job.run();
				}
			}
		}
	}

	public void setCpuListener(CPUListener cpuListener) {
		this.cpuListener = cpuListener;
	}

	public void shutdown() {
		executorTask.cancel(true);
		executorService.shutdownNow();
	}

	public void addAnimationTask(Runnable job, int delay) {
		animationTasks.add(new Animation(delay, job));
	}

	/**
	 * Schedule several animation tasks with a fixed delay in between
	 */
	public void scheduleRegularAnimations(int delay, Runnable... jobs) {
		int thisDelay = 0;
		for (Runnable r : jobs) {
			addAnimationTask(r, thisDelay);
			thisDelay += delay;
		}
	}
}
