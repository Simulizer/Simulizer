package simulizer.highlevel.models;

public class FrameModel extends DataStructureModel {

	private double[][] image = new double[640][480];

	public void commit() {
		// TODO: Write FrameModel
	}

	@Override
	public ModelType modelType() {
		return ModelType.FRAME;
	}

}
