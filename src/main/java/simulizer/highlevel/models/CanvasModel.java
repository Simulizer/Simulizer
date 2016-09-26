package simulizer.highlevel.models;

import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import simulizer.simulation.cpu.user_interaction.IO;
import simulizer.utils.CircularIntBuffer;

/**
 * Model for accessing a JavaFX canvas from annotations
 * 
 * @author mbway
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CanvasModel extends DataStructureModel {

	private CircularIntBuffer frameTimes = new CircularIntBuffer(3);
	private long lastFrameMs = 0;

	public Canvas canvas;
    public GraphicsContext ctx;
	public Paint clearColor = getColor("black");
	public Paint pixelColor = getColor("#4BE34B");
	public Paint textColor  = getColor("#4BE34B");
	public boolean squareShaped = false; // make sure canvas is always a square
	public boolean showFPS = false; // show FPS when drawing pixels
    public double maxFPS = Double.POSITIVE_INFINITY;

    // cannot be static otherwise not accessible from javascript
	public final int UP    = 1;
	public final int DOWN  = 1<<1;
	public final int LEFT  = 1<<2;
	public final int RIGHT = 1<<3;
	public final int SPACE = 1<<4;
	/**
	 * A pseudo-register (bit vector) for holding the pressed state for the keys with masks given above
	 * var downIsPressed  = c.input & c.DOWN;
	 * var downIsReleased = c.input ^ c.DOWN;
	 */
	public volatile int input;

	public CanvasModel(IO io) {
		super(io);
		input = 0;

		// these are set by the Visualiser
		canvas = null;
		ctx = null;
	}

	/**
     * get a javaFX Paint for a given string
	 * accepts strings for a single color, and also linear and radial gradients (see Paint.valueOf)
	 * for a single color: accepts the following (see Color.web)
	 * - HTML color name eg 'blue'
	 * - hex eg 0xFF or #AABBCC
	 * - rgb(r, g, b) where r, g, b are 0-255 or 0.0% to 100.0%  or 0.0 to 1.0
	 * - hsl(h, s, l)
	 * @param colorName the string to convert
	 * @return the color given by the string
	 */
	public Paint getColor(String colorName) {
		return Paint.valueOf(colorName);
	}

	public Font getFont(String family, double size) {
		return Font.font(family, size);
	}
	public void setFont(String family, double size) {
		ctx.setFont(Font.font(family, size));
	}
	public void setFont(Font f) {
		ctx.setFont(f);
	}

	/**
	 * clear the entire canvas with the clearColor
	 */
	public void clear() {
		ctx.setFill(clearColor);
		ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}


	public void drawPixels(boolean[] pixels, int cols) {
		assert(pixels.length > 0 && cols > 0 && pixels.length % cols == 0);

		clear();

		int rows = pixels.length / cols;

		int pixelWidth = (int) (canvas.getWidth() / cols);
		int pixelHeight = (int) (canvas.getHeight() / rows);

		ctx.setFill(pixelColor);

		for(int row = 0; row < rows; ++row) {
            int rowOffset = row*cols;
			for(int col = 0; col < cols; ++col) {
                if(pixels[rowOffset + col]) // pixel is lit
                    ctx.fillRect(col*pixelWidth, row*pixelHeight, pixelWidth, pixelHeight);
			}
		}

		submitFrame();
	}

	/**
     * like drawPixels but with a slight border between each pixel
	 * @param margin the spacing around each pixel (eg 0.1 => border of 0.1*dimension between each pixel (each contributing half of the margin))
	 */
	public void drawTiles(boolean[] pixels, int cols, double margin) {
		assert(pixels.length > 0 && cols > 0 && pixels.length % cols == 0 && 0 <= margin && margin <= 1);

		clear();

		int rows = pixels.length / cols;

		int fullPixelWidth = (int) (canvas.getWidth() / cols);
		int fullPixelHeight = (int) (canvas.getHeight() / rows);
		margin /= 2; // to take into account each neighbouring pixel also has a border
        int indentX = (int) (fullPixelWidth * margin);
		int indentY = (int) (fullPixelHeight * margin);
		int pixelWidth  = fullPixelWidth - 2*indentX;
		int pixelHeight = fullPixelHeight - 2*indentY;

		ctx.setFill(pixelColor);

		for(int row = 0; row < rows; ++row) {
			int rowOffset = row*cols;
			for(int col = 0; col < cols; ++col) {
				if(pixels[rowOffset + col]) // pixel is lit
					ctx.fillRect(col*fullPixelWidth+indentX, row*fullPixelHeight+indentY, pixelWidth, pixelHeight);
			}
		}

		submitFrame();
	}


	private void enforceFPSLimit() {
		if(lastFrameMs != 0 && !Double.isInfinite(maxFPS)) { // not the first frame
			int currentMs = (int) (System.currentTimeMillis() - lastFrameMs); // current time for this frame
			int desiredMs = (int) (1000.0/maxFPS);
			int sleepTime = desiredMs - currentMs;
			if(sleepTime > 0) {
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}

	/**
	 * used internally to keep track of frames and display an FPS counter if requested
	 */
	private void submitFrame() {
		if(showFPS) {
			long now = System.currentTimeMillis();
			if(lastFrameMs != 0) {
				frameTimes.add((int) (now - lastFrameMs));
			}
			lastFrameMs = now;

			double fps = 1000.0/frameTimes.mean();

			ctx.setFont(Font.font("monospace", 10));
			ctx.setFill(textColor);
			ctx.setTextAlign(TextAlignment.RIGHT);
			ctx.setTextBaseline(VPos.TOP);
			ctx.fillText(String.format("%.1f FPS", fps), canvas.getWidth(), 0);
		}
		enforceFPSLimit();
	}

	public void centerText(String text) {
		ctx.setFill(textColor);
		ctx.setTextAlign(TextAlignment.CENTER);
		ctx.setTextBaseline(VPos.CENTER);
		ctx.fillText(text, canvas.getWidth()/2, canvas.getHeight()/2);
	}

	@Override
	public ModelType modelType() {
		return ModelType.CANVAS;
	}

}
