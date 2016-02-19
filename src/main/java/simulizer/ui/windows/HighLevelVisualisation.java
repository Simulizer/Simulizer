package simulizer.ui.windows;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.highlevel.visualisation.DataStructureVisualiser;
import simulizer.highlevel.visualisation.PresentationTowerOfHanoiVisualiser;
import simulizer.highlevel.visualisation.listeners.PresentationTowerOfHanoiListener;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private double width = 600;
	private double height = 350;

	private DataStructureVisualiser visualiser;
	private Pane drawingPane = new Pane();

	private void init() {
		this.drawingPane = new Pane();

		setPaneWidth(width);
		setPaneHeight(height);
		getChildren().add(drawingPane);

		setMinWidth(width);
		setMinHeight(getMinimalHeight());

		// TODO remove this line so that the visualiser is set depending on code
		this.visualiser = new PresentationTowerOfHanoiVisualiser(this, (int) width, (int) height, 0, 4);

		widthProperty().addListener((o, old, newValue) -> {
			setPaneWidth(newValue.doubleValue());
			visualiser.resize();
		});

		heightProperty().addListener((o, old, newValue) -> {
			setPaneHeight(newValue.doubleValue());
			visualiser.resize();
		});
	}

    public void add(Node e){
        drawingPane.getChildren().add(e);
    }

    public void addAll(Node... elements){
        drawingPane.getChildren().addAll(elements);
    }

	public void setPaneWidth(double width) {
		drawingPane.setPrefWidth(width);
		drawingPane.setMinWidth(width);
		drawingPane.setMaxWidth(width);
	}

	public void setPaneHeight(double height) {
		drawingPane.setPrefHeight(height);
		drawingPane.setMinHeight(height);
		drawingPane.setMaxHeight(height);
	}

	public double getWindowWidth() {
		return width;
	}

	public double getWindowHeight() {
		return height;
	}

	@Override
	protected double getMinimalHeight() {
		return 400;
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
	 *
	 * @param cpu
	 */
	public void attachCPU(CPU cpu) {
		cpu.registerListener(new PresentationTowerOfHanoiListener((PresentationTowerOfHanoiVisualiser) visualiser));
		setResizableWindow(false);
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
