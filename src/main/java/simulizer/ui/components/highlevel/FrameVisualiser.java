package simulizer.ui.components.highlevel;

import java.util.Random;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import simulizer.ui.windows.HighLevelVisualisation;

public class FrameVisualiser extends DataStructureVisualiser {

	private ImageView image;
	private int width = 240, height = 160;
	private volatile boolean rendering = false;

	public FrameVisualiser(HighLevelVisualisation vis) {
		super(vis);

		image = new ImageView();
		image.setSmooth(false);
		image.setCache(false);
		vis.add(image);
		commit();
	}

	public void commit() {
		if (!rendering) {
			rendering = true;
			Thread t = new Thread(this::drawFrame, "Render");;
			t.setDaemon(true);
			t.start();
		} else {
			System.out.println("Missed frame");
		}
	}

	private void drawFrame() {
		WritableImage frame = new WritableImage(width, height);
		PixelWriter pw = frame.getPixelWriter();
		Random rand = new Random();
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double r = rand.nextDouble();
				pw.setColor(x, y, new Color(r, r, r, 1));
			}
		}
		image.setImage(frame);
		rendering = false;
	}

	@Override
	public void resize() {
		double windowWidth = vis.getWindowWidth(), windowHeight = vis.getWindowHeight();

		image.setLayoutX(0);
		image.setLayoutY(0);
		image.setFitWidth(windowWidth);
		image.setFitHeight(windowHeight);
	}

}
