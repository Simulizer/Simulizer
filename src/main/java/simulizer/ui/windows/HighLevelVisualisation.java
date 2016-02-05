package simulizer.ui.windows;

import simulizer.highlevel.visualisation.DataStructureVisualiser;
import simulizer.ui.interfaces.InternalWindow;

public class HighLevelVisualisation extends InternalWindow {
	private DataStructureVisualiser visualiser;
	
	public HighLevelVisualisation() {
		
	}
	
	public void setVisualiser(DataStructureVisualiser visualiser) {
		this.visualiser = visualiser;
	}
	
	public DataStructureVisualiser getVisualiser() {
		return this.visualiser;
	}
	
}
