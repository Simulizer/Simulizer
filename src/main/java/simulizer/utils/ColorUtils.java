package simulizer.utils;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import simulizer.assembler.representation.Address;

public class ColorUtils {
	// Reading: http://krazydad.com/tutorials/makecolors.php
	public static Color getColor(Address address) {
		if (address == null) return Color.RED.brighter();
		else return getColor(address.getValue() % 128);
	}

	public static Color getColor(int i) {
		// @formatter:off
		int red   = (int) (Math.sin(.3 * i + 0) * 127) + 128;
		int green = (int) (Math.sin(.3 * i + 2) * 127) + 128;
		int blue  = (int) (Math.sin(.3 * i + 4) * 127) + 128;
		// @formatter:on

		return Color.rgb(red, green, blue);
	}

	// Thanks to http://stackoverflow.com/a/3943023
	public static Paint getTextColor(Color backgroundColor) {
		double r = backgroundColor.getRed() * 255;
		double g = backgroundColor.getGreen() * 255;
		double b = backgroundColor.getBlue() * 255;

		if (r * 0.299 + g * 0.587 + b * 0.114 > 90) return Paint.valueOf("black");
		else return Paint.valueOf("white");
	}
}
