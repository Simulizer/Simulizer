package simulizer.highlevel.models;

import simulizer.simulation.cpu.user_interaction.IO;

public class FrameModel extends DataStructureModel {

	public FrameModel(IO io) {
		super(io);
	}

	private double[][] image = new double[240][160];

	public void commit() {
		// TODO: Write FrameModel
		setChanged();
		notifyObservers(new FrameAction());
	}

	@Override
	public ModelType modelType() {
		return ModelType.FRAME;
	}

	public class FrameAction extends ModelAction<double[][]> {

		public FrameAction() {
			super(image, true);
			// TODO Make image a copy
		}

	}

}
