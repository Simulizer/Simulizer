package simulizer.ui.components.highlevel;

import java.util.Random;

import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import simulizer.highlevel.models.FrameModel;
import simulizer.highlevel.models.ModelAction;
import simulizer.ui.windows.HighLevelVisualisation;

public class FrameVisualiser extends DataStructureVisualiser {

	private ImageView image;
	private int width = 240, height = 160;

	public FrameVisualiser(FrameModel model, HighLevelVisualisation vis) {
		super(model, vis);

		image = new ImageView();
		image.setSmooth(false);
		image.setCache(false);
		getChildren().add(image);
	}

	@Override
	public void repaint() {
		double windowWidth = vis.getWindowWidth(), windowHeight = vis.getWindowHeight();

		image.setLayoutX(0);
		image.setLayoutY(0);
		image.setFitWidth(windowWidth);
		image.setFitHeight(windowHeight);
	}

	@Override
	public String getName() {
		return "Frame";
	}

	@Override
	public void processChange(ModelAction<?> action) {
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
	}

}
