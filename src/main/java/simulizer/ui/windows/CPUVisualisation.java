package simulizer.ui.windows;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import simulizer.ui.interfaces.InternalWindow;

public class CPUVisualisation extends InternalWindow {

	public CPUVisualisation() {
		Canvas canvas = new Canvas(600, 350);
		GraphicsContext ctx = canvas.getGraphicsContext2D();
		drawVisualisation(ctx);
		//setMinHeight(0.0);
		getContentPane().getChildren().add(canvas);
	}

	private void drawVisualisation(GraphicsContext ctx) {
		Image proc = new Image("processor.png");
		ctx.drawImage(proc, 10, 0, 580, 350);

	}
	
	@Override
	protected double getMinimalHeight() {
		return 380;
	}
}
