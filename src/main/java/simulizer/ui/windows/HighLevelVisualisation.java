package simulizer.ui.windows;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.ui.components.highlevel.DataStructureVisualiser;
import simulizer.ui.components.highlevel.ListVisualiser;
import simulizer.ui.components.highlevel.TowerOfHanoiVisualiser;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class HighLevelVisualisation extends InternalWindow {
	private double width = 400;
	private double height = 300;

	private DataStructureVisualiser visualiser;
	private Pane drawingPane;

	private void init() {
		this.drawingPane = new Pane();

		// TODO remove this
		setCache(false);

		// TODO check if all this `setXWidth/Height()` stuff is needed
		setPaneWidth(width);
		setPaneHeight(height);
		getChildren().add(drawingPane);

		setMinWidth(width);
		setMinHeight(200);

		widthProperty().addListener((o, old, newValue) -> {
			width = newValue.doubleValue();
			setPaneWidth(width);
			if(visualiser != null) {
				visualiser.resize();
			}
		});

		heightProperty().addListener((o, old, newValue) -> {
			height = newValue.doubleValue();
			setPaneHeight(height);
			if(visualiser != null) {
				visualiser.resize();
			}
		});
	}


	//TODO: have these not be mutually exclusive
	public void loadTowerOfHanoiVisualisation() {
		this.visualiser = new TowerOfHanoiVisualiser(this, 0);
	}
	public void loadListVisualisation() {
		visualiser = new ListVisualiser(this);
	}

    public void add(Node e){
        drawingPane.getChildren().add(e);
    }

    public void addAll(Node... elements){
        drawingPane.getChildren().addAll(elements);
    }

    public void remove(Node e){
        drawingPane.getChildren().remove(e);
    }

    public void removeAll(Node... elements){
        drawingPane.getChildren().removeAll(elements);
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
