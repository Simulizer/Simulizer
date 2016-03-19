package simulizer.simulation.messages;

import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a thread which processes messages sent from the simulation
 * @author Charlie Street
 */
public class MessageManager {

	private final long allowedProcessingTime = 200; // milliseconds

	private final CopyOnWriteArrayList<SimulationListener> listeners;
	private final ThreadPoolExecutor executor;
	private final BlockingQueue<Message> messages;
	private final AtomicBoolean noWaitingMessages; // all messages submitted
	private final List<Future<Void>> tasks;
	private final Future<?> dispatchTask;
	private final static int maxTasks = 12;

	private final IO io;

	public MessageManager(IO io) {
		listeners = new CopyOnWriteArrayList<>();
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxTasks+1,
				new ThreadUtils.NamedThreadFactory("Message-Manager"));

		// one of the threads is dedicated to dispatching to the others
		messages = new LinkedBlockingQueue<>();
		noWaitingMessages = new AtomicBoolean(true);
		dispatchTask = executor.submit((Runnable) this::processMessageQueue);

		// if the executor runs out of threads, the calling thread to submit the tasks has to run them
		// this essentially means the list of waiting messages can grow indefinitely, as the dispatching
		// thread will have to process some of the messages itsself
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		tasks = new ArrayList<>(maxTasks);
		this.io = io;
	}

	public void shutdown() {
		synchronized (executor) {
			messages.clear();

			dispatchTask.cancel(true);
			synchronized (tasks) {
				for (Future<Void> t : tasks)
					t.cancel(true);
			}

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
		listeners.add(l);
    }

    /**
     * Unregisters a listener from the list
     * @param l the listener to be removed
     */
    public void unregisterListener(SimulationListener l){
		listeners.remove(l);
    }

	public void sendMessage(Message m) {
		try {
			noWaitingMessages.set(false);
			messages.put(m);
		} catch (InterruptedException ignored) {
		}
	}

	/**
	 * send messages to all of the registered listeners
	 */
	private void processMessageQueue() {
		for(;;) {

			Message m;
			try {
				m = messages.poll(10, TimeUnit.MILLISECONDS); // wait until one becomes available
				if(executor.isShutdown())
					return;
				if(m == null)
					continue;
				synchronized (messages) {
					messages.notifyAll();
				}
			} catch (InterruptedException ignored) {
				return;
			}

			synchronized (this) {
				// make sure not to add tasks after shutdown
				// to avoid java.util.concurrent.RejectedExecutionException
				if (executor.isShutdown())
					return;
				synchronized (tasks) {
					tasks.add(executor.submit(() -> {
						try {
							for (SimulationListener l : listeners) {
								l.delegateMessage(m);
							}
						} catch (Throwable t) {
							UIUtils.showExceptionDialog(t);
						}
						return null;
					}));
				}
				if(messages.isEmpty()) {
					synchronized (noWaitingMessages) {
						noWaitingMessages.set(true);
						noWaitingMessages.notifyAll();
					}
				}
			}
		}
	}

	public void waitForCrucialTasks() {
		waitForAllRunningTasks(allowedProcessingTime);
		/*
		synchronized (tasks) {
			tasks.removeIf(Future::isDone);
		}

		try {
			synchronized (noWaitingMessages) {
				while (!noWaitingMessages.get()) {
					noWaitingMessages.wait();
				}
			}
		} catch (InterruptedException ignored) {
			return;
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
							"  The simulation will continue without waiting.\n"
					);
				}

				l.criticalProcesses.reset();
			}
		}
		*/
	}

	public void waitForAllRunningTasks() {
		waitForAllRunningTasks(allowedProcessingTime);
	}
	public void waitForAllRunningTasks(long timeoutTime) {
		synchronized (tasks) {
			tasks.removeIf(Future::isDone);
		}

		try {
			synchronized (noWaitingMessages) {
				while (!noWaitingMessages.get()) {
					noWaitingMessages.wait(timeoutTime);
				}
			}
		} catch (InterruptedException ignored) {
			return;
		}

		synchronized (tasks) {
			for (Future<Void> t : tasks) {
				if (!t.isDone()) {
					try {
						t.get(timeoutTime, TimeUnit.MILLISECONDS);
					} catch (InterruptedException | ExecutionException | TimeoutException ignored) {
						io.printString(IOStream.ERROR,
								"A simulation message is taking too long to process.\n" +
								"  The simulation will continue without waiting.\n"
						);
					}
					t.cancel(true);
				}
			}

			tasks.clear();
		}
	}

}
