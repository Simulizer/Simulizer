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
	private final ExecutorService executor;
	private final List<Future<Void>> tasks;

	private final IO io;

	public MessageManager(IO io) {
		int expectedListeners = 5; // to preallocate

		listeners = new ArrayList<>(expectedListeners);
		executor = Executors.newFixedThreadPool(expectedListeners, new ThreadUtils.NamedThreadFactory("Message-Manager"));
		tasks = new ArrayList<>(expectedListeners);
		this.io = io;
	}

	public void shutdown() {
		waitForAllRunningTasks();

		executor.shutdown();
		try {
			executor.awaitTermination(allowedProcessingTime, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			executor.shutdownNow();
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
		List<Callable<Void>> jobs = new ArrayList<>();

		synchronized (listeners) {
			for (SimulationListener l : listeners) {
				jobs.add(() -> {
					l.delegateMessage(m);
					return null;
				});
			}
		}
		try {
			List<Future<Void>> newTasks = executor.invokeAll(jobs);
			tasks.addAll(newTasks);
		} catch (InterruptedException e) {
			UIUtils.showExceptionDialog(e);
		}

	}

	public void waitForCrucialTasks() {
		tasks.removeIf(Future::isDone);

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
		for(Future t : tasks) {
			if(!t.isDone()) {
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
