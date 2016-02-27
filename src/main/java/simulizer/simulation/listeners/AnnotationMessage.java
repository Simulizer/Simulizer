package simulizer.simulation.listeners;

import simulizer.assembler.representation.Annotation;

/**
 * A message stating that an annotation has been met
 * @author mbway
 */
public class AnnotationMessage extends Message {
	public Annotation annotation; // the annotation that was met

	public AnnotationMessage(Annotation annotation) {
		this.annotation = annotation;
	}
}
