package simulizer.ui.windows;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import simulizer.ui.WindowManager;
import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.GeneralComponent;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

public class CPUVisualisation extends InternalWindow {

    double width;
    double height;
    Pane pane;
	private CPU cpu;
	private CPUListener cpuListener;

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
        setMinWidth(width);
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

	public CPU getCpu(){ return cpu; }

	private void drawVisualisation() {

		cpu = new CPU(this, width, height);
		cpu.drawCPU();

		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				width = newValue.doubleValue();
				setPaneWidth(width);
				setPaneHeight(height);
				setClip(new Rectangle(width, height));
				cpu.resizeShapes();
			}
		});

		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				height = newValue.doubleValue();
				setPaneHeight(height);
				setPaneWidth(width);
				setClip(new Rectangle(width, height));
				cpu.resizeShapes();
			}
		});

	}

	protected double getMinimalHeight() {
		return 415;
	}

	@Override
	public void ready(){
		attachCPU(getWindowManager().getCPU());
		super.ready();
	}
	
	@Override
	public void close() {
		getWindowManager().getCPU().unregisterListener(cpuListener);
		cpu.closeAllThreads();
		super.close();
	}

	/**
	 * Sets the CPU and adds a listener to the CPU
	 *
	 * @param simCpu The simulated cpu
	 */
	public void attachCPU(simulizer.simulation.cpu.components.CPU simCpu) {
		cpuListener = new CPUListener(cpu, simCpu, this);
		simCpu.registerListener(cpuListener);
	}
}
