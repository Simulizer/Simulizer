package simulizer.ui.windows;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.ui.WindowManager;
import simulizer.ui.components.CPU;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class CPUVisualisation extends InternalWindow {

    double width;
    double height;
    Pane pane;
	private CPU cpu;

	public CPUVisualisation() {
        width = 530;
        height = 415;
        pane = new Pane();
        pane.setPrefWidth(width);
        pane.setMinWidth(width);
        pane.setMaxWidth(width);
        pane.setPrefHeight(height);
        pane.setMinHeight(height);
        pane.setMaxHeight(height);
        getChildren().add(pane);
        setMinWidth(530);
        setMinHeight(getMinimalHeight());
		drawVisualisation();
	}

    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        getStylesheets().clear();
        getStylesheets().add(theme.getStyleSheet("window.css"));
        getStylesheets().add(theme.getStyleSheet("cpu.css"));
    }

    public void add(Node e){
        pane.getChildren().add(e);
    }

    public void addAll(Node... elements){
        pane.getChildren().addAll(elements);
    }

	public Pane getPane() {
		return pane;
	}

	public WindowManager getMainWindowManager(){
		return super.getWindowManager();
	}

	public void setPaneWidth(double width) {
		pane.setPrefWidth(width);
		pane.setMinWidth(width);
		pane.setMaxWidth(width);
	}

	public void setPaneHeight(double height) {
		pane.setPrefHeight(height);
		pane.setMinHeight(height);
		pane.setMaxHeight(height);
	}

	public double getWindowWidth() {
		return width;
	}

	public double getWindowHeight() {
		return height;
	}

	private void drawVisualisation() {

		cpu = new CPU(this, width, height);
		cpu.drawCPU();

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				width = newValue.doubleValue();
				setPaneWidth(width);
				setPaneHeight(height);
				cpu.resizeShapes();
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				height = newValue.doubleValue();
				setPaneHeight(height);
				setPaneWidth(width);
				cpu.resizeShapes();
			}
		});

	}
	
	@Override
	protected double getMinimalHeight() {
		return 415;
	}
	
	@Override
	public void close() {
		cpu.closeAllThreads();
		super.close();
	}
}
