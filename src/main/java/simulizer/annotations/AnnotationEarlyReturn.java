package simulizer.annotations;

/**
 * An exception thrown when abortAnnotation() is called
 */
@SuppressWarnings("unused")
public class AnnotationEarlyReturn extends RuntimeException {
	// thrown when ret() is called

	public AnnotationEarlyReturn() {
	}
}
