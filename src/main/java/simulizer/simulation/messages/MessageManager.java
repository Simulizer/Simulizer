package simulizer.simulation.messages;

import simulizer.Simulizer;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.simulation.cpu.user_interaction.IOStream;
import simulizer.utils.ThreadUtils;
import simulizer.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Manages a thread which processes messages sent from the simulation
 * @author Charlie Street
 */
public class MessageManager {

	private final static long allowedProcessingTime = 1000; // milliseconds

	private final CopyOnWriteArrayList<SimulationListener> listeners;
	private final ThreadPoolExecutor executor;
	private final ThreadUtils.NamedTaggedThreadFactory threadFactory;
	private final BlockingQueue<Message> messages;
	private final AtomicBoolean noWaitingMessages; // all messages submitted
	private final List<MessageTask> tasks;
	private final static int maxTasks = 12;

	private final IO io;

	public MessageManager(IO io) {
		listeners = new CopyOnWriteArrayList<>();
		threadFactory = new ThreadUtils.NamedTaggedThreadFactory("Message-Manager");
		executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(maxTasks+1, threadFactory);


		// one of the threads is dedicated to dispatching to the others
		messages = new LinkedBlockingQueue<>();
		noWaitingMessages = new AtomicBoolean(true);
		executor.submit((Runnable) this::processMessageQueue);

		// if the executor runs out of threads, the calling thread to submit the tasks has to run them
		// this essentially means the list of waiting messages can grow indefinitely, as the dispatching
		// thread will have to process some of the messages itsself
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

		tasks = new ArrayList<>(maxTasks);
		this.io = io;
	}

	public void shutdown() {
		synchronized (executor) {
			threadFactory.killThreads();
			messages.clear();
			tasks.clear();

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

	private class MessageTask implements Runnable {
		public Message m;
		public Future<?> future;

		public MessageTask(Message m) {
			this.m = m;
		}


		@Override
		public void run() {
			try {
				for (SimulationListener l : listeners) {
					l.delegateMessage(m);
				}
			} catch (Exception e) {
				Simulizer.handleException(e);
			}
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
					MessageTask t = new MessageTask(m);
					t.future = executor.submit(t);
					tasks.add(t);

					if(messages.isEmpty()) {
						synchronized (noWaitingMessages) {
							noWaitingMessages.set(true);
							noWaitingMessages.notifyAll();
						}
					}

					if(tasks.size() > 50) {
						tasks.removeIf((task) -> task.future.isDone());
					}
				}
			}
		}
	}

	public void waitForAll() {
		waitForAll(allowedProcessingTime);
	}
	public void waitForAll(long timeoutTime) {
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
			for (MessageTask t : tasks) {
				try {
					if(!t.future.isDone()) {
						t.future.get(timeoutTime, TimeUnit.MILLISECONDS);
					}
				} catch (InterruptedException e) {
					return;
				} catch (ExecutionException | TimeoutException e) {
					io.printString(IOStream.ERROR, "" +
							"A simulation message is taking too long to process.\n" +
							"  The simulation will continue without waiting.\n" +
							"  Detail: " + t.m + "\n"
					);
				}
			}

			tasks.clear();
		}
	}

}
