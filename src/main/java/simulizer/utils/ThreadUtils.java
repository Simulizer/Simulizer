package simulizer.utils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javafx.application.Platform;

/**
 * Utility functions for dealing with threads
 */
public class ThreadUtils {

	/**
	 * like Platform.runLater but waits until the thread has finished
	 * based on: http://www.guigarage.com/2013/01/invokeandwait-for-javafx/
	 * @param r the runnable to run in a JavaFX thread
	 */
	public static void platformRunAndWait(final Runnable r) throws Throwable {
		if(Platform.isFxApplicationThread()) {
			try {
				r.run();
			} catch(Exception e) {
				throw new ExecutionException(e);
			}
		} else {
			final Lock lock = new ReentrantLock();
			final Condition condition = lock.newCondition();
			// to get around the requirement for final
			final Throwable[] ex = { null };
			lock.lock();
			try {

				Platform.runLater(() -> {
					lock.lock();
					try {
						r.run();
					} catch(Throwable e) {
						ex[0] = e;
					} finally {
						try {
							condition.signal();
						} finally {
							lock.unlock();
						}
					}
				});

				condition.await();

				if(ex[0] != null) {
					// re-throw exception from the runLater thread
					throw ex[0];
				}
			} finally {
				lock.unlock();
			}
		}
	}

	/**
     * Given the name of the thread pool or other executor. This factory gives
     * descriptive names to the threads in that pool
	 */
	public static class NamedThreadFactory implements ThreadFactory {
		private final String poolName;
		private int threadID;

		public NamedThreadFactory(String poolName) {
			this.poolName = poolName;
			threadID = 0;
		}

		@SuppressWarnings("NullableProblems")
		@Override
		public Thread newThread(Runnable runnable) {
			if(runnable == null) runnable = () -> {};

			Thread t = new Thread(runnable, "Executor(" + poolName + "): Thread(" + (threadID++) + ")");
			t.setDaemon(true);
			return t;
		}
	}
}
