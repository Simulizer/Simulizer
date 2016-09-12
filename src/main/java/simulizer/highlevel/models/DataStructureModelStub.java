package simulizer.highlevel.models;

import java.util.*;

/**
 * A stub which implements dummy methods for ALL models. Because of weak typing in Javascript this dummy class can be
 * used to imitate any of the models without causing errors (unless annotations don't check errors correctly,
 * eg calling ListModel::getList() on a stub object will return an empty array)
 *
 * can check in the annotations: var myVis = vis.load('blah');    if(myVis.modelType() == 'STUB') // handle
 *
 * Created by matthew on 12/09/16.
 * @author mbway
 */
public class DataStructureModelStub extends DataStructureModel {

    public DataStructureModelStub() {super(null);}

    @Override
    public void show() { }

    @Override
    public void hide() { }

    @Override
    public boolean isVisible() { return true; }

    @Override
    public ModelType modelType() { return ModelType.STUB; }

    @Override
    protected void printError(String error) { }

    // Frame Model
    public void commit() { }

    // Hanoi Model
    public void setNumDisks(int n) { }
    public void move(int startPeg, int endPeg) { }
    public List<Stack<Integer>> getPegs() { return new ArrayList<>(); }
    public int getNumDiscs() { return 0; }

    // List Model
    public void setList(List<Long> list) { }
    private boolean checkIndex(int index) { return false; }
    public void set(int i, Long item) { }
    public void swap(int i, int j) { }
    public void setMarkers(String markerName, int index) { }
    public void highlightMarker(int index) { }
    public void clearMarker(int index) { }
    public void clearMarkers() { }
    public void emphasise(int index) { }
    public int size() { return 0; }
    public long[] getList() { return new long[0]; }
    public Map<Integer, ArrayList<String>> getMarkers() { return new LinkedHashMap<>(); }
}
