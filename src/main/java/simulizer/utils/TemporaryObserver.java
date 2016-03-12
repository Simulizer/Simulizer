package simulizer.utils;

/**
 * Represents an observer of another single object. It can respond to messages being fired. It can also stop observing
 * the object.
 *
 * @author Kelsey McKenna
 *
 */
public interface TemporaryObserver {
	/**
	 * Update with whatever actions are suitable.
	 */
	void update();

	/**
	 * Stop observing the object.
	 */
	void stopObserving();
}
