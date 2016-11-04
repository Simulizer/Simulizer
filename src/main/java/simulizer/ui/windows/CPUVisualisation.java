package simulizer.ui.windows;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.ui.components.CPU;
import simulizer.ui.components.cpu.listeners.CPUChangedListener;
import simulizer.ui.components.cpu.listeners.CPUListener;
import simulizer.ui.interfaces.InternalWindow;
import simulizer.ui.interfaces.WindowEnum;
import simulizer.ui.theme.Theme;

/**
 * The cpu visualisation window
 * @author Theo Styles
 */
public class CPUVisualisation extends InternalWindow {

    private double width;
	private double height;
	private Pane pane;
	private CPU cpu;
	private CPUListener cpuListener;

	/**
	 * Sets up the cpu visualisation along with the containing pane
	 */
	public CPUVisualisation() {
        width = 530;
        height = 415;
        pane = getContentPane();
        pane.setPrefWidth(width);
        pane.setMinWidth(width);
        pane.setMaxWidth(width);
        pane.setPrefHeight(height);
        pane.setMinHeight(height);
        pane.setMaxHeight(height);
        setMinWidth(width);
        setMinHeight(getMinimalHeight());
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
	 * Sets min, max, and preferred widths for the pane
	 * @param width The width to set
     */
	public void setPaneWidth(double width) {
		pane.setPrefWidth(width);
		pane.setMaxWidth(width);
	}

	/**
	 * Sets min, max and preferred heights for the pane
	 * @param height The height to set
     */
	public void setPaneHeight(double height) {
		pane.setPrefHeight(height);
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

		cpu = new CPU(this);
		cpu.drawCPU();

		getEventManager().addPropertyListener(widthProperty(), (observable, oldValue, newValue) -> {
			width = newValue.doubleValue();
			setPaneWidth(width);
			setPaneHeight(height);
			cpu.resizeShapes();
		});

		getEventManager().addPropertyListener(heightProperty(), (observable, oldValue, newValue) -> {
			height = newValue.doubleValue();
			setPaneHeight(height);
			setPaneWidth(width);
			cpu.resizeShapes();
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
		CPUChangedListener cpuChangedListener = new CPUChangedListener(this);
		getWindowManager().addCPUChangedListener(cpuChangedListener);
		super.ready();
	}

	/**
	 * Opens a required internal window
	 * @param window The window to open
	 * @return The internal window
	 */
	public InternalWindow openWindow(WindowEnum window){
		return getWindowManager().getWorkspace().openInternalWindow(window);
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
		cpuListener = new CPUListener(cpu, simCpu, cpu.animationProcessor);
		cpu.animationProcessor.setCpuListener(cpuListener);
		simCpu.registerListener(cpuListener);
	}
}