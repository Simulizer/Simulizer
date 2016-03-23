package simulizer.highlevel.models;

/**
 * Model action tells the visualisation what has changed
 * 
 * @author Michael
 *
 * @param <E>
 *            the type of the data structure
 */
public class ModelAction<E> {

	public final E structure;
	public final boolean skipable;

	/**
	 * @param structure
	 *            a copy of the data structure
	 * @param skippable
	 *            whether this change can be skipped
	 */
	public ModelAction(E structure, boolean skippable) {
		this.structure = structure;
		this.skipable = skippable;
	}

}
