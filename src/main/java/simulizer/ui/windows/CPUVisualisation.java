package simulizer.ui.windows;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import simulizer.cpu.visualisation.CPU;
import simulizer.ui.interfaces.InternalWindow;

public class CPUVisualisation extends InternalWindow {

    int width;
    int height;
    Pane pane;

	public CPUVisualisation() {
        width = 600;
        height = 350;
        pane = new Pane();
        pane.setPrefHeight(height);
        pane.setPrefWidth(width);
        pane.setMinHeight(height);
        pane.setMinWidth(width);
        pane.setMaxHeight(height);
        pane.setMaxWidth(width);
        getChildren().add(pane);
		drawVisualisation();
	}

    @Override
    public void setTheme(String theme) {
        super.setTheme(theme);
        getStylesheets().clear();
        getStylesheets().add(theme + "/window.css");
        getStylesheets().add(theme + "/cpu.css");
    }

    public void add(Node e){
        pane.getChildren().add(e);
    }

    public void addAll(Node... elements){
        pane.getChildren().addAll(elements);
    }

	private void drawVisualisation() {
        CPU cpu = new CPU(this, width, height);
        cpu.drawCPU();
	}
	
	@Override
	protected double getMinimalHeight() {
		return 380;
	}
}
