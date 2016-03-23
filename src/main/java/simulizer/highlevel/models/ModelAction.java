package simulizer.highlevel.models;

public class ModelAction<E> {

	public final E structure;
	public final boolean skipable;

	public ModelAction(E structure, boolean skippable){
		this.structure = structure;
		this.skipable = skippable;
	}

}
