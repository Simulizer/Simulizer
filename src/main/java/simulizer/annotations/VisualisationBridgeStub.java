package simulizer.annotations;

import simulizer.highlevel.models.DataStructureModel;
import simulizer.highlevel.models.DataStructureModelStub;

/**
 * Created by matthew on 12/09/16.
 *
 * @author mbway
 */
@SuppressWarnings("WeakerAccess")
public class VisualisationBridgeStub extends VisualisationBridge {

    @Override
    public DataStructureModel load(String visualisationName) {
        return new DataStructureModelStub();
    }

    @Override
    public DataStructureModel loadHidden(String visualisationName) {
        return new DataStructureModelStub();
    }

    @Override
    public void show() { }

    @Override
    public void hide() { }
}
