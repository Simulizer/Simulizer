package simulizer.ui.windows;

import javafx.scene.layout.Pane;
import simulizer.highlevel.visualisation.DataStructureVisualiser;
import simulizer.highlevel.visualisation.TowerOfHanoiVisualiser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private DataStructureVisualiser visualiser;
	private Pane drawingPane;

	private void init() {
		this.drawingPane = new Pane();
		visualiser = new TowerOfHanoiVisualiser(drawingPane, (int) getWidth(), (int) getHeight(), 0, 100);
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

	@Override
	public void ready() {
		init();
		super.ready();
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		getStylesheets().add(theme.getStyleSheet("highlevel.css"));
	}

}
