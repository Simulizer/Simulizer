package simulizer.ui.windows;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.ui.WindowManager;
import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.theme.Theme;

/**
 * The cpu visualisation window
 */
public class CPUVisualisation extends InternalWindow {

    double width;
    double height;
    Pane pane;
	private CPU cpu;
	private CPUListener cpuListener;

	/**
	 * Sets up the cpu visualisation along with the containing pane
	 */
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
        setMinWidth(width);
        setMinHeight(getMinimalHeight());
		getChildren().add(pane);
		drawVisualisation();
		pane.setCache(true);
		pane.setCacheHint(CacheHint.SPEED);
		setCache(true);
		setCacheHint(CacheHint.SPEED);
	}

	/**
	 * Sets the theme for the window
	 * @param theme The theme to use
     */
    @Override
    public void setTheme(Theme theme) {
        super.setTheme(theme);
        getStylesheets().clear();
        getStylesheets().add(theme.getStyleSheet("window.css"));
        getStylesheets().add(theme.getStyleSheet("cpu.css"));
    }

	/**
	 * Adds a node to the pane
	 * @param node The node to add
     */
    public void add(Node node){
        pane.getChildren().add(node);
    }

	/**
	 * Adds a group of nodes to the pane
	 * @param nodes The group of nodes to add
     */
    public void addAll(Node... nodes){
        pane.getChildren().addAll(nodes);
    }

	/**
	 * Gets the current pane
	 * @return The current pane
     */
	public Pane getPane() {
		return pane;
	}

	/**
	 * Gets the window manager
	 * @return The window manager
     */
	public WindowManager getMainWindowManager(){
		return super.getWindowManager();
	}

	/**
	 * Sets min, max, and preferred widths for the pane
	 * @param width The width to set
     */
	public void setPaneWidth(double width) {
		pane.setPrefWidth(width);
		pane.setMinWidth(width);
		pane.setMaxWidth(width);
	}

	/**
	 * Sets min, max and preferred heights for the pane
	 * @param height The height to set
     */
	public void setPaneHeight(double height) {
		pane.setPrefHeight(height);
		pane.setMinHeight(height);
		pane.setMaxHeight(height);
	}

	/**
	 * Gets the window width
	 * @return The window width
     */
	public double getWindowWidth() {
		return width;
	}

	/**
	 * Gets the window height
	 * @return The window height
     */
	public double getWindowHeight() {
		return height;
	}

	/**
	 * Gets the cpu handling the visualisation
	 * @return The cpu
     */
	public CPU getCpu(){ return cpu; }

	/**
	 * Draws the visualisation and handles resizing of the window
	 */
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

	/**
	 * Gets the minimal height of the window
	 * @return The minimal height
     */
	protected double getMinimalHeight() {
		return 415;
	}

	/**
	 * Called when the window is ready
	 */
	@Override
	public void ready(){
		attachCPU(getWindowManager().getCPU());
		super.ready();
	}

	/**
	 * Called when the window is closed
	 */
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