package simulizer.ui.windows;

import javafx.scene.layout.Pane;
import simulizer.highlevel.visualisation.DataStructureVisualiser;
import simulizer.highlevel.visualisation.PresentationTowerOfHanoiVisualiser;
import simulizer.highlevel.visualisation.listeners.PresentationTowerOfHanoiListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private DataStructureVisualiser visualiser;
	private Pane drawingPane;

	private void init() {
		this.drawingPane = new Pane();
		this.visualiser = new PresentationTowerOfHanoiVisualiser(drawingPane, (int) getWidth(), (int) getHeight(), 0, 4);
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

	/**
	 * Sets the CPU and adds a listener to the CPU
	 * @param cpu
	 */
	public void setCPU(CPU cpu) {
		cpu.registerListener(new PresentationTowerOfHanoiListener((PresentationTowerOfHanoiVisualiser) visualiser));
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
