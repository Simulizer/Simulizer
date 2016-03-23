package simulizer.utils;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import simulizer.assembler.representation.Address;

/**
 * Contains a number of static helper methods for colouring activities.
 *
 * @author Kelsey McKenna
 *
 */
public class ColorUtils {
	/**
	 * @param address
	 *            the address for which a color is to be returned
	 * @return a color for the given address.
	 */
	public static Color getColor(Address address) {
		if (address == null) return Color.RED.brighter();
		else return getColor(address.getValue() % 128);
	}

	/**
	 * @param seed
	 *            the seed for generating the color
	 * @return a color using the seed
	 */
	public static Color getColor(int seed) {
		// @formatter:off
		int red   = (int) (Math.sin(.3 * seed + 0) * 127) + 128;
		int green = (int) (Math.sin(.3 * seed + 2) * 127) + 128;
		int blue  = (int) (Math.sin(.3 * seed + 4) * 127) + 128;
		// @formatter:on

		return Color.rgb(red, green, blue);
	}

	/**
	 * Calculates an appropriate text colour based on the background colour.
	 *
	 * @param backgroundColor
	 *            the background colour for the text
	 * @return black if the background colour is sufficiently bright; white otherwise
	 */
	public static Paint getTextColor(Color backgroundColor) {
		// Thanks to http://stackoverflow.com/a/3943023
		double r = backgroundColor.getRed() * 255;
		double g = backgroundColor.getGreen() * 255;
		double b = backgroundColor.getBlue() * 255;

		if (r * 0.299 + g * 0.587 + b * 0.114 > 90) return Paint.valueOf("black");
		else return Paint.valueOf("white");
	}
}
