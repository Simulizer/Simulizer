package simulizer.ui.windows;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.simulation.cpu.components.CPU;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.components.highlevel.ListVisualiser;
import simulizer.ui.components.highlevel.PresentationTowerOfHanoiVisualiser;
import simulizer.ui.components.highlevel.listeners.PresentationTowerOfHanoiListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private double width = 400;
	private double height = 300;

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
		//this.visualiser = new ListVisualiser<Integer>(this, getWindowWidth(), getWindowHeight(), new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7)));

		widthProperty().addListener((o, old, newValue) -> {
			width = newValue.doubleValue();
			setPaneWidth(width);
			visualiser.resize();
		});

		heightProperty().addListener((o, old, newValue) -> {
			height = newValue.doubleValue();
			setPaneHeight(height);
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
		return 200;
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
		this.visualiser = new PresentationTowerOfHanoiVisualiser(this, (int) width, (int) height, 0, 4);
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
