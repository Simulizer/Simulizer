package simulizer.simulation.listeners;

import simulizer.assembler.representation.Annotation;

/**
 * A message stating that an annotation has been met
 * @author mbway
 */
public class AnnotationMessage extends Message {
	Annotation a; // the annotation that was met

	public AnnotationMessage(Annotation a) {
		this.a = a;
	}
}
