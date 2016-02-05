package simulizer.highlevel.visualisation;

import javafx.scene.layout.Region;

public abstract class DataStructureVisualiser {
	private Region region;
	
	public DataStructureVisualiser() {
		
	}
	
	/**
	 * @param region the region on which this visualiser should draw
	 */
	public void setRegion(Region region) {
		this.region = region;
	}
	
	public Region getRegion() {
		return this.region;
	}
	
}
