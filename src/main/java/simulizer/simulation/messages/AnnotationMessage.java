package simulizer.simulation.messages;

import simulizer.assembler.representation.Address;
import simulizer.assembler.representation.Annotation;

/**
 * A message stating that an annotation has been met
 * @author mbway
 */
public class AnnotationMessage extends Message {
	public final Annotation annotation; // the annotation that was met
	public final Address boundAddress; // null if initial annotation

	public AnnotationMessage(Annotation annotation, Address boundAddress) {
		this.annotation = annotation;
		this.boundAddress = boundAddress;
	}
}
