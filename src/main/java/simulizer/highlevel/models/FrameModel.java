package simulizer.highlevel.models;

public class FrameModel extends DataStructureModel {

	private double[][] image = new double[240][160];

	public void commit() {
		// TODO: Write FrameModel
		setChanged();
		notifyObservers(image);
	}

	@Override
	public ModelType modelType() {
		return ModelType.FRAME;
	}

}
