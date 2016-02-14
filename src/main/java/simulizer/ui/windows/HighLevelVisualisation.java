package simulizer.ui.windows;

import javafx.scene.layout.Pane;
import simulizer.highlevel.visualisation.DataStructureVisualiser;
import simulizer.ui.interfaces.InternalWindow;

public class HighLevelVisualisation extends InternalWindow {
	private DataStructureVisualiser visualiser;
	private Pane drawingPane;
	
	public HighLevelVisualisation() {
		init();
	}
	
	private void init() {
		this.drawingPane = new Pane();
		getChildren().add(drawingPane);
	}
	
	public void setVisualiser(DataStructureVisualiser visualiser) {
		this.visualiser = visualiser;
	}
	
	public DataStructureVisualiser getVisualiser() {
		return this.visualiser;
	}
	
	public Pane getDrawingPane() {
		return drawingPane;
	}
	
//	TODO Convert this to the new way of setting themes
//	@Override
//	public void setTheme(String theme) {
//		super.setTheme(theme);
//		System.out.println("Adding stylesheet");
//		getStylesheets().add(theme + "/highlevel.css");
//	}

}
