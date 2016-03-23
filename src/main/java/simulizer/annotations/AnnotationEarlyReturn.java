package simulizer.annotations;

/**
 * An exception thrown when ret() is called from an annotation
 *
 * @author mbway
 */
@SuppressWarnings("unused")
public class AnnotationEarlyReturn extends RuntimeException {
	private static final long serialVersionUID = 8318921246194718607L;

	// thrown when ret() is called

	public AnnotationEarlyReturn() {
	}
}
