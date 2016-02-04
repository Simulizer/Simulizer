package simulizer.ui.windows;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import simulizer.ui.interfaces.InternalWindow;

public class CPUVisualiser extends InternalWindow {

	public CPUVisualiser() {
		Canvas canvas = new Canvas(600, 350);
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		drawVisualisation(ctx);
		getContentPane().getChildren().add(canvas);
	}

	private void drawVisualisation(GraphicsContext ctx) {
		Image proc = new Image("processor.png");
		ctx.drawImage(proc, 10, 0, 580, 350);

	}
}
