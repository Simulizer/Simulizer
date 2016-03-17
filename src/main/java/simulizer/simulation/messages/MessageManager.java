package simulizer.simulation.messages;

import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Manages a thread which processes messages sent from the simulation
 * @author Charlie Street
 */
public class MessageManager {

	private final long allowedProcessingTime = 100; // milliseconds

	private final List<SimulationListener> listeners;
	private final ThreadPoolExecutor executor;
	private final List<Future<Void>> tasks;
	private final static int maxTasks = 8;

	private final IO io;

	public MessageManager(IO io) {
		listeners = new ArrayList<>(maxTasks);
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxTasks,
				new ThreadUtils.NamedThreadFactory("Message-Manager"));

		// if the executor runs out of threads, the calling thread to submit the tasks has to run them
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		tasks = new ArrayList<>(maxTasks);
		this.io = io;
	}

	public void shutdown() {
		waitForAllRunningTasks();

		synchronized (executor) {
			executor.shutdown();
			try {
				executor.awaitTermination(allowedProcessingTime, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				executor.shutdownNow();
			}
		}
	}

	/**
     * Register a listener to receive messages
     * @param l the listener to send messages to
     */
    public void registerListener(SimulationListener l) {
		synchronized (listeners) {
			listeners.add(l);
		}
    }

    /**
     * Unregisters a listener from the list
     * @param l the listener to be removed
     */
    public void unregisterListener(SimulationListener l){
		synchronized (listeners) {
			listeners.remove(l);
		}
    }

	/**
	 * send a message to all of the registered listeners
	 * @param m the message to send
	 */
	public synchronized void sendMessage(Message m) {
		List<Callable<Void>> jobs = new ArrayList<>(listeners.size());

		synchronized (listeners) {
			for (SimulationListener l : listeners) {
				jobs.add(() -> {
					l.delegateMessage(m);
					return null;
				});
			}
		}
		try {
			List<Future<Void>> newTasks;

			// make sure not to add any more tasks than the executor can cope with

			// make sure not to add tasks after shutdown
			// to avoid java.util.concurrent.RejectedExecutionException
			synchronized (executor) {
				if(executor.isShutdown()) return;
				newTasks = executor.invokeAll(jobs);
			}

			synchronized (tasks) {
				tasks.addAll(newTasks);
			}
		} catch (InterruptedException e) {
			UIUtils.showExceptionDialog(e);
		}

	}

	public void waitForCrucialTasks() {
		synchronized (tasks) {
			tasks.removeIf(Future::isDone);
		}

		for(SimulationListener l : listeners) {
			if(l.criticalProcesses.intValue() != 0) {

				try {
					synchronized (l.criticalProcesses) {
						l.criticalProcesses.wait(allowedProcessingTime); // wait to be notified of any change
					}
				} catch (InterruptedException e) {
					UIUtils.showExceptionDialog(e);
				}


				if(l.criticalProcesses.intValue() != 0) {
					io.printString(IOStream.ERROR,
							"A simulation message is taking too long to process.\n" +
							"  The simulation will continue without waiting."
					);
				}

				l.criticalProcesses.reset();
			}
		}
	}

	public void waitForAllRunningTasks() {
		synchronized (tasks) {
			for (Future<Void> t : tasks) {
				if (!t.isDone()) {
					try {
						t.get(allowedProcessingTime, TimeUnit.MILLISECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException e) {
						UIUtils.showExceptionDialog(e);
					}
					t.cancel(true);
				}
			}

			tasks.clear();
		}
	}

}
