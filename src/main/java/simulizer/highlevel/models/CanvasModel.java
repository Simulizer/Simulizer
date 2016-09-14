package simulizer.highlevel.models;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import simulizer.simulation.cpu.user_interaction.IO;

/**
 * Model for accessing a JavaFX canvas from annotations
 * 
 * @author mbway
 *
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class CanvasModel extends DataStructureModel {

	public Canvas canvas;
    public GraphicsContext ctx;
	public Paint clearColor = getColor("black");
	public Paint pixelColor = getColor("#4BE34B");

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

	//TODO: get font
	//TODO: set font rendering type

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

	/**
	 * clear the entire canvas with the clearColor
	 */
	public void clear() {
		ctx.setFill(clearColor);
		ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
	}

	public void drawPixels(boolean[][] pixels) {
		clear();

		int rows = pixels.length;
        if(rows == 0)
        	return; // empty image

		int cols = pixels[0].length;
		if(cols == 0)
			return; // empty image

		int pixelWidth = (int) (canvas.getWidth() / cols);
		int pixelHeight = (int) (canvas.getHeight() / rows);

		ctx.setFill(pixelColor);

		for(int row = 0; row < rows; ++row) {
			for(int col = 0; col < cols; ++col) {
                if(pixels[row][col]) // pixel is lit
                    ctx.fillRect(col*pixelWidth, row*pixelHeight, pixelWidth, pixelHeight);
			}
		}
	}

	@Override
	public ModelType modelType() {
		return ModelType.CANVAS;
	}

}
